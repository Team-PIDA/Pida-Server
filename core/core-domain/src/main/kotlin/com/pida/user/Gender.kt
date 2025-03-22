package com.pida.user

enum class Gender(
    val description: String,
) {
    ETC("기타"),
    MALE("남성"),
    FEMALE("여성"),
    ;

    companion object {
        fun toGender(gender: String): Gender =
            when (gender) {
                "male" -> MALE
                "female" -> FEMALE
                else -> ETC
            }
    }
}
