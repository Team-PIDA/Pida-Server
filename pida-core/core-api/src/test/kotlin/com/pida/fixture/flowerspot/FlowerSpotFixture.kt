package com.pida.fixture.flowerspot

import com.navercorp.fixturemonkey.kotlin.setExp
import com.pida.flowerspot.FlowerKind
import com.pida.flowerspot.FlowerSpot
import com.pida.flowerspot.GeoJson
import com.pida.flowerspot.Region
import com.pida.test.helper.fixtureBuilder

object FlowerSpotFixture {
    val flowerSpot =
        fixtureBuilder<FlowerSpot> {
            setExp(FlowerSpot::id, 1L)
            setExp(FlowerSpot::address, "서울특별시 강남구 역삼동 123-45")
            setExp(FlowerSpot::streetName, "역삼로")
            setExp(FlowerSpot::district, "역삼동")
            setExp(FlowerSpot::description, "벚나무, 양버즘나무")
            setExp(FlowerSpot::geom, GeoJson.LineString(listOf(listOf(127.123456, 37.123456), listOf(127.654321, 37.654321))))
            setExp(FlowerSpot::pinPoint, GeoJson.Point(listOf(127.123456, 37.123456)))
            setExp(FlowerSpot::region, Region.SEOUL)
            setExp(FlowerSpot::kind, FlowerKind.BLOSSOM)
            setExp(FlowerSpot::deletedAt, null)
        }

    val flowerSpots =
        listOf(
            fixtureBuilder<FlowerSpot> {
                setExp(FlowerSpot::id, 1L)
                setExp(FlowerSpot::address, "서울특별시 강남구 역삼동 123-45")
                setExp(FlowerSpot::streetName, "역삼로")
                setExp(FlowerSpot::district, "역삼동")
                setExp(FlowerSpot::description, "벚나무, 양버즘나무")
                setExp(FlowerSpot::geom, GeoJson.LineString(listOf(listOf(127.123456, 37.123456), listOf(127.654321, 37.654321))))
                setExp(FlowerSpot::pinPoint, GeoJson.Point(listOf(127.123456, 37.123456)))
                setExp(FlowerSpot::region, Region.SEOUL)
                setExp(FlowerSpot::kind, FlowerKind.BLOSSOM)
                setExp(FlowerSpot::deletedAt, null)
            },
            fixtureBuilder<FlowerSpot> {
                setExp(FlowerSpot::id, 2L)
                setExp(FlowerSpot::address, "서울특별시 강남구 역삼동 678-90")
                setExp(FlowerSpot::streetName, "역삼로")
                setExp(FlowerSpot::district, "역삼동")
                setExp(FlowerSpot::description, "벚나무, 양버즘나무")
                setExp(FlowerSpot::geom, GeoJson.LineString(listOf(listOf(127.123456, 37.123456), listOf(127.654321, 37.654321))))
                setExp(FlowerSpot::pinPoint, GeoJson.Point(listOf(127.123456, 37.123456)))
                setExp(FlowerSpot::region, Region.SEOUL)
                setExp(FlowerSpot::kind, FlowerKind.BLOSSOM)
                setExp(FlowerSpot::deletedAt, null)
            },
            fixtureBuilder<FlowerSpot> {
                setExp(FlowerSpot::id, 3L)
                setExp(FlowerSpot::address, "서울특별시 강남구 역삼동 111-22")
                setExp(FlowerSpot::streetName, "역삼로")
                setExp(FlowerSpot::district, "역삼동")
                setExp(FlowerSpot::description, "벚나무, 양버즘나무")
                setExp(FlowerSpot::geom, GeoJson.LineString(listOf(listOf(127.123456, 37.123456), listOf(127.654321, 37.654321))))
                setExp(FlowerSpot::pinPoint, GeoJson.Point(listOf(127.123456, 37.123456)))
                setExp(FlowerSpot::region, Region.SEOUL)
                setExp(FlowerSpot::kind, FlowerKind.BLOSSOM)
                setExp(FlowerSpot::deletedAt, null)
            },
        )
}
