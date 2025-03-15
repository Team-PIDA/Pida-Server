package com.pida.authentication.storage.db.core.auth

import com.pida.authentication.domain.auth.AuthenticationPida
import com.pida.authentication.domain.auth.AuthenticationSns
import com.pida.authentication.domain.auth.AuthorityType
import com.pida.authentication.domain.auth.GrantedAuthority
import com.pida.authentication.domain.auth.LoginIdWithSocialType
import com.pida.authentication.domain.auth.NewAuthenticationSocial
import com.pida.authentication.domain.auth.SocialType
import com.pida.authentication.storage.db.core.AuthenticationBaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "t_authentication")
class AuthenticationEntity(
    val userId: Long,
    val userKey: String,
    private var loginId: String,
    private var password: String?,
    private var socialId: String?,
    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private val socialType: SocialType,
    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private var authorityType: AuthorityType,
) : AuthenticationBaseEntity() {
    constructor(
        userId: Long,
        userKey: String,
        newAuthenticationSocial: NewAuthenticationSocial,
    ) : this(
        userId = userId,
        userKey = userKey,
        loginId = newAuthenticationSocial.loginId,
        password = null,
        socialId = newAuthenticationSocial.socialId,
        socialType = newAuthenticationSocial.socialType,
        authorityType = newAuthenticationSocial.grantedAuthority.authorityType,
    )

    fun toAuthenticationPida(): AuthenticationPida =
        AuthenticationPida(
            id = id!!,
            userId = userId,
            userKey = userKey,
            loginId = loginId,
            password = password!!,
            socialType = socialType,
            grantedAuthorities = mutableListOf(GrantedAuthority(authorityType)),
            authenticationStatus = entityStatus,
        )

    fun toAuthenticationSns(): AuthenticationSns =
        AuthenticationSns(
            id = id!!,
            userId = userId,
            userKey = userKey,
            loginId = loginId,
            socialId = socialId!!,
            socialType = socialType,
            grantedAuthorities = mutableListOf(GrantedAuthority(authorityType)),
            authenticationStatus = entityStatus,
        )

    fun updatePassword(newHashedPassword: String) {
        this.password = newHashedPassword
    }

    fun updateLoginId(loginId: String) {
        this.loginId = loginId
    }

    fun toLoginIdWithSocialType(): LoginIdWithSocialType =
        LoginIdWithSocialType(
            loginId = loginId,
            socialType = socialType,
            createdAt = createdAt,
        )
}
