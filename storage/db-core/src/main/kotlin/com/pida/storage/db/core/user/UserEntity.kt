package com.pida.storage.db.core.user

import com.pida.auth.SocialType
import com.pida.storage.db.core.support.BaseEntity
import com.pida.user.NewUser
import com.pida.user.NewUserKey
import com.pida.user.SocialUser
import com.pida.user.User
import com.pida.user.UserProfile
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "t_users")
class UserEntity(
    @Column(name = "user_key")
    val userKey: String,
    var name: String,
    var nickname: String?,
    var email: String,
    private var password: String?,
    private var socialId: String?,
    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private val socialType: SocialType,
) : BaseEntity() {
    constructor(
        newUser: NewUser,
        newUserKey: NewUserKey,
    ) : this(
        userKey = newUserKey.key,
        email = newUser.email,
        name = newUser.name,
        nickname = newUser.nickname,
        password = newUser.password,
        socialId = newUser.socialId,
        socialType = newUser.socialType,
    )

    fun toUser(): User =
        User(
            id = id!!,
            key = userKey,
        )

    fun toSocialUser(): SocialUser =
        SocialUser(
            id = id!!,
            key = userKey,
            name = name,
            socialId = socialId!!,
            socialType = socialType,
        )

    fun toProfile(): UserProfile =
        UserProfile(
            id = id!!,
            key = userKey,
            email = email,
            name = name,
            nickname = nickname ?: "",
            createdAt = createdAt,
        )

    fun updateNickname(nickname: String) {
        this.nickname = nickname
    }

    fun updateName(name: String) {
        this.name = name
    }

    fun updateEmail(email: String) {
        this.email = email
    }
}
