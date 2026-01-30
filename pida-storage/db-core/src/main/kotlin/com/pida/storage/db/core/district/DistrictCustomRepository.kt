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
    fun searchByKeyword(keyword: String): List<DistrictEntity> {
        val pattern = "$keyword%" // B-tree index 활용을 위한 접두사 일치 패턴

        val query =
            jpql {
                select(entity(DistrictEntity::class))
                    .from(entity(DistrictEntity::class))
                    .whereAnd(
                        path(DistrictEntity::deletedAt).isNull(),
                        or(
                            path(DistrictEntity::sigungu).like(pattern),
                            path(DistrictEntity::eupmyeondonggu).like(pattern),
                            path(DistrictEntity::eupmyeonridong).like(pattern),
                            path(DistrictEntity::ri).like(pattern),
                        ),
                    ).orderBy(
                        caseWhen(path(DistrictEntity::sigungu).like(pattern))
                            .then(value(1))
                            .`when`(path(DistrictEntity::eupmyeondonggu).like(pattern))
                            .then(value(2))
                            .`when`(path(DistrictEntity::eupmyeonridong).like(pattern))
                            .then(value(3))
                            .`when`(path(DistrictEntity::ri).like(pattern))
                            .then(value(4))
                            .`else`(value(5))
                            .asc(),
                    )
            }

        return entityManager.createQuery(query, jdslRenderContext).resultList
    }
}
