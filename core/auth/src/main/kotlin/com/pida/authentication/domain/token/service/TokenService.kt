package com.pida.authentication.domain.token.service

import com.pida.authentication.domain.token.PhoneToken
import com.pida.authentication.domain.token.component.TokenCreator
import com.pida.authentication.domain.token.component.TokenReader
import org.springframework.stereotype.Service

@Service
class TokenService(
    private val tokenCreator: TokenCreator,
    private val tokenReader: TokenReader,
) {
    fun createPhoneToken(phone: String): PhoneToken = tokenCreator.createPhoneToken(phone)

    fun getPhone(phoneToken: PhoneToken): String = tokenReader.getPhoneWithVerifyPhoneJwt(phoneToken)
}
