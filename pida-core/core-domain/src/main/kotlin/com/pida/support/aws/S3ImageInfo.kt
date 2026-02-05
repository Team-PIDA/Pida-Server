package com.pida.support.aws

import java.time.LocalDateTime

data class S3ImageInfo(
    val url: String,
    val uploadedAt: LocalDateTime,
)
