package com.pida.storage.db.core.flowerevent

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FlowerEventJpaRepository : JpaRepository<FlowerEventEntity, Long> {
    fun findByCategoryIdAndDeletedAtIsNullOrderByStartDateAscIdAsc(categoryId: Long): List<FlowerEventEntity>

    @Query(
        """
        SELECT *
        FROM t_flower_event
        WHERE category_id = :categoryId
        AND ST_Contains(
            ST_MakeEnvelope(:swLng, :swLat, :neLng, :neLat, 4326),
            pin_point
        )
        AND deleted_at IS NULL
        ORDER BY start_date ASC, id ASC
        """,
        nativeQuery = true,
    )
    fun findByCategoryIdWithinBoundsOrderByStartDateAscIdAsc(
        @Param("categoryId") categoryId: Long,
        @Param("swLat") swLat: Double,
        @Param("swLng") swLng: Double,
        @Param("neLat") neLat: Double,
        @Param("neLng") neLng: Double,
    ): List<FlowerEventEntity>
}
