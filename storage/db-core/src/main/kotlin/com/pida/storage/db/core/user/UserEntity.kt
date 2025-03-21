package com.pida.storage.db.core.user

import com.pida.storage.db.core.support.BaseEntity
import com.pida.user.Gender
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "t_users")
class UserEntity(
    @Column(name = "user_key")
    val userKey: String,
    val name: String,
    val email: String,
    val phone: String,
    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    val gender: Gender,
    var birth: LocalDate,
): BaseEntity()