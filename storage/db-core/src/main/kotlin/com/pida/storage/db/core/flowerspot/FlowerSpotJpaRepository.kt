package com.pida.storage.db.core.flowerspot

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface FlowerSpotJpaRepository :
    JpaRepository<FlowerSpotEntity, Long>,
    KotlinJdslJpqlExecutor {
    fun findByDeletedAtIsNull(): List<FlowerSpotEntity>?
}
