package com.pida.authentication.domain.auth.repository

import com.pida.authentication.domain.auth.*

interface AuthenticationRepository {
    fun findBy(loginId: String): AuthenticationPida?

    fun findBy(
        socialId: String,
        socialType: SocialType,
    ): AuthenticationSns?

    fun readAuthentication(userKey: String): AuthenticationPida

    fun createAuthentication(
        userId: Long,
        userKey: String,
        newAuthenticationSocial: NewAuthenticationSocial,
    ): AuthenticationSns

    fun createAuthentication(
        userId: Long,
        userKey: String,
        newAuthenticationPida: NewAuthenticationPida,
    ): AuthenticationPida

    fun verifyLoginId(loginId: String): Boolean

    fun verifySocialId(socialId: String): Boolean

    fun withdrawal(userKey: String)

    fun updatePassword(
        userKey: String,
        hashedNewPassword: String,
    )

    fun updatePassword(
        userKey: String,
        loginId: String,
        hashedNewPassword: String,
    )

    fun updateLoginId(
        id: Long,
        loginId: String,
    )

    fun readLoginId(userKey: String): List<LoginIdWithSocialType>?

    fun readyByLoginIdWithSocialType(userKey: String): LoginIdWithSocialType?
}
