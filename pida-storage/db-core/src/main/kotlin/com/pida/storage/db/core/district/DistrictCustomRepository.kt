package com.pida.storage.db.core.district

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.RenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class DistrictCustomRepository(
    private val entityManager: EntityManager,
    private val jdslRenderContext: RenderContext,
) {
    /**
     * 주어진 좌표에서 가장 가까운 District를 PostGIS KNN 정렬로 찾습니다.
     *
     * @param latitude 위도
     * @param longitude 경도
     * @return 가장 가까운 DistrictEntity, 없으면 null
     */
    fun findNearestDistrict(
        latitude: Double,
        longitude: Double,
    ): DistrictEntity? {
        val query =
            entityManager.createNativeQuery(
                """
                SELECT *
                FROM t_district
                WHERE deleted_at IS NULL
                ORDER BY pin_point <-> ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
                LIMIT 1
                """,
                DistrictEntity::class.java,
            )

        query.setParameter("lat", latitude)
        query.setParameter("lng", longitude)

        @Suppress("UNCHECKED_CAST")
        val results = query.resultList as List<DistrictEntity>

        return results.firstOrNull()
    }

    fun searchByKeyword(keyword: String): List<DistrictEntity> {
        val pattern = "$keyword%" // B-tree index 활용을 위한 접두사 일치 패턴

        val query =
            jpql {
                select(entity(DistrictEntity::class))
                    .from(entity(DistrictEntity::class))
                    .whereAnd(
                        path(DistrictEntity::deletedAt).isNull(),
                        or(
                            path(DistrictEntity::sido).like(pattern),
                            path(DistrictEntity::sigungu).like(pattern),
                            path(DistrictEntity::eupmyeondonggu).like(pattern),
                            path(DistrictEntity::eupmyeonridong).like(pattern),
                            path(DistrictEntity::ri).like(pattern),
                        ),
                    ).orderBy(
                        caseWhen(path(DistrictEntity::sido).like(pattern))
                            .then(value(1))
                            .`when`(path(DistrictEntity::sigungu).like(pattern))
                            .then(value(2))
                            .`when`(path(DistrictEntity::eupmyeondonggu).like(pattern))
                            .then(value(3))
                            .`when`(path(DistrictEntity::eupmyeonridong).like(pattern))
                            .then(value(4))
                            .`when`(path(DistrictEntity::ri).like(pattern))
                            .then(value(5))
                            .`else`(value(6))
                            .asc(),
                    )
            }

        return entityManager.createQuery(query, jdslRenderContext).resultList
    }
}
