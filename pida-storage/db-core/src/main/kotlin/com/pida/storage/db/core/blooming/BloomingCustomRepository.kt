package com.pida.storage.db.core.blooming

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.RenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
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
}
