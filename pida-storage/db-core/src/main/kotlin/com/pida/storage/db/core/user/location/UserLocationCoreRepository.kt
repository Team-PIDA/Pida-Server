package com.pida.storage.db.core.user.location

import com.pida.support.tx.Tx
import com.pida.user.location.UserLocation
import com.pida.user.location.UserLocationRepository
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.stereotype.Repository

@Repository
class UserLocationCoreRepository(
    private val userLocationJpaRepository: UserLocationJpaRepository,
) : UserLocationRepository {
    companion object {
        private val GEOMETRY_FACTORY = GeometryFactory(PrecisionModel(), 4326)
    }

    override fun findByUserId(userId: Long): UserLocation.Info? =
        Tx.readable {
            userLocationJpaRepository
                .findByUserId(userId)
                ?.toUserLocation()
        }

    override fun findByUserIds(userIds: List<Long>): List<UserLocation.Info> =
        Tx.readable {
            if (userIds.isEmpty()) {
                emptyList()
            } else {
                userLocationJpaRepository
                    .findByUserIdIn(userIds)
                    .map { it.toUserLocation() }
            }
        }

    override fun findWithinRadius(
        latitude: Double,
        longitude: Double,
        radiusMeters: Double,
    ): List<UserLocation.Info> =
        Tx.readable {
            userLocationJpaRepository
                .findWithinRadius(
                    latitude = latitude,
                    longitude = longitude,
                    radiusMeters = radiusMeters,
                ).map { it.toUserLocation() }
        }

    override fun saveOrUpdate(
        userId: Long,
        latitude: Double,
        longitude: Double,
    ): UserLocation.Info =
        Tx.writeable {
            val point = GEOMETRY_FACTORY.createPoint(Coordinate(longitude, latitude))
            val existingEntity = userLocationJpaRepository.findByUserId(userId)

            if (existingEntity != null) {
                // JPA dirty checking을 활용하여 자동으로 UPDATE 쿼리 실행
                existingEntity.updateLocation(point)
                existingEntity.toUserLocation()
            } else {
                userLocationJpaRepository
                    .save(
                        UserLocationEntity(
                            UserLocation.Create(
                                userId = userId,
                                location = point,
                            ),
                        ),
                    ).toUserLocation()
            }
        }
}
