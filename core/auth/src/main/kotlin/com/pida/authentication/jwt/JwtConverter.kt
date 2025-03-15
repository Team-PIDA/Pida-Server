package com.pida.authentication.jwt

import com.pida.authentication.domain.auth.Provider
import com.pida.authentication.storage.redis.auth.RedisTokenRepository
import com.pida.authentication.support.error.AuthenticationErrorException
import com.pida.authentication.support.error.AuthenticationErrorType
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimNames
import org.springframework.stereotype.Component
import org.springframework.util.Assert

@Component
class JwtConverter(
    private val redisTokenRepository: RedisTokenRepository,
) : Converter<Jwt, AbstractAuthenticationToken> {
    private var customJwtGrantedAuthoritiesConverter: Converter<Jwt, Collection<GrantedAuthority>> =
        CustomJwtGrantedAuthoritiesConverter()

    private var principalClaimName: String = JwtClaimNames.SUB

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val authority: Collection<GrantedAuthority>? = customJwtGrantedAuthoritiesConverter.convert(jwt)
        val provider: Provider = findProvider(jwt)
        return UsernamePasswordAuthenticationToken(provider, null, authority)
    }

    private fun setCustomJwtGrantedAuthoritiesConverter(
        customJwtGrantedAuthoritiesConverter: Converter<Jwt, Collection<GrantedAuthority>>,
    ) {
        Assert.notNull(customJwtGrantedAuthoritiesConverter, "jwtGrantedAuthoritiesConverter cannot be null")
        this.customJwtGrantedAuthoritiesConverter = customJwtGrantedAuthoritiesConverter
    }

    fun setPrincipalClaimName(principalClaimName: String) {
        Assert.hasText(principalClaimName, "principalClaimName cannot be empty")
        this.principalClaimName = principalClaimName
    }

    private fun findProvider(jwt: Jwt): Provider {
        val tokenWithAuthentication = redisTokenRepository.findByToken(jwt.tokenValue)
        jwt.validateByCachedToken(tokenWithAuthentication.accessToken)
        return Provider(
            id = tokenWithAuthentication.provider.id,
            userId = tokenWithAuthentication.provider.userId,
            userKey = tokenWithAuthentication.provider.userKey,
        )
    }

    @Throws(AuthenticationErrorException::class)
    private fun Jwt.validateByCachedToken(token: String) {
        if (this.tokenValue == token) {
            return
        }

        redisTokenRepository.deleteAllToken(this.tokenValue)
        throw AuthenticationErrorException(AuthenticationErrorType.INVALID_TOKEN)
    }
}
