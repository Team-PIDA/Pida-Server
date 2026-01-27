package com.pida.storage.db.core.flowerspot

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import com.pida.support.geo.Region
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FlowerSpotJpaRepository :
    JpaRepository<FlowerSpotEntity, Long>,
    KotlinJdslJpqlExecutor {
    fun findByDeletedAtIsNull(): List<FlowerSpotEntity>

    fun findByRegionAndDeletedAtIsNull(region: Region): List<FlowerSpotEntity>

    @Query(
        """
        SELECT *
        FROM t_flower_spot
        WHERE ST_Contains(
            ST_MakeEnvelope(:swLng, :swLat, :neLng, :neLat, 4326),
            pin_point
        )
        AND deleted_at IS NULL
        """,
        nativeQuery = true,
    )
    fun findWithinBounds(
        @Param("swLat") swLat: Double,
        @Param("swLng") swLng: Double,
        @Param("neLat") neLat: Double,
        @Param("neLng") neLng: Double,
    ): List<FlowerSpotEntity>

    @Query(
        """
        SELECT *
        FROM t_flower_spot
        WHERE ST_Contains(
            ST_MakeEnvelope(:swLng, :swLat, :neLng, :neLat, 4326),
            pin_point
        )
        AND deleted_at IS NULL AND region = :region
        """,
        nativeQuery = true,
    )
    fun findWithinBoundsAndRegion(
        @Param("swLat") swLat: Double,
        @Param("swLng") swLng: Double,
        @Param("neLat") neLat: Double,
        @Param("neLng") neLng: Double,
        @Param("region") region: String,
    ): List<FlowerSpotEntity>

    fun findByStreetNameContainingAndDeletedAtIsNull(streetName: String): List<FlowerSpotEntity>
}
