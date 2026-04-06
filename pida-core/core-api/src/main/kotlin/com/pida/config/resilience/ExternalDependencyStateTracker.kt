package com.pida.config.resilience

import com.pida.support.resilience.ExternalDependency
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Component
class ExternalDependencyStateTracker(
    meterRegistry: MeterRegistry,
) {
    private val states = ConcurrentHashMap<ExternalDependency, ExternalDependencyState>()
    private val fallbackCounters =
        ExternalDependency.entries.associateWith { dependency ->
            Counter
                .builder("pida.external.dependency.fallback")
                .tag("provider", dependency.id)
                .register(meterRegistry)
        }

    internal fun clear(dependency: ExternalDependency) {
        states.remove(dependency)
    }

    internal fun recordFallback(
        dependency: ExternalDependency,
        reason: String,
        throwable: Throwable? = null,
    ) {
        fallbackCounters.getValue(dependency).increment()
        states.compute(dependency) { _, current ->
            ExternalDependencyState(
                dependency = dependency,
                reason = reason,
                lastError = throwable?.javaClass?.simpleName,
                updatedAt = Instant.now(),
                fallbackCount = (current?.fallbackCount ?: 0L) + 1,
            )
        }
    }

    internal fun snapshot(): List<ExternalDependencyState> = states.values.sortedBy { it.dependency.id }
}
