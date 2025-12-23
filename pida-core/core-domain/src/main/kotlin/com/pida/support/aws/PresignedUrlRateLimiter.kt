package com.pida.support.aws

import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import io.github.bucket4j.Bucket
import io.github.bucket4j.ConsumptionProbe
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Component
class PresignedUrlRateLimiter {
    private val buckets = ConcurrentHashMap<Long, Bucket>()

    fun consumeToken(userId: Long) {
        val probe =
            resolveBucket(userId)
                .tryConsumeAndReturnRemaining(1)

        if (!probe.isConsumed) {
            throw ErrorException(
                ErrorType.EXCEED_RATE_LIMIT,
                RateLimitMeta(retryAfterSeconds(probe)),
            )
        }
    }

    private fun retryAfterSeconds(probe: ConsumptionProbe): Long = TimeUnit.NANOSECONDS.toSeconds(probe.nanosToWaitForRefill)

    fun resolveBucket(userId: Long): Bucket =
        buckets.computeIfAbsent(userId) {
            Bucket
                .builder()
                .addLimit(PresignedUrlRateLimitPolicy.PER_MINUTE)
                .build()
        }
}
