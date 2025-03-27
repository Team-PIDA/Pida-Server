package com.pida.storage.db.core.user

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository :
    JpaRepository<UserEntity, Long>,
    KotlinJdslJpqlExecutor {
    fun findByUserKeyAndDeletedAtIsNull(userKey: String): UserEntity?

    fun findByEmailAndPasswordAndDeletedAtIsNull(
        email: String,
        password: String,
    ): UserEntity?

    fun findAllByIdIn(ids: List<Long>): List<UserEntity>

    fun findByIdAndDeletedAtIsNull(id: Long): UserEntity?

    fun existsByEmailAndDeletedAtIsNull(email: String): Boolean

    fun findByEmailAndDeletedAtIsNull(email: String): UserEntity?
}
