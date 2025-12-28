package com.pida.notification

enum class ReadStatus {
    READ,
    UNREAD,
    ;

    companion object {
        fun of(readStatus: String): ReadStatus =
            when (readStatus) {
                "READ" -> READ
                "UNREAD" -> UNREAD
                else -> throw IllegalArgumentException("Unknown ReadStatus: $readStatus")
            }
    }
}
