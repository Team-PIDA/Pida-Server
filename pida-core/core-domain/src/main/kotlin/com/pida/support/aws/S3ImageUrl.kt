package com.pida.support.aws

data class S3ImageUrl(
    val presignedUrl: String,
    val presignedGetUrl: String,
)
