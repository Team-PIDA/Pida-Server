package com.pida.support.aws

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import io.github.bucket4j.Bucket
import io.github.bucket4j.ConsumptionProbe
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class PresignedUrlRateLimiter {
    private val buckets: Cache<Long, Bucket> =
        Caffeine
            .newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .maximumSize(100_000)
            .build()

    fun consumeToken(userId: Long) {
        val bucket =
            buckets.get(userId) {
                Bucket
                    .builder()
                    .addLimit(PresignedUrlRateLimitPolicy.PER_MINUTE)
                    .build()
            }

        val probe = bucket.tryConsumeAndReturnRemaining(1)

        if (!probe.isConsumed) {
            throw ErrorException(ErrorType.EXCEED_RATE_LIMIT)
        }
    }

    // TODO: 향후 Retry-After 헤더로 활용할 수 있음
    private fun retryAfterSeconds(probe: ConsumptionProbe): Long = TimeUnit.NANOSECONDS.toSeconds(probe.nanosToWaitForRefill)
}
