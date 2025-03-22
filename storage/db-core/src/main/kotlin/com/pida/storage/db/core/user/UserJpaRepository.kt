package com.pida.storage.db.core.user

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository :
    JpaRepository<UserEntity, Long>,
    KotlinJdslJpqlExecutor {
    fun findByUserKey(userKey: String): UserEntity?

    fun findByNameAndPhoneAndDeletedAtIsNull(
        name: String,
        phone: String,
    ): UserEntity?

    fun findByPhone(phone: String): UserEntity?

    fun findAllByIdIn(ids: List<Long>): List<UserEntity>

    fun findByIdAndDeletedAtIsNull(id: Long): UserEntity?

    fun existsByEmailAndPhoneAndDeletedAtIsNull(
        email: String,
        phone: String,
    ): Boolean

    fun existsByEmailAndDeletedAtIsNull(email: String): Boolean

    fun existsByPhoneAndDeletedAtIsNull(phone: String): Boolean

    fun deleteByUserKey(userKey: String)
}
