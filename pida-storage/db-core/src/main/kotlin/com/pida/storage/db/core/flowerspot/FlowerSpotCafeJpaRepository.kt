package com.pida.storage.db.core.flowerspot

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FlowerSpotCafeJpaRepository : JpaRepository<FlowerSpotCafeEntity, Long> {
    fun findByDeletedAtIsNullOrderByIdAsc(): List<FlowerSpotCafeEntity>

    @Query(
        """
        SELECT *
        FROM t_flower_spot_cafe
        WHERE ST_Contains(
            ST_MakeEnvelope(:swLng, :swLat, :neLng, :neLat, 4326),
            pin_point
        )
        AND deleted_at IS NULL
        ORDER BY id ASC
        """,
        nativeQuery = true,
    )
    fun findWithinBoundsOrderByIdAsc(
        @Param("swLat") swLat: Double,
        @Param("swLng") swLng: Double,
        @Param("neLat") neLat: Double,
        @Param("neLng") neLng: Double,
    ): List<FlowerSpotCafeEntity>
}
