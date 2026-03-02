package com.pida.scheduler

import com.pida.notification.rain.RainForecastNotificationService
import com.pida.support.extension.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * 저녁 푸시 알림 오케스트레이터
 *
 * 매일 18:00에 실행되며 분산 락 아래에서 실행 순서를 고정합니다.
 */
@Component
class EveningNotificationScheduler(
    private val notificationExecutionLock: NotificationExecutionLock,
    private val weekdayNotificationScheduler: WeekdayNotificationScheduler,
    private val rainForecastNotificationService: RainForecastNotificationService,
) {
    private val logger by logger()

    @Scheduled(cron = "0 0 18 * * *")
    fun executeEveningNotifications() {
        notificationExecutionLock.runWithEveningLock {
            executeInOrder(LocalDate.now())
        }
    }

    private fun executeInOrder(today: LocalDate) {
        if (isWeekday(today.dayOfWeek)) {
            weekdayNotificationScheduler.executeWeekdayNotification(today)
        } else {
            logger.info("Skipping weekday notification flow on weekend: ${today.dayOfWeek}")
        }

        runSchedulerSafely(
            logger = logger,
            failureMessage = "Failed to execute rain forecast notification scheduler",
        ) {
            rainForecastNotificationService.sendRainForecastNotifications()
        }
    }

    private fun isWeekday(dayOfWeek: DayOfWeek): Boolean =
        dayOfWeek in
            setOf(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY,
            )
}
