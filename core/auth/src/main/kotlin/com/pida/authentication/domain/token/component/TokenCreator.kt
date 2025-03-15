package com.pida.authentication.domain.token.component

import com.pida.authentication.domain.token.PhoneToken
import com.pida.authentication.domain.token.repository.TokenRepository
import org.springframework.stereotype.Component

@Component
class TokenCreator(
    private val tokenRepository: TokenRepository,
) {
    fun createPhoneToken(phone: String): PhoneToken {
        val token = tokenRepository.createPhoneJwt(phone)
        return PhoneToken(token)
    }
}
