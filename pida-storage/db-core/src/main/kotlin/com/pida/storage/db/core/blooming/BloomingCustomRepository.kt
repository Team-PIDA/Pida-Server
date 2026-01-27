package com.pida.storage.db.core.blooming

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.RenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import com.pida.blooming.BloomingStatus
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

    /**
     * 주어진 FlowerSpot ID 목록 중 BLOOMED 상태인 spot ID 목록 조회
     *
     * @param spotIds FlowerSpot ID 목록
     * @return BLOOMED 상태인 중복 제거된 spot ID 목록
     */
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
}
