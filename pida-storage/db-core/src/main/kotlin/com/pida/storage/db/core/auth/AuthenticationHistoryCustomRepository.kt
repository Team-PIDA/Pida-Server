package com.pida.storage.db.core.auth

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.RenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class AuthenticationHistoryCustomRepository(
    private val entityManager: EntityManager,
    private val jdslRenderContext: RenderContext,
) {
    /**
     * 특정 날짜 이후 로그인한 활성 사용자 ID 목록 조회
     *
     * @param sinceDate 기준 날짜
     * @return 중복 제거된 사용자 ID 목록
     */
    fun findActiveUserIdsSince(sinceDate: LocalDateTime): List<Long> {
        val query =
            jpql {
                selectDistinct(path(AuthenticationHistoryEntity::userId))
                    .from(entity(AuthenticationHistoryEntity::class))
                    .whereAnd(
                        path(AuthenticationHistoryEntity::updatedAt).greaterThanOrEqualTo(sinceDate),
                        path(AuthenticationHistoryEntity::entityStatus).eq(AuthenticationEntityStatus.ACTIVE),
                    )
            }

        return entityManager.createQuery(query, jdslRenderContext).resultList
    }
}
