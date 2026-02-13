package com.pida.support.aws

import io.github.bucket4j.Bandwidth
import java.time.Duration

object PresignedUrlRateLimitPolicy {
    val PER_MINUTE: Bandwidth =
        Bandwidth
            .builder()
            .capacity(5)
            .refillGreedy(5, Duration.ofMinutes(1))
            .build()
}
