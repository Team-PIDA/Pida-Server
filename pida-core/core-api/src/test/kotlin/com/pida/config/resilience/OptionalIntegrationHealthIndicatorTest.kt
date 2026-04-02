package com.pida.config.resilience

import com.pida.support.resilience.ExternalDependency
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.kotest.matchers.shouldBe
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.junit.jupiter.api.Test

class OptionalIntegrationHealthIndicatorTest {
    @Test
    fun `degraded dependency가 있으면 DEGRADED 상태를 반환한다`() {
        val circuitBreakerRegistry = CircuitBreakerRegistry.ofDefaults()
        val stateTracker = ExternalDependencyStateTracker(SimpleMeterRegistry())
        val indicator = OptionalIntegrationHealthIndicator(circuitBreakerRegistry, stateTracker)

        circuitBreakerRegistry.circuitBreaker(ExternalDependency.AWS_S3.id).transitionToOpenState()
        stateTracker.recordFallback(
            dependency = ExternalDependency.AWS_S3,
            reason = "empty-image-list",
            throwable = IllegalStateException("s3 down"),
        )

        val health = indicator.health()

        health.status.code shouldBe "DEGRADED"
    }
}
