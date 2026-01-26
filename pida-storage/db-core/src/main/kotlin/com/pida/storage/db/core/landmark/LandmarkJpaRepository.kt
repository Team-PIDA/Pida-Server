package com.pida.storage.db.core.landmark

import org.springframework.data.jpa.repository.JpaRepository

interface LandmarkJpaRepository : JpaRepository<LandmarkEntity, Long> {
    fun findByDeletedAtIsNull(): List<LandmarkEntity>

    fun findByNameContainingAndDeletedAtIsNull(name: String): List<LandmarkEntity>

    fun existsByNameAndDeletedAtIsNull(name: String): Boolean
}
