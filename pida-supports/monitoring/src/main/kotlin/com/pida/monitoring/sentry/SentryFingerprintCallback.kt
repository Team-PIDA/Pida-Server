package com.pida.monitoring.sentry

import com.pida.support.error.AuthenticationErrorException
import com.pida.support.error.ErrorException
import io.sentry.Hint
import io.sentry.SentryEvent
import io.sentry.SentryOptions
import org.springframework.stereotype.Component

@Component
class SentryFingerprintCallback : SentryOptions.BeforeSendCallback {
    override fun execute(
        event: SentryEvent,
        hint: Hint,
    ): SentryEvent {
        val exception = event.throwable ?: return event

        val fingerprint = resolveFingerprint(exception)
        if (fingerprint != null) {
            event.fingerprints = fingerprint
        }

        return event
    }

    private fun resolveFingerprint(exception: Throwable): List<String>? =
        when (exception) {
            is ErrorException ->
                listOf(
                    "ErrorException",
                    exception.errorType.kind.name,
                    exception.errorType.name,
                )

            is AuthenticationErrorException ->
                listOf(
                    "AuthenticationErrorException",
                    exception.authenticationErrorType.kind.name,
                    exception.authenticationErrorType.name,
                )

            else -> null
        }
}
