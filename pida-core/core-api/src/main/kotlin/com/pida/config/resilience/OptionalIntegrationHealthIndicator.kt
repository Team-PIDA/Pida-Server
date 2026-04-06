package com.pida.config.resilience

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component("optionalIntegrations")
class OptionalIntegrationHealthIndicator(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    private val stateTracker: ExternalDependencyStateTracker,
) : HealthIndicator {
    override fun health(): Health {
        val breakerStates =
            circuitBreakerRegistry
                .allCircuitBreakers
                .associate { it.name to it.state.name }
                .filterValues { it != CircuitBreaker.State.CLOSED.name && it != CircuitBreaker.State.DISABLED.name }

        val degradedDependencies = stateTracker.snapshot()

        if (breakerStates.isEmpty() && degradedDependencies.isEmpty()) {
            return Health.up().build()
        }

        return Health
            .status("DEGRADED")
            .withDetail("circuitBreakers", breakerStates)
            .withDetail(
                "dependencies",
                degradedDependencies.map { status ->
                    mapOf(
                        "provider" to status.dependency.id,
                        "reason" to status.reason,
                        "lastError" to status.lastError,
                        "updatedAt" to status.updatedAt.toString(),
                        "fallbackCount" to status.fallbackCount,
                    )
                },
            ).build()
    }
}
