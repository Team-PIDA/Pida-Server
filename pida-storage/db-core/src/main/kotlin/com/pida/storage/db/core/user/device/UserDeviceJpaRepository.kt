package com.pida.storage.db.core.user.device

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface UserDeviceJpaRepository :
    JpaRepository<UserDeviceEntity, Long>,
    KotlinJdslJpqlExecutor {
    fun findAllByDeletedAtIsNull(): List<UserDeviceEntity>

    fun findAllByUserKeyAndDeletedAtIsNull(userKey: String): List<UserDeviceEntity>

    fun findAllByUserIdAndDeletedAtIsNull(userId: Long): List<UserDeviceEntity>
}
