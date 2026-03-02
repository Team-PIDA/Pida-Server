package com.pida.notification.rain

import com.pida.notification.EligibleUser
import com.pida.notification.NotificationStoredRepository
import com.pida.notification.NotificationType
import com.pida.notification.weekend.WeekendNotificationUserReader
import com.pida.support.extension.logger
import com.pida.weather.WeatherLocation
import com.pida.weather.WeatherService
import org.springframework.stereotype.Component
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

/**
 * 비 예보 알림 대상 사용자 적격성 검증 컴포넌트
 */
@Component
class RainForecastNotificationEligibilityChecker(
    private val userReader: WeekendNotificationUserReader,
    private val weatherService: WeatherService,
    private val notificationStoredRepository: NotificationStoredRepository,
) {
    private val logger by logger()

    companion object {
        private const val RAIN_PROBABILITY_THRESHOLD = 60
    }

    /**
     * 조건을 모두 만족하는 사용자 조회
     *
     * 조건:
     * - 최근 30일 내 활성 사용자
     * - 위치 정보가 있는 사용자
     * - 내일 기준 사용자 위치 격자(nx, ny)의 최대 POP가 60 이상
     * - 당일 RAIN_FORECAST_ALERT 외 다른 푸시를 받지 않은 사용자
     * - 당주(월요일 시작) RAIN_FORECAST_ALERT를 받지 않은 사용자
     */
    fun findEligibleUsers(): List<EligibleUser> {
        val activeUsers = userReader.findActiveUsersWithLocation()

        if (activeUsers.isEmpty()) {
            logger.info("No active users found for rain forecast notification")
            return emptyList()
        }

        val usersWithWeatherLocation =
            activeUsers.map { user ->
                UserWithWeatherLocation(
                    user = user,
                    weatherLocation = WeatherLocation.fromCoordinates(user.latitude, user.longitude),
                )
            }

        val usersByGrid =
            usersWithWeatherLocation
                .groupBy { userWithLocation ->
                    val location = userWithLocation.weatherLocation
                    GridKey(location.nx, location.ny)
                }

        logger.info("Found ${usersByGrid.size} unique weather grids from ${activeUsers.size} active users")

        val rainForecastGridKeys =
            usersByGrid.keys
                .filter { key ->
                    val weatherLocation = usersByGrid.getValue(key).first().weatherLocation
                    runCatching {
                        weatherService.willRainTomorrow(weatherLocation, RAIN_PROBABILITY_THRESHOLD)
                    }.onFailure { error ->
                        logger.warn(
                            "Failed to check rain forecast for nx=${key.nx}, ny=${key.ny}. Users in this grid will be excluded.",
                            error,
                        )
                    }.getOrDefault(false)
                }.toSet()

        if (rainForecastGridKeys.isEmpty()) {
            logger.info("No grids satisfy tomorrow rain forecast threshold")
            return emptyList()
        }

        val rainForecastUsers =
            usersByGrid
                .filterKeys { it in rainForecastGridKeys }
                .values
                .flatten()
                .map { it.user }
                .distinctBy { it.userId }

        if (rainForecastUsers.isEmpty()) {
            return emptyList()
        }

        val usersExcludedByOtherNotificationsToday = findUsersWithOtherNotificationsToday(rainForecastUsers)

        val eligibleByTodayCondition =
            rainForecastUsers.filter { user ->
                user.userId !in usersExcludedByOtherNotificationsToday
            }

        if (eligibleByTodayCondition.isEmpty()) {
            logger.info("All users were excluded by daily other-notification condition")
            return emptyList()
        }

        val usersExcludedByWeeklyRainNotification = findUsersWithWeeklyRainNotification(eligibleByTodayCondition)

        val eligibleUsers =
            eligibleByTodayCondition.filter { user ->
                user.userId !in usersExcludedByWeeklyRainNotification
            }

        logger.info("${eligibleUsers.size} users are eligible for rain forecast notification")

        return eligibleUsers
    }

    private fun findUsersWithOtherNotificationsToday(users: List<EligibleUser>): Set<Long> {
        val todayStart = LocalDate.now().atStartOfDay()
        val userIds = users.map { it.userId }

        val otherNotificationCountMap =
            notificationStoredRepository.countByUserIdsAndTypeNotAndCreatedAtAfter(
                userIds = userIds,
                excludedType = NotificationType.RAIN_FORECAST_ALERT,
                createdAtAfter = todayStart,
            )

        return otherNotificationCountMap
            .filterValues { count -> count > 0L }
            .keys
    }

    private fun findUsersWithWeeklyRainNotification(users: List<EligibleUser>): Set<Long> {
        val weekStartDateTime = getWeekStartDateTime()
        val userIds = users.map { it.userId }

        val rainNotificationCountMap =
            notificationStoredRepository.countByUserIdsAndTypeAndCreatedAtAfter(
                userIds = userIds,
                type = NotificationType.RAIN_FORECAST_ALERT,
                createdAtAfter = weekStartDateTime,
            )

        return rainNotificationCountMap
            .filterValues { count -> count > 0L }
            .keys
    }

    private fun getWeekStartDateTime(): LocalDateTime =
        LocalDateTime
            .now()
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .toLocalDate()
            .atStartOfDay()

    private data class GridKey(
        val nx: Int,
        val ny: Int,
    )

    private data class UserWithWeatherLocation(
        val user: EligibleUser,
        val weatherLocation: WeatherLocation,
    )
}
