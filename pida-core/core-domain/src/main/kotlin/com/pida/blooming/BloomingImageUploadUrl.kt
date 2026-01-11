package com.pida.blooming

import com.pida.support.aws.S3ImageUrl

data class BloomingImageUploadUrl(
    val uploadUrl: String,
    val previewUrl: String,
) {
    companion object {
        fun from(s3ImageUrl: S3ImageUrl) =
            BloomingImageUploadUrl(
                uploadUrl = s3ImageUrl.presignedUrl,
                previewUrl = s3ImageUrl.presignedGetUrl,
            )
    }
}
