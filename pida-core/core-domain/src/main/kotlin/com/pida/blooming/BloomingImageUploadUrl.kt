package com.pida.blooming

import com.pida.support.aws.S3ImageUrl

data class BloomingImageUploadUrl(
    val uploadUrl: String,
    val imageUrl: String,
) {
    companion object {
        fun from(s3ImageUrl: S3ImageUrl) = BloomingImageUploadUrl(
            uploadUrl = s3ImageUrl.presignedUrl,
            imageUrl = s3ImageUrl.imageUrl,
        )
    }
}
