package com.pida.authentication.domain.token.component

import com.pida.authentication.domain.token.PhoneToken
import com.pida.authentication.domain.token.repository.TokenRepository
import org.springframework.stereotype.Component

@Component
class TokenReader(
    private val tokenRepository: TokenRepository,
) {
    fun getPhoneWithVerifyPhoneJwt(phoneToken: PhoneToken): String = tokenRepository.getPhoneWithVerifyPhoneJwt(phoneToken)
}
