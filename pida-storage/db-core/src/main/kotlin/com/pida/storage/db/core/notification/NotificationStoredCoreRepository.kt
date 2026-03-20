package com.pida.storage.db.core.notification

import com.pida.notification.NotificationStored
import com.pida.notification.NotificationStoredRepository
import com.pida.notification.NotificationType
import com.pida.notification.ReadStatus
import com.pida.storage.db.core.support.findByIdOrElseThrow
import com.pida.support.cursor.Cursor
import com.pida.support.cursor.CursorRequest
import com.pida.support.tx.Tx
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class NotificationStoredCoreRepository(
    private val notificationStoredJpaRepository: NotificationStoredJpaRepository,
    private val notificationStoredCustomRepository: NotificationStoredCustomRepository,
) : NotificationStoredRepository {
    override fun save(notificationStored: NotificationStored.Create): NotificationStored.Info =
        Tx.writeable {
            notificationStoredJpaRepository
                .save(
                    NotificationStoredEntity(notificationStored),
                ).toNotificationStoredInfo()
        }

    override fun saveAll(createAll: List<NotificationStored.Create>) =
        Tx.writeable {
            notificationStoredJpaRepository
                .saveAll(
                    createAll.map { NotificationStoredEntity(it) },
                ).map { it.toNotificationStoredInfo() }
        }

    override fun findById(notificationStoredId: Long): NotificationStored.Info =
        Tx.readable {
            notificationStoredJpaRepository
                .findByIdOrElseThrow(notificationStoredId)
                .toNotificationStoredInfo()
        }

    override fun findByUserIdAndReadStatus(
        userId: Long,
        readStatus: ReadStatus,
    ): List<NotificationStored.Info> =
        Tx.readable {
            notificationStoredJpaRepository
                .findByUserIdAndReadStatus(userId, readStatus)
                .map { it.toNotificationStoredInfo() }
        }

    override fun findAllBy(
        userId: Long,
        cursorRequest: CursorRequest,
    ): Cursor<NotificationStored.Info> =
        Tx.readable {
            notificationStoredCustomRepository.findAllBy(userId, cursorRequest)
        }

    override fun countByUserIdAndReadStatus(
        userId: Long,
        readStatus: ReadStatus?,
    ): Long =
        Tx.readable {
            notificationStoredJpaRepository
                .countByUserIdAndReadStatus(userId, readStatus)
        }

    override fun countByUserIdsAndTypeAndCreatedAtAfter(
        userIds: List<Long>,
        type: NotificationType,
        createdAtAfter: LocalDateTime,
    ): Map<Long, Long> =
        Tx.readable {
            notificationStoredCustomRepository.countByUserIdsAndTypeAndCreatedAtAfter(
                userIds = userIds,
                type = type,
                createdAtAfter = createdAtAfter,
            )
        }

    override fun countByUserIdsAndCreatedAtAfter(
        userIds: List<Long>,
        createdAtAfter: LocalDateTime,
    ): Map<Long, Long> =
        Tx.readable {
            notificationStoredCustomRepository.countByUserIdsAndCreatedAtAfter(
                userIds = userIds,
                createdAtAfter = createdAtAfter,
            )
        }

    override fun countByUserIdsAndTypeNotAndCreatedAtAfter(
        userIds: List<Long>,
        excludedType: NotificationType,
        createdAtAfter: LocalDateTime,
    ): Map<Long, Long> =
        Tx.readable {
            notificationStoredCustomRepository.countByUserIdsAndTypeNotAndCreatedAtAfter(
                userIds = userIds,
                excludedType = excludedType,
                createdAtAfter = createdAtAfter,
            )
        }

    override fun countByUserIdsAndTypeAndParameterValueAndCreatedAtAfter(
        userIds: List<Long>,
        type: NotificationType,
        parameterValue: String,
        createdAtAfter: LocalDateTime,
    ): Map<Long, Long> =
        Tx.readable {
            notificationStoredCustomRepository.countByUserIdsAndTypeAndParameterValueAndCreatedAtAfter(
                userIds = userIds,
                type = type,
                parameterValue = parameterValue,
                createdAtAfter = createdAtAfter,
            )
        }

    override fun markAsRead(notificationId: Long) =
        Tx.writeable {
            val notificationStored =
                notificationStoredJpaRepository
                    .findByIdOrElseThrow(notificationId)

            if (notificationStored.readStatus == ReadStatus.READ) {
                return@writeable
            }

            notificationStored.read()
        }
}
