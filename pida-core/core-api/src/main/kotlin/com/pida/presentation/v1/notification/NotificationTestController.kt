package com.pida.presentation.v1.notification

import com.pida.notification.weekday.WeekdayNotificationService
import com.pida.notification.weekend.WeekendNotificationService
import com.pida.notification.withered.WitheredNotificationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Tag(name = "🧪 Test", description = "테스트용 API")
@RestController
@RequestMapping("/test")
class NotificationTestController(
    private val weekendNotificationService: WeekendNotificationService,
    private val weekdayNotificationService: WeekdayNotificationService,
    private val witheredNotificationService: WitheredNotificationService,
) {
    @PostMapping("/weekend-notification")
    @Operation(
        summary = "주말 알림 수동 트리거",
        description = "주말 힐링 푸시 알림을 수동으로 발송합니다. (테스트용)",
    )
    fun triggerWeekendNotification(): NotificationTriggerResponse {
        val startTime = LocalDateTime.now()

        weekendNotificationService.sendWeekendNotifications()

        return NotificationTriggerResponse(
            message = "Weekend notification triggered successfully",
            triggeredAt = startTime,
        )
    }

    @PostMapping("/weekday-notification")
    @Operation(
        summary = "평일 알림 수동 트리거",
        description = "평일 힐링 푸시 알림을 수동으로 발송합니다. (테스트용)",
    )
    fun triggerWeekdayNotification(): NotificationTriggerResponse {
        val startTime = LocalDateTime.now()

        weekdayNotificationService.sendWeekdayNotifications()

        return NotificationTriggerResponse(
            message = "Weekday notification triggered successfully",
            triggeredAt = startTime,
        )
    }

    @PostMapping("/withered-notification")
    @Operation(
        summary = "저물었어요 알림 수동 트리거",
        description =
            "WITHERED 상태 푸시 알림을 수동으로 발송합니다. (테스트용)\n\n" +
                "각 지역별 WITHERED 투표 비율을 확인하여 30% 이상인 지역의 사용자들에게 알림을 발송합니다.",
    )
    fun triggerWitheredNotification(): NotificationTriggerResponse {
        val startTime = LocalDateTime.now()

        witheredNotificationService.sendWitheredNotifications()

        return NotificationTriggerResponse(
            message = "Withered notification triggered successfully",
            triggeredAt = startTime,
        )
    }

    data class NotificationTriggerResponse(
        val message: String,
        val triggeredAt: LocalDateTime,
    )
}
