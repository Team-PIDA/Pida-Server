package com.pida.authentication.domain.auth.service

import com.pida.authentication.domain.auth.component.AuthenticationHistoryReader
import com.pida.authentication.domain.token.Token
import org.springframework.stereotype.Service

@Service
class AuthenticationHistoryService(
    private val authenticationHistoryReader: AuthenticationHistoryReader,
) {
    /**
     * 사용중인 accessToken, refreshToken 가져오기
     */
    fun getAccessTokenWithUserKey(userKey: String): Token = authenticationHistoryReader.readByUserKey(userKey).token
}
