package com.pida.support.aws

enum class ImagePrefix {
    FLOWERSPOT,
    ;
}

val ImagePrefix.value: String
    get() = name.lowercase()
