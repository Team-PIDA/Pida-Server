package com.pida.config.resilience

import com.pida.support.resilience.ExternalDependency
import com.pida.support.resilience.ExternalDependencyPolicy
import io.github.resilience4j.bulkhead.Bulkhead
import io.github.resilience4j.bulkhead.BulkheadRegistry
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.springframework.stereotype.Component

@Component
class ResilienceExternalDependencyPolicy(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    private val bulkheadRegistry: BulkheadRegistry,
    private val stateTracker: ExternalDependencyStateTracker,
) : ExternalDependencyPolicy {
    override fun isAvailable(dependency: ExternalDependency): Boolean {
        val circuitBreaker = circuitBreakerRegistry.circuitBreaker(dependency.id)
        val bulkhead = bulkheadRegistry.bulkhead(dependency.id)

        return circuitBreaker.state !in unavailableStates &&
            bulkhead.metrics.availableConcurrentCalls > 0
    }

    override fun <T> execute(
        dependency: ExternalDependency,
        block: () -> T,
    ): T = executeInternal(dependency, block)

    override suspend fun <T> executeSuspend(
        dependency: ExternalDependency,
        block: suspend () -> T,
    ): T = executeSuspendInternal(dependency, block)

    override fun recordFallback(
        dependency: ExternalDependency,
        reason: String,
        throwable: Throwable?,
    ) {
        stateTracker.recordFallback(dependency, reason, throwable)
    }

    private fun <T> executeInternal(
        dependency: ExternalDependency,
        block: () -> T,
    ): T {
        val bulkhead = bulkheadRegistry.bulkhead(dependency.id)
        val circuitBreaker = circuitBreakerRegistry.circuitBreaker(dependency.id)

        acquireBulkheadAndCircuitBreaker(bulkhead, circuitBreaker)
        val startedAt = circuitBreaker.currentTimestamp

        return try {
            block().also {
                circuitBreaker.onSuccess(circuitBreaker.currentTimestamp - startedAt, circuitBreaker.timestampUnit)
                stateTracker.clear(dependency)
            }
        } catch (throwable: Throwable) {
            circuitBreaker.onError(circuitBreaker.currentTimestamp - startedAt, circuitBreaker.timestampUnit, throwable)
            throw throwable
        } finally {
            bulkhead.onComplete()
        }
    }

    private suspend fun <T> executeSuspendInternal(
        dependency: ExternalDependency,
        block: suspend () -> T,
    ): T {
        val bulkhead = bulkheadRegistry.bulkhead(dependency.id)
        val circuitBreaker = circuitBreakerRegistry.circuitBreaker(dependency.id)

        acquireBulkheadAndCircuitBreaker(bulkhead, circuitBreaker)
        val startedAt = circuitBreaker.currentTimestamp

        return try {
            block().also {
                circuitBreaker.onSuccess(circuitBreaker.currentTimestamp - startedAt, circuitBreaker.timestampUnit)
                stateTracker.clear(dependency)
            }
        } catch (throwable: Throwable) {
            circuitBreaker.onError(circuitBreaker.currentTimestamp - startedAt, circuitBreaker.timestampUnit, throwable)
            throw throwable
        } finally {
            bulkhead.onComplete()
        }
    }

    private fun acquireBulkheadAndCircuitBreaker(
        bulkhead: Bulkhead,
        circuitBreaker: CircuitBreaker,
    ) {
        bulkhead.acquirePermission()
        try {
            circuitBreaker.acquirePermission()
        } catch (throwable: Throwable) {
            bulkhead.releasePermission()
            throw throwable
        }
    }

    companion object {
        private val unavailableStates =
            setOf(
                CircuitBreaker.State.OPEN,
                CircuitBreaker.State.FORCED_OPEN,
            )
    }
}
