package com.pida.authentication.client.oauth.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.pida.authentication.client.oauth.KaKaoClientResult
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@JsonIgnoreProperties(ignoreUnknown = true)
data class KaKaoUserResponse(
    val id: String,
    @field:JsonProperty("kakao_account")
    val kaKaoAccount: KaKaoAccount,
) {
    fun toResult(): KaKaoClientResult =
        KaKaoClientResult(
            id = id,
            email = kaKaoAccount.email ?: "",
            name = kaKaoAccount.name ?: "피다",
            nickname = kaKaoAccount.kaKaoProfile.nickname ?: "피다",
            phone =
                kaKaoAccount.phoneNumber
                    ?.replace("+82 ", "0")
                    ?.replace("-", "") ?: "",
            birth =
                kaKaoAccount.birthYear?.let {
                    LocalDate.parse(
                        "${kaKaoAccount.birthYear}${kaKaoAccount.birthDay}",
                        DateTimeFormatter.ofPattern("yyyyMMdd"),
                    )
                } ?: LocalDate.now(),
            gender = kaKaoAccount.gender ?: "ETC",
        )
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class KaKaoAccount(
    val email: String?,
    val name: String?,
    @field:JsonProperty("profile")
    val kaKaoProfile: KaKaoProfile,
    @field:JsonProperty("phone_number")
    val phoneNumber: String?,
    @field:JsonProperty("birthyear")
    val birthYear: String?,
    @field:JsonProperty("birthday")
    val birthDay: String?,
    val gender: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KaKaoProfile(
    val nickname: String?,
)
