package com.pida.fixture.blooming

import com.navercorp.fixturemonkey.kotlin.setExp
import com.pida.blooming.Blooming
import com.pida.blooming.BloomingDetails
import com.pida.blooming.BloomingStatus
import com.pida.blooming.BloomingStatusDetails
import com.pida.test.helper.fixtureBuilder
import java.time.LocalDateTime

object BloomingFixture {
    val blooming =
        fixtureBuilder<Blooming> {
            setExp(Blooming::id, 1L)
            setExp(Blooming::status, BloomingStatus.BLOOMED)
            setExp(Blooming::userId, 1L)
            setExp(Blooming::flowerSpotId, 1L)
            setExp(Blooming::createdAt, LocalDateTime.of(2025, 4, 2, 14, 0))
        }

    val bloomings =
        listOf(
            fixtureBuilder<Blooming> {
                setExp(Blooming::id, 2L)
                setExp(Blooming::status, BloomingStatus.BLOOMED)
                setExp(Blooming::userId, 1L)
                setExp(Blooming::flowerSpotId, 1L)
                setExp(Blooming::createdAt, LocalDateTime.of(2025, 4, 2, 14, 0))
            },
            fixtureBuilder<Blooming> {
                setExp(Blooming::id, 1L)
                setExp(Blooming::status, BloomingStatus.WITHERED)
                setExp(Blooming::userId, 2L)
                setExp(Blooming::flowerSpotId, 1L)
                setExp(Blooming::createdAt, LocalDateTime.of(2025, 4, 2, 14, 0))
            },
            fixtureBuilder<Blooming> {
                setExp(Blooming::id, 3L)
                setExp(Blooming::status, BloomingStatus.BLOOMED)
                setExp(Blooming::userId, 1L)
                setExp(Blooming::flowerSpotId, 1L)
                setExp(Blooming::createdAt, LocalDateTime.of(2025, 4, 3, 14, 0))
            },
            fixtureBuilder<Blooming> {
                setExp(Blooming::id, 4L)
                setExp(Blooming::status, BloomingStatus.WITHERED)
                setExp(Blooming::userId, 1L)
                setExp(Blooming::flowerSpotId, 1L)
                setExp(Blooming::createdAt, LocalDateTime.of(2025, 4, 4, 14, 0))
            },
            fixtureBuilder<Blooming> {
                setExp(Blooming::id, 5L)
                setExp(Blooming::status, BloomingStatus.BLOOMED)
                setExp(Blooming::userId, 1L)
                setExp(Blooming::flowerSpotId, 1L)
                setExp(Blooming::createdAt, LocalDateTime.of(2025, 4, 5, 14, 0))
            },
            fixtureBuilder<Blooming> {
                setExp(Blooming::id, 6L)
                setExp(Blooming::status, BloomingStatus.BLOOMED)
                setExp(Blooming::userId, 1L)
                setExp(Blooming::flowerSpotId, 1L)
                setExp(Blooming::createdAt, LocalDateTime.of(2025, 4, 6, 14, 0))
            },
            fixtureBuilder<Blooming> {
                setExp(Blooming::id, 7L)
                setExp(Blooming::status, BloomingStatus.BLOOMED)
                setExp(Blooming::userId, 1L)
                setExp(Blooming::flowerSpotId, 1L)
                setExp(Blooming::createdAt, LocalDateTime.of(2025, 4, 7, 14, 0))
            },
        )

    val bloomingDetails =
        fixtureBuilder<BloomingDetails> {
            setExp(BloomingDetails::totalCount, 3L)
            setExp(BloomingDetails::nickname, "피다")
            setExp(BloomingDetails::updatedAt, LocalDateTime.of(2025, 4, 10, 0, 0))
            setExp(
                BloomingDetails::details,
                mapOf(
                    "2025-04-06" to
                        mapOf(
                            "BLOOMED" to BloomingStatusDetails(peopleCount = 1, percentage = 100),
                        ),
                    "2025-04-05" to
                        mapOf(
                            "BLOOMED" to BloomingStatusDetails(peopleCount = 1, percentage = 100),
                        ),
                    "2025-04-04" to
                        mapOf(
                            "BLOOMED" to BloomingStatusDetails(peopleCount = 3, percentage = 100),
                        ),
                    "2025-04-03" to
                        mapOf(
                            "WITHERED" to BloomingStatusDetails(peopleCount = 1, percentage = 50),
                        ),
                    "2025-04-02" to
                        mapOf(
                            "BLOOMED" to BloomingStatusDetails(peopleCount = 1, percentage = 50),
                            "WITHERED" to BloomingStatusDetails(peopleCount = 1, percentage = 50),
                        ),
                ),
            )
        }
}
