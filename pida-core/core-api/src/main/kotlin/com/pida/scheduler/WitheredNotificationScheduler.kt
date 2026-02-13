package com.pida.scheduler

import com.pida.notification.withered.WitheredNotificationService
import com.pida.support.extension.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * 저물었어요 알림 스케줄러
 *
 * 매일 오전 9시에 실행하여 WITHERED 상태 알림을 발송합니다.
 * 하루 1회만 실행되도록 중복 실행을 방지합니다.
 */
@Component
class WitheredNotificationScheduler(
    private val witheredNotificationService: WitheredNotificationService,
) {
    private val logger by logger()

    // 마지막 실행 날짜 (중복 실행 방지)
    private var lastExecutedDate: LocalDate? = null

    /**
     * 매일 오전 9시 실행
     *
     * WITHERED 비율이 30% 이상인 지역의 사용자들에게 알림 발송
     */
    @Scheduled(cron = "0 0 9 * * *")
    fun executeWitheredNotification() {
        val today = LocalDate.now()

        // 중복 실행 방지: 오늘 이미 실행되었으면 스킵
        if (lastExecutedDate == today) {
            return
        }

        try {
            witheredNotificationService.sendWitheredNotifications()
            lastExecutedDate = today
        } catch (e: Exception) {
            logger.error("Failed to execute withered notification scheduler", e)
        }
    }
}
