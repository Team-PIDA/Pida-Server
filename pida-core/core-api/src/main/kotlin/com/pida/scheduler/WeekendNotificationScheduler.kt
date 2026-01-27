package com.pida.scheduler

import com.pida.notification.weekend.WeekendNotificationService
import com.pida.support.extension.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoField

/**
 * 주말 힐링 푸시 알림 스케줄러
 *
 * 매주 토요일 또는 일요일 중 랜덤으로 1회 오전 10시에 실행
 */
@Component
class WeekendNotificationScheduler(
    private val weekendNotificationService: WeekendNotificationService,
) {
    private val logger by logger()

    // 마지막 실행 주차 (중복 실행 방지)
    private var lastExecutedWeek: Int? = null

    /**
     * 토요일 오전 10시 실행
     */
    @Scheduled(cron = "0 0 10 ? * SAT")
    fun saturdayCheck() {
        executeIfNotThisWeek(DayOfWeek.SATURDAY)
    }

    /**
     * 일요일 오전 10시 실행
     */
    @Scheduled(cron = "0 0 10 ? * SUN")
    fun sundayCheck() {
        executeIfNotThisWeek(DayOfWeek.SUNDAY)
    }

    /**
     * 이번 주에 아직 실행되지 않았다면 50% 확률로 실행
     *
     * @param day 실행 요일
     */
    private fun executeIfNotThisWeek(day: DayOfWeek) {
        val today = LocalDate.now()
        val currentWeek = today.get(ChronoField.ALIGNED_WEEK_OF_YEAR)
        val currentYear = today.year
        val weekKey = currentYear * 100 + currentWeek

        // 이번 주에 이미 실행되었는지 확인
        if (lastExecutedWeek == weekKey) {
            return
        }

        // Deterministically choose Saturday or Sunday based on week
        val chosenDay = if (weekKey % 2 == 0) DayOfWeek.SATURDAY else DayOfWeek.SUNDAY

        if (day == chosenDay) {
            weekendNotificationService.sendWeekendNotifications()
            lastExecutedWeek = weekKey
        } else {
            logger.info("Skipped weekend notification on $day (random selection, week: $currentWeek)")
        }
    }
}
