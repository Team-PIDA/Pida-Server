package com.pida.support.aws

interface ImageS3Caller {
    fun createUploadUrl(
        userId: Long,
        prefix: String,
        prefixId: Long,
    ): S3ImageUrl
}
