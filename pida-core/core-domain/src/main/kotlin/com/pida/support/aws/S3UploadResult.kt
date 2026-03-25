package com.pida.support.aws

data class S3UploadResult(
    val s3Key: String,
    val publicUrl: String,
)
