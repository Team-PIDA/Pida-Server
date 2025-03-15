package com.pida.authentication.domain.auth.component

import com.pida.authentication.domain.auth.NewAuthenticationSocial
import com.pida.authentication.domain.auth.repository.AuthenticationRepository
import com.pida.authentication.storage.db.core.AuthenticationEntityStatus
import com.pida.authentication.support.error.AuthenticationErrorException
import com.pida.authentication.support.error.AuthenticationErrorType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AuthenticationValidator(
    private val passwordEncoder: PasswordEncoder,
    private val authenticationRepository: AuthenticationRepository,
) {
    companion object {
        /** 이메일 정규화 **/
        val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,}\$")

        /** 8자 이상 16자 이하, 대소문자, 특수기호 최소 1개를 포함 */
        val PASSWORD_REGEX = Regex("^.{8,16}$")
    }

    fun verifySns(newAuthenticationSocial: NewAuthenticationSocial) {
        if (!newAuthenticationSocial.loginId.matches(EMAIL_REGEX)) {
            throw AuthenticationErrorException(AuthenticationErrorType.INVALID_LOGIN_ID_FORMAT)
        }

        if (authenticationRepository.verifySocialId(newAuthenticationSocial.socialId)) {
            throw AuthenticationErrorException(
                authenticationErrorType = AuthenticationErrorType.DUPLICATED_USER,
                data = newAuthenticationSocial.socialType.name,
            )
        }
    }

    fun verifyPassword(
        password: String,
        encryptedPassword: String,
    ) {
        if (!passwordEncoder.matches(password, encryptedPassword)) {
            throw AuthenticationErrorException(AuthenticationErrorType.INVALID_CREDENTIALS)
        }
    }

    fun verifyAuthenticationStatus(authenticationStatus: AuthenticationEntityStatus) {
        if (authenticationStatus == AuthenticationEntityStatus.DELETE) {
            throw AuthenticationErrorException(AuthenticationErrorType.WITHDRAWAL_USER)
        }
    }

    fun verifyLoginId(loginId: String) {
        if (authenticationRepository.verifyLoginId(loginId)) {
            throw AuthenticationErrorException(AuthenticationErrorType.DUPLICATED_USER)
        }
    }

    fun verifyChangePassword(
        password: String,
        newPassword: String,
        encryptedPassword: String,
    ) {
        if (!passwordEncoder.matches(password, encryptedPassword)) {
            throw AuthenticationErrorException(AuthenticationErrorType.INVALID_PASSWORD)
        }

        if (passwordEncoder.matches(newPassword, encryptedPassword)) {
            throw AuthenticationErrorException(AuthenticationErrorType.INVALID_NEW_PASSWORD)
        }
    }

    fun verifyChangeLoginId(
        loginId: String,
        newLoginId: String,
    ) {
        if (authenticationRepository.verifyLoginId(newLoginId)) {
            throw AuthenticationErrorException(AuthenticationErrorType.DUPLICATED_USER)
        }

        if (loginId == newLoginId) {
            throw AuthenticationErrorException(AuthenticationErrorType.INVALID_NEW_LOGIN_ID)
        }
    }
}
