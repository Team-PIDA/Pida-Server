package com.pida.storage.db.core.flowerevent

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.RenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class FlowerEventCustomRepository(
    private val entityManager: EntityManager,
    private val jdslRenderContext: RenderContext,
) {
    fun updateThumbnailUrl(
        eventId: Long,
        thumbnailUrl: String,
    ) {
        val query =
            jpql {
                update(entity(FlowerEventEntity::class))
                    .set(path(FlowerEventEntity::thumbnailUrl), thumbnailUrl)
                    .where(path(FlowerEventEntity::id).eq(eventId))
            }

        entityManager.createQuery(query, jdslRenderContext).executeUpdate()
    }
}
