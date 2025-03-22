package com.pida.storage.db.core.user

import com.pida.storage.db.core.support.BaseEntity
import com.pida.user.Gender
import com.pida.user.NewUser
import com.pida.user.NewUserKey
import com.pida.user.User
import com.pida.user.UserProfile
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "t_users")
class UserEntity(
    @Column(name = "user_key")
    val userKey: String,
    var name: String,
    var nickname: String,
    var email: String,
    var phone: String,
    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    val gender: Gender,
    var birth: LocalDate,
) : BaseEntity() {
    constructor(
        newUser: NewUser,
        newUserKey: NewUserKey,
    ) : this(
        userKey = newUserKey.key,
        email = newUser.email,
        name = newUser.name,
        nickname = newUser.nickname,
        phone = newUser.phone,
        gender = newUser.gender,
        birth = newUser.birth,
    )

    fun toUser(): User =
        User(
            id = id!!,
            key = userKey,
        )

    fun toProfile(): UserProfile =
        UserProfile(
            id = id!!,
            key = userKey,
            email = email,
            name = name,
            nickname = nickname,
            phone = phone,
            gender = gender,
            birth = birth,
            createdAt = createdAt,
        )

    fun updateNickname(nickname: String) {
        this.nickname = nickname
    }

    fun updatePhone(phone: String) {
        this.phone = phone
    }

    fun updateEmail(email: String) {
        this.email = email
    }
}
