package com.pida.scheduler

import com.pida.notification.weekend.WeekendNotificationService
import com.pida.support.extension.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * 주말 힐링 푸시 알림 스케줄러
 *
 * 매주 토요일 또는 일요일 중 1회, 오전 10시에 실행
 */
@Component
class WeekendNotificationScheduler(
    private val weekendNotificationService: WeekendNotificationService,
) {
    private val logger by logger()

    // 마지막 실행 주차 (중복 실행 방지)
    private var lastExecutedWeekKey: Int? = null

    /**
     * 토요일/일요일 오전 10시 실행
     */
    @Scheduled(cron = "0 0 10 ? * SAT,SUN")
    fun executeWeekendNotification() = executeIfNotThisWeek(LocalDate.now())

    /**
     * 이번 주에 아직 실행되지 않았다면 주차 키를 기준으로 실행 요일을 결정해 1회 실행
     *
     * @param today 실행 날짜
     */
    private fun executeIfNotThisWeek(today: LocalDate) {
        val currentWeekKey = today.toWeekKey()

        // 이번 주에 이미 실행되었는지 확인
        if (lastExecutedWeekKey == currentWeekKey) {
            return
        }

        val day = today.dayOfWeek
        val chosenDay = chooseExecutionDay(currentWeekKey)

        if (day == chosenDay) {
            val executed =
                runSchedulerSafely(
                    logger = logger,
                    failureMessage = "Failed to execute weekend notification scheduler",
                ) {
                    weekendNotificationService.sendWeekendNotifications()
                }

            if (executed) {
                lastExecutedWeekKey = currentWeekKey
            }
        } else {
            logger.info("Skipped weekend notification on $day (chosen day: $chosenDay)")
        }
    }

    private fun chooseExecutionDay(weekKey: Int): DayOfWeek =
        if (weekKey % 2 == 0) {
            DayOfWeek.SATURDAY
        } else {
            DayOfWeek.SUNDAY
        }
}
