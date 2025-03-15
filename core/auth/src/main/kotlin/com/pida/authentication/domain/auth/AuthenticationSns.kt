package com.pida.authentication.domain.auth

import com.pida.authentication.storage.db.core.AuthenticationEntityStatus

data class AuthenticationSns(
    override val id: Long,
    override val userId: Long,
    override val userKey: String,
    override val loginId: String,
    val socialId: String,
    override val socialType: SocialType,
    override val grantedAuthorities: MutableList<GrantedAuthority>,
    override val authenticationStatus: AuthenticationEntityStatus,
) : Authentication
