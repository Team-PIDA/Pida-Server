package com.pida.storage.db.core.user.location

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface UserLocationJpaRepository :
    JpaRepository<UserLocationEntity, Long>,
    KotlinJdslJpqlExecutor {
    fun findByUserId(userId: Long): UserLocationEntity?
}
