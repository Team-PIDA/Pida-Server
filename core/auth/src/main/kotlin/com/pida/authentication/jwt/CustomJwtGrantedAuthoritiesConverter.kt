package com.pida.authentication.jwt
import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

class CustomJwtGrantedAuthoritiesConverter : Converter<Jwt, Collection<GrantedAuthority>> {
    override fun convert(jwt: Jwt): Collection<GrantedAuthority> {
        val getClaim = jwt.getClaim<List<String>>("roles") ?: return emptyList()
        return getClaim.map { role -> SimpleGrantedAuthority(role) }
    }
}
