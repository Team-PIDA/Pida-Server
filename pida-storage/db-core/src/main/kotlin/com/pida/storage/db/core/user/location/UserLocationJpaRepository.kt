package com.pida.storage.db.core.user.location

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserLocationJpaRepository :
    JpaRepository<UserLocationEntity, Long>,
    KotlinJdslJpqlExecutor {
    fun findByUserId(userId: Long): UserLocationEntity?

    fun findByUserIdIn(userIds: List<Long>): List<UserLocationEntity>

    @Query(
        """
        SELECT *
        FROM t_user_location
        WHERE ST_DWithin(
            location::geography,
            ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
            :radiusMeters
        )
        ORDER BY ST_Distance(
            location::geography,
            ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography
        )
        """,
        nativeQuery = true,
    )
    fun findWithinRadius(
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("radiusMeters") radiusMeters: Double,
    ): List<UserLocationEntity>
}
