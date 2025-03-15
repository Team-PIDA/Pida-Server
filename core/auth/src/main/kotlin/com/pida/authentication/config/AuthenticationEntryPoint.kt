package com.pida.authentication.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.pida.authentication.support.error.AuthenticationErrorMessage
import com.pida.authentication.support.error.AuthenticationErrorType
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint

class AuthenticationEntryPoint(
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authenticationException: AuthenticationException,
    ) {
        with(response) {
            status = HttpStatus.UNAUTHORIZED.value()
            contentType = MediaType.APPLICATION_JSON_VALUE
            writer.write(
                objectMapper.writeValueAsString(
                    AuthenticationErrorMessage(AuthenticationErrorType.UNAUTHORIZED_TOKEN),
                ),
            )
        }
    }
}
