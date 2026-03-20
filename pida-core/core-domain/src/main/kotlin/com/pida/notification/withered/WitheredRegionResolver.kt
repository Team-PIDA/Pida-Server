package com.pida.notification.withered

import com.pida.place.DistrictRepository
import com.pida.support.extension.logger
import com.pida.support.geo.Region
import org.springframework.stereotype.Component

/**
 * 사용자 좌표를 Region(광역 자치단체)으로 변환하는 컴포넌트
 *
 * 가장 가까운 District를 찾아 해당 District의 region을 반환합니다.
 */
@Component
class WitheredRegionResolver(
    private val districtRepository: DistrictRepository,
) {
    private val logger by logger()

    /**
     * 주어진 좌표를 Region으로 변환합니다.
     *
     * @param latitude 위도
     * @param longitude 경도
     * @return 해당 좌표가 속한 Region, 찾을 수 없으면 null
     */
    fun resolveRegion(
        latitude: Double,
        longitude: Double,
    ): Region? {
        val district = districtRepository.findNearestDistrict(latitude, longitude)

        if (district == null) {
            logger.warn("No district found for coordinates: ($latitude, $longitude)")
            return null
        }

        return district.region
    }
}
