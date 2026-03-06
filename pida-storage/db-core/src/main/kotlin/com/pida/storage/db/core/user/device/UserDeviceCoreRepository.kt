package com.pida.storage.db.core.user.device

import com.pida.support.tx.Tx
import com.pida.user.device.UserDevice
import com.pida.user.device.UserDeviceRepository
import org.springframework.stereotype.Repository

@Repository
class UserDeviceCoreRepository(
    private val userDeviceJpaRepository: UserDeviceJpaRepository,
) : UserDeviceRepository {
    override fun save(create: UserDevice.Create) =
        Tx.writeable {
            userDeviceJpaRepository
                .save(
                    UserDeviceEntity(
                        create,
                    ),
                ).toUserDevice()
        }

    override fun findAll(): List<UserDevice.Info> =
        Tx.readable {
            userDeviceJpaRepository
                .findAllByDeletedAtIsNull()
                .map { it.toUserDevice() }
        }

    override fun findLastByUserId(userId: Long): UserDevice.Info? =
        Tx.readable {
            userDeviceJpaRepository
                .findFirstByUserIdAndDeletedAtIsNullOrderByCreatedAtDescIdDesc(userId)
                ?.toUserDevice()
        }

    override fun findLastByUserIds(userIds: List<Long>): Map<Long, UserDevice.Info> =
        Tx.readable {
            if (userIds.isEmpty()) {
                emptyMap()
            } else {
                userDeviceJpaRepository
                    .findLastByUserIds(userIds.distinct())
                    .map { it.toUserDevice() }
                    .associateBy { it.userId }
            }
        }

    override fun findAllByUserKey(userKey: String): List<UserDevice.Info> =
        Tx.readable {
            userDeviceJpaRepository
                .findAllByUserKeyAndDeletedAtIsNull(userKey)
                .map { it.toUserDevice() }
        }

    override fun findAllByUserId(userId: Long): List<UserDevice.Info> =
        Tx.readable {
            userDeviceJpaRepository
                .findAllByUserIdAndDeletedAtIsNull(userId)
                .map { it.toUserDevice() }
        }

    override fun softDeleteBy(id: Long) {
        Tx.writeable {
            userDeviceJpaRepository.findById(id).ifPresent { entity ->
                entity.softDelete()
                userDeviceJpaRepository.save(entity)
            }
        }
    }
}
