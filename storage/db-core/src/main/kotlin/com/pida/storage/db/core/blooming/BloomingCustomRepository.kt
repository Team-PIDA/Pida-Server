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
    fun countRecentBySpotId(spotId: Long): Long {
        val threshold = LocalDateTime.now().minusDays(5)

        val query =
            jpql {
                select(count(entity(BloomingEntity::class)))
                    .from(entity(BloomingEntity::class))
                    .whereAnd(
                        path(BloomingEntity::flowerSpotId).eq(spotId),
                        path(BloomingEntity::createdAt).gt(threshold),
                    )
            }

        return entityManager.createQuery(query, jdslRenderContext).singleResult
    }
}
