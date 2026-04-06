package com.pida.config.resilience

import com.pida.support.resilience.ExternalDependency
import java.time.Instant

internal data class ExternalDependencyState(
    val dependency: ExternalDependency,
    val reason: String,
    val lastError: String?,
    val updatedAt: Instant,
    val fallbackCount: Long,
)
