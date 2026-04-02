package com.pida.support.resilience

interface ExternalDependencyPolicy {
    fun isAvailable(dependency: ExternalDependency): Boolean

    fun <T> execute(
        dependency: ExternalDependency,
        block: () -> T,
    ): T

    suspend fun <T> executeSuspend(
        dependency: ExternalDependency,
        block: suspend () -> T,
    ): T

    fun recordFallback(
        dependency: ExternalDependency,
        reason: String,
        throwable: Throwable? = null,
    )
}
