package com.pida.auth.component

import com.pida.auth.UpdateLoginId
import com.pida.auth.UpdatePassword
import org.springframework.stereotype.Component

@Component
class AuthenticationUpdater(
    private val authenticationValidator: AuthenticationValidator,
    private val authenticationRepository: AuthenticationRepository,
) {
    fun updateLoginId(
        authenticationId: Long,
        updateLoginId: com.pida.auth.UpdateLoginId,
    ) {
        authenticationValidator.verifyChangeLoginId(
            loginId = updateLoginId.loginId,
            newLoginId = updateLoginId.newLoginId,
        )

        authenticationRepository.updateLoginId(authenticationId, updateLoginId.newLoginId)
    }

    fun updatePassword(
        userKey: String,
        updatePassword: UpdatePassword,
    ) {
        val authentication = authenticationRepository.readAuthentication(userKey)
        authenticationValidator.verifyChangePassword(
            password = updatePassword.password,
            newPassword = updatePassword.newPassword,
            encryptedPassword = authentication.password,
        )
        authenticationRepository.updatePassword(
            userKey = userKey,
            hashedNewPassword = passwordEncoder.encode(updatePassword.newPassword),
        )
    }

    fun updatePassword(
        userKey: String,
        newPassword: String,
    ) {
        authenticationRepository.updatePassword(
            userKey = userKey,
            hashedNewPassword = passwordEncoder.encode(newPassword),
        )
    }

    fun updatePassword(
        userKey: String,
        loginId: String,
        newPassword: String,
    ) {
        authenticationRepository.updatePassword(
            userKey = userKey,
            hashedNewPassword = passwordEncoder.encode(newPassword),
        )
    }
}
