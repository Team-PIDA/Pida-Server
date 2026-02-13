package com.pida.storage.db.core.landmark

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface LandmarkJpaRepository : JpaRepository<LandmarkEntity, Long> {
    fun findByDeletedAtIsNull(): List<LandmarkEntity>

    @Query(
        """
        SELECT *
        FROM t_landmark
        WHERE name_tsv @@ plainto_tsquery('simple', :query)
        AND deleted_at IS NULL
        """,
        nativeQuery = true,
    )
    fun findByNameContainingAndDeletedAtIsNull(query: String): List<LandmarkEntity>

    fun existsByNameAndDeletedAtIsNull(name: String): Boolean

    @Modifying(clearAutomatically = true)
    @Query(
        """
        UPDATE
        t_landmark
        SET name_tsv = to_tsvector('simple', name)
        WHERE name_tsv IS NULL
        """,
        nativeQuery = true,
    )
    fun updateNameTsv()
}
