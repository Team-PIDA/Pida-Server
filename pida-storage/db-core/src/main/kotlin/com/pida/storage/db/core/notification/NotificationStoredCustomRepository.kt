package com.pida.storage.db.core.notification

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.RenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import com.pida.notification.NotificationStored
import com.pida.notification.NotificationType
import com.pida.storage.db.core.support.JDSLExtensions
import com.pida.support.cursor.Cursor
import com.pida.support.cursor.CursorRequest
import jakarta.persistence.EntityManager
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class NotificationStoredCustomRepository(
    private val notificationStoredJpaRepository: NotificationStoredJpaRepository,
    private val entityManager: EntityManager,
    private val jdslRenderContext: RenderContext,
) {
    companion object {
        private val EXCLUDED_TYPES =
            listOf(
                NotificationType.REGULAR,
            )
    }

    /**
     * 사용자별 알림 횟수 조회 결과 DTO
     */
    data class UserNotificationCount(
        val userId: Long,
        val count: Long,
    )

    fun findAllBy(
        userId: Long,
        cursorRequest: CursorRequest,
    ): Cursor<NotificationStored.Info> {
        val pageable = PageRequest.ofSize(cursorRequest.size.toInt())
        val notifications =
            notificationStoredJpaRepository.findPage(JDSLExtensions, pageable) {
                select(entity(NotificationStoredEntity::class))
                    .from(entity(NotificationStoredEntity::class))
                    .whereAnd(
                        path(NotificationStoredEntity::userId).equal(userId),
                        path(NotificationStoredEntity::type).notIn(EXCLUDED_TYPES),
                        cursorRequest.lastId?.let {
                            path(NotificationStoredEntity::id).lessThan(cursorRequest.lastId)
                        },
                    ).orderBy(
                        path(NotificationStoredEntity::id).desc(),
                    )
            }
        val content =
            notifications.content
                .filterNotNull()
                .map { it.toNotificationStoredInfo() }
        val nextCursor = if (content.size < cursorRequest.size) null else notifications.lastOrNull()?.id

        return Cursor.of(
            nextCursor = nextCursor,
            size = notifications.totalElements,
            content = content,
        )
    }

    /**
     * 특정 기간 이후 특정 타입의 알림을 받은 사용자별 알림 횟수 조회
     *
     * @param userIds 조회할 사용자 ID 목록
     * @param type 알림 타입
     * @param createdAtAfter 조회 시작 시간
     * @return 사용자 ID별 알림 횟수 Map
     */
    fun countByUserIdsAndTypeAndCreatedAtAfter(
        userIds: List<Long>,
        type: NotificationType,
        createdAtAfter: LocalDateTime,
    ): Map<Long, Long> {
        if (userIds.isEmpty()) {
            return emptyMap()
        }

        val query =
            jpql(JDSLExtensions) {
                selectNew<UserNotificationCount>(
                    path(NotificationStoredEntity::userId),
                    count(NotificationStoredEntity::id),
                ).from(
                    entity(NotificationStoredEntity::class),
                ).whereAnd(
                    path(NotificationStoredEntity::userId).`in`(userIds),
                    path(NotificationStoredEntity::type).eq(type),
                    path(NotificationStoredEntity::createdAt).greaterThanOrEqualTo(createdAtAfter),
                ).groupBy(
                    path(NotificationStoredEntity::userId),
                )
            }

        return entityManager
            .createQuery(query, jdslRenderContext)
            .resultList
            .associate { it.userId to it.count }
    }

    /**
     * 특정 기간 이후 알림을 받은 사용자별 알림 횟수 조회
     *
     * @param userIds 조회할 사용자 ID 목록
     * @param createdAtAfter 조회 시작 시간
     * @return 사용자 ID별 알림 횟수 Map
     */
    fun countByUserIdsAndCreatedAtAfter(
        userIds: List<Long>,
        createdAtAfter: LocalDateTime,
    ): Map<Long, Long> {
        if (userIds.isEmpty()) {
            return emptyMap()
        }

        val query =
            jpql(JDSLExtensions) {
                selectNew<UserNotificationCount>(
                    path(NotificationStoredEntity::userId),
                    count(NotificationStoredEntity::id),
                ).from(
                    entity(NotificationStoredEntity::class),
                ).whereAnd(
                    path(NotificationStoredEntity::userId).`in`(userIds),
                    path(NotificationStoredEntity::createdAt).greaterThanOrEqualTo(createdAtAfter),
                ).groupBy(
                    path(NotificationStoredEntity::userId),
                )
            }

        return entityManager
            .createQuery(query, jdslRenderContext)
            .resultList
            .associate { it.userId to it.count }
    }

    /**
     * 특정 기간 이후 특정 타입을 제외한 알림을 받은 사용자별 알림 횟수 조회
     *
     * @param userIds 조회할 사용자 ID 목록
     * @param excludedType 제외할 알림 타입
     * @param createdAtAfter 조회 시작 시간
     * @return 사용자 ID별 알림 횟수 Map
     */
    fun countByUserIdsAndTypeNotAndCreatedAtAfter(
        userIds: List<Long>,
        excludedType: NotificationType,
        createdAtAfter: LocalDateTime,
    ): Map<Long, Long> {
        if (userIds.isEmpty()) {
            return emptyMap()
        }

        val query =
            jpql(JDSLExtensions) {
                selectNew<UserNotificationCount>(
                    path(NotificationStoredEntity::userId),
                    count(NotificationStoredEntity::id),
                ).from(
                    entity(NotificationStoredEntity::class),
                ).whereAnd(
                    path(NotificationStoredEntity::userId).`in`(userIds),
                    path(NotificationStoredEntity::type).notIn(listOf(excludedType)),
                    path(NotificationStoredEntity::createdAt).greaterThanOrEqualTo(createdAtAfter),
                ).groupBy(
                    path(NotificationStoredEntity::userId),
                )
            }

        return entityManager
            .createQuery(query, jdslRenderContext)
            .resultList
            .associate { it.userId to it.count }
    }

    /**
     * 특정 기간 이후 특정 타입/파라미터의 알림을 받은 사용자별 알림 횟수 조회
     *
     * @param userIds 조회할 사용자 ID 목록
     * @param type 알림 타입
     * @param parameterValue 알림 파라미터 값
     * @param createdAtAfter 조회 시작 시간
     * @return 사용자 ID별 알림 횟수 Map
     */
    fun countByUserIdsAndTypeAndParameterValueAndCreatedAtAfter(
        userIds: List<Long>,
        type: NotificationType,
        parameterValue: String,
        createdAtAfter: LocalDateTime,
    ): Map<Long, Long> {
        if (userIds.isEmpty()) {
            return emptyMap()
        }

        val query =
            jpql(JDSLExtensions) {
                selectNew<UserNotificationCount>(
                    path(NotificationStoredEntity::userId),
                    count(NotificationStoredEntity::id),
                ).from(
                    entity(NotificationStoredEntity::class),
                ).whereAnd(
                    path(NotificationStoredEntity::userId).`in`(userIds),
                    path(NotificationStoredEntity::type).eq(type),
                    path(NotificationStoredEntity::parameterValue).eq(parameterValue),
                    path(NotificationStoredEntity::createdAt).greaterThanOrEqualTo(createdAtAfter),
                ).groupBy(
                    path(NotificationStoredEntity::userId),
                )
            }

        return entityManager
            .createQuery(query, jdslRenderContext)
            .resultList
            .associate { it.userId to it.count }
    }
}
