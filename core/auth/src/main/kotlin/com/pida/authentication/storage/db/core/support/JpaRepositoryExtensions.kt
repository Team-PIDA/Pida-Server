package com.pida.authentication.storage.db.core.support

import com.pida.authentication.storage.db.core.AuthenticationBaseEntity
import com.pida.authentication.support.error.AuthenticationErrorException
import com.pida.authentication.support.error.AuthenticationErrorType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull

fun <T : AuthenticationBaseEntity> JpaRepository<T, Long>.findByIdOrElseThrow(id: Long): T {
    val value = findByIdOrNull(id) ?: throw AuthenticationErrorException(AuthenticationErrorType.NOT_FOUND_DATA)
    return value
}
