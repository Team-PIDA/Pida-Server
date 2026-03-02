package com.pida.scheduler

import com.pida.notification.bloomed.BloomedNotificationService
import com.pida.support.extension.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * 만개했어요 알림 스케줄러
 *
 * 매일 오전 9시에 실행하여 BLOOMED 비율 80% 이상 지역 알림을 발송합니다.
 * 하루 1회만 실행되도록 중복 실행을 방지합니다.
 */
@Component
class BloomedNotificationScheduler(
    private val bloomedNotificationService: BloomedNotificationService,
) : DailyNotificationScheduler() {
    private val logger by logger()

    @Scheduled(cron = "0 0 9 * * *")
    fun executeBloomedNotification() =
        executeOncePerDay(
            logger = logger,
            failureMessage = "Failed to execute bloomed notification scheduler",
        ) {
            bloomedNotificationService.sendBloomedNotifications()
        }
}
