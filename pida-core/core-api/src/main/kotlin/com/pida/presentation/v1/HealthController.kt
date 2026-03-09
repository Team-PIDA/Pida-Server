package com.pida.presentation.v1

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Tag(name = "\uD83C\uDFC3 Health Check", description = "서버 상태 확인 API")
@RestController
class HealthController {
    @GetMapping("/ping")
    @Operation(summary = "서버 상태 확인", description = "서버 상태를 확인합니다.")
    fun healthCheck(): PongResponse = PongResponse(LocalDateTime.now())

    @PostMapping("/sentry-test")
    @Operation(summary = "Sentry 알림 테스트", description = "Sentry 알림 확인을 위해 의도적으로 RuntimeException을 발생시킵니다.")
    fun sentryTest(
        @RequestParam(required = false) message: String?,
        @RequestBody(required = false) body: SentryTestRequest?,
    ): Nothing {
        throw RuntimeException(
            "Sentry 테스트 에러 발생! " +
                "param.message=$message, " +
                "body=$body",
        )
    }

    data class PongResponse(
        val now: LocalDateTime,
    )

    data class SentryTestRequest(
        val key: String? = null,
        val value: String? = null,
    )
}
