package com.pida.authentication.domain.auth

import com.pida.authentication.storage.db.core.AuthenticationEntityStatus

sealed interface Authentication {
    val id: Long
    val userId: Long
    val userKey: String
    val loginId: String
    val socialType: SocialType
    val grantedAuthorities: MutableList<GrantedAuthority>
    val authenticationStatus: AuthenticationEntityStatus
}
