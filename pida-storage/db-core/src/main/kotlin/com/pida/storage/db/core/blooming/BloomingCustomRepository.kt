package com.pida.storage.db.core.blooming

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.RenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import com.pida.blooming.BloomingStatus
import com.pida.blooming.RegionStatusCount
import com.pida.storage.db.core.flowerspot.FlowerSpotEntity
import com.pida.storage.db.core.support.JDSLExtensions
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class BloomingCustomRepository(
    private val entityManager: EntityManager,
    private val jdslRenderContext: RenderContext,
) {
    companion object {
        const val DATE_THRESHOLD = 5L
    }

    fun recentlyBySpotId(spotId: Long): List<BloomingEntity> {
        val threshold = LocalDateTime.now().minusDays(DATE_THRESHOLD)

        val query =
            jpql {
                select(entity(BloomingEntity::class))
                    .from(entity(BloomingEntity::class))
                    .whereAnd(
                        path(BloomingEntity::flowerSpotId).eq(spotId),
                        path(BloomingEntity::createdAt).gt(threshold),
                    )
            }

        return entityManager.createQuery(query, jdslRenderContext).resultList
    }

    fun recentlyBySpotIds(spotIds: List<Long>): List<BloomingEntity> {
        val threshold = LocalDateTime.now().minusDays(DATE_THRESHOLD)

        val query =
            jpql {
                select(entity(BloomingEntity::class))
                    .from(entity(BloomingEntity::class))
                    .whereAnd(
                        path(BloomingEntity::flowerSpotId).`in`(spotIds),
                        path(BloomingEntity::createdAt).gt(threshold),
                    )
            }

        return entityManager.createQuery(query, jdslRenderContext).resultList
    }

    fun findTodayBloomingByUserId(
        userId: Long,
        flowerSpotId: Long,
    ): BloomingEntity? {
        val startOfDay = LocalDate.now().atStartOfDay()
        val endOfDay = startOfDay.plusDays(1)

        val query =
            jpql {
                select(entity(BloomingEntity::class))
                    .from(entity(BloomingEntity::class))
                    .whereAnd(
                        path(BloomingEntity::userId).eq(userId),
                        path(BloomingEntity::flowerSpotId).eq(flowerSpotId),
                        path(BloomingEntity::createdAt).greaterThanOrEqualTo(startOfDay),
                        path(BloomingEntity::createdAt).lessThan(endOfDay),
                    )
            }

        return entityManager.createQuery(query, jdslRenderContext).resultList.firstOrNull()
    }

    fun findBloomedSpotIdsByFlowerSpotIds(spotIds: List<Long>): List<Long> {
        if (spotIds.isEmpty()) return emptyList()

        val threshold = LocalDateTime.now().minusDays(DATE_THRESHOLD)

        val query =
            jpql {
                selectDistinct(path(BloomingEntity::flowerSpotId))
                    .from(entity(BloomingEntity::class))
                    .whereAnd(
                        path(BloomingEntity::flowerSpotId).`in`(spotIds),
                        path(BloomingEntity::status).eq(BloomingStatus.BLOOMED),
                        path(BloomingEntity::createdAt).greaterThan(threshold),
                    )
            }

        return entityManager.createQuery(query, jdslRenderContext).resultList
    }

    /**
     * 지역별, 상태별 최근 5일간 투표 수를 집계합니다.
     * BloomingEntity와 FlowerSpotEntity를 JOIN하여 지역 정보를 가져옵니다.
     *
     * @return 지역별 상태별 투표 수 리스트
     */
    fun countByRegionAndStatus(): List<RegionStatusCount> {
        val threshold = LocalDateTime.now().minusDays(DATE_THRESHOLD)

        val query =
            jpql(JDSLExtensions) {
                selectNew<RegionStatusCount>(
                    path(FlowerSpotEntity::region),
                    path(BloomingEntity::status),
                    count(path(BloomingEntity::id)),
                ).from(
                    entity(BloomingEntity::class),
                    join(FlowerSpotEntity::class)
                        .on(path(BloomingEntity::flowerSpotId).eq(path(FlowerSpotEntity::id))),
                ).whereAnd(
                    path(BloomingEntity::createdAt).greaterThan(threshold),
                    path(FlowerSpotEntity::deletedAt).isNull(),
                ).groupBy(
                    path(FlowerSpotEntity::region),
                    path(BloomingEntity::status),
                )
            }

        return entityManager.createQuery(query, jdslRenderContext).resultList
    }
}
