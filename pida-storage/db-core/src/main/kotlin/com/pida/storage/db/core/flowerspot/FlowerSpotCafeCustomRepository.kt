package com.pida.storage.db.core.flowerspot

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.RenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class FlowerSpotCafeCustomRepository(
    private val entityManager: EntityManager,
    private val jdslRenderContext: RenderContext,
) {
    fun updateThumbnailUrl(
        cafeId: Long,
        thumbnailUrl: String,
    ) {
        val query =
            jpql {
                update(entity(FlowerSpotCafeEntity::class))
                    .set(path(FlowerSpotCafeEntity::thumbnailUrl), thumbnailUrl)
                    .where(path(FlowerSpotCafeEntity::id).eq(cafeId))
            }

        entityManager.createQuery(query, jdslRenderContext).executeUpdate()
    }
}
