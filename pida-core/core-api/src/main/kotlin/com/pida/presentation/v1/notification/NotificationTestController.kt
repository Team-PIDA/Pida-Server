package com.pida.presentation.v1.notification

import com.pida.notification.bloomed.BloomedNotificationService
import com.pida.notification.bloomedevent.BloomedEventNotificationService
import com.pida.notification.bloomedspot.BloomedSpotNotificationService
import com.pida.notification.rain.RainForecastNotificationService
import com.pida.notification.weekday.WeekdayNotificationService
import com.pida.notification.weekend.WeekendNotificationService
import com.pida.notification.withered.WitheredNotificationService
import com.pida.support.geo.Region
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Tag(name = "🧪 Test", description = "테스트용 API")
@RestController
@RequestMapping("/test")
class NotificationTestController(
    private val weekendNotificationService: WeekendNotificationService,
    private val weekdayNotificationService: WeekdayNotificationService,
    private val witheredNotificationService: WitheredNotificationService,
    private val bloomedNotificationService: BloomedNotificationService,
    private val bloomedSpotNotificationService: BloomedSpotNotificationService,
    private val bloomedEventNotificationService: BloomedEventNotificationService,
    private val rainForecastNotificationService: RainForecastNotificationService,
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

    @PostMapping("/bloomed-notification")
    @Operation(
        summary = "만개했어요 알림 수동 트리거",
        description =
            "BLOOMED 상태 푸시 알림을 수동으로 발송합니다. (테스트용)\n\n" +
                "입력한 지역의 사용자들에게 BLOOMED 알림을 수동 발송합니다.",
    )
    fun triggerBloomedNotification(
        @RequestParam region: Region,
    ): NotificationTriggerResponse {
        val startTime = LocalDateTime.now()

        bloomedNotificationService.sendBloomedNotificationForRegion(region)

        return NotificationTriggerResponse(
            message = "Bloomed notification triggered successfully",
            triggeredAt = startTime,
        )
    }

    @PostMapping("/bloomed-spot-notification")
    @Operation(
        summary = "벚꽃길 만개 이벤트 알림 수동 트리거",
        description =
            "특정 벚꽃길의 BLOOMED 투표 이벤트 알림을 수동 발송합니다. (테스트용)\n\n" +
                "조건: 반경 3km 내 위치 권한 허용 사용자\n" +
                "제외: 당일 이미 푸시 수신 사용자, 해당 벚꽃길 시즌 내 알림 기수신 사용자",
    )
    fun triggerBloomedSpotNotification(
        @RequestParam spotId: Long,
    ): NotificationTriggerResponse {
        val startTime = LocalDateTime.now()

        bloomedSpotNotificationService.sendBloomedSpotNotification(spotId)

        return NotificationTriggerResponse(
            message = "Bloomed spot notification triggered successfully",
            triggeredAt = startTime,
        )
    }

    @PostMapping("/bloomed-event-notification")
    @Operation(
        summary = "꽃 이벤트 만개 알림 수동 트리거",
        description =
            "특정 꽃 이벤트의 BLOOMED 투표 이벤트 알림을 수동 발송합니다. (테스트용)\n\n" +
                "조건: 반경 3km 내 위치 권한 허용 사용자\n" +
                "제외: 당일 이미 푸시 수신 사용자, 해당 꽃 이벤트 시즌 내 알림 기수신 사용자",
    )
    fun triggerBloomedEventNotification(
        @RequestParam eventId: Long,
    ): NotificationTriggerResponse {
        val startTime = LocalDateTime.now()

        bloomedEventNotificationService.sendBloomedEventNotification(eventId)

        return NotificationTriggerResponse(
            message = "Bloomed event notification triggered successfully",
            triggeredAt = startTime,
        )
    }

    @PostMapping("/rain-forecast-notification")
    @Operation(
        summary = "비 예보 알림 수동 트리거",
        description =
            "내일 POP(강수확률) 최대값이 60 이상인 위치의 사용자에게 비 예보 알림을 수동 발송합니다. (테스트용)\n\n" +
                "제외 조건: 당일 다른 푸시 수신 사용자, 당주 이미 비 예보 알림 수신 사용자",
    )
    fun triggerRainForecastNotification(): NotificationTriggerResponse {
        val startTime = LocalDateTime.now()

        rainForecastNotificationService.sendRainForecastNotifications()

        return NotificationTriggerResponse(
            message = "Rain forecast notification triggered successfully",
            triggeredAt = startTime,
        )
    }

    data class NotificationTriggerResponse(
        val message: String,
        val triggeredAt: LocalDateTime,
    )
}
