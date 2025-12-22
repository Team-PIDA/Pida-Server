package com.pida.support.aws

enum class ImageExtension {
    JPG,
    PNG,
    WEBP,
    ;
}

val ImageExtension.value: String
    get() = name.lowercase()
