package com.pida.flowerspot.perf

import com.pida.blooming.Blooming
import com.pida.blooming.BloomingStatus
import com.pida.flowerspot.FlowerKind
import com.pida.flowerspot.FlowerSpot
import com.pida.flowerspot.FlowerSpotDetails
import com.pida.flowerspot.FlowerSpotType
import com.pida.support.aws.S3ImageInfo
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import kotlin.math.roundToLong
import kotlin.system.measureNanoTime

object FlowerSpotPerformanceBenchmarkRunner {
    @JvmStatic
    fun main(args: Array<String>) {
        val fixture = BenchmarkFixture.create()
        val results =
            listOf(
                benchmark(
                    scenario = "Blooming aggregation",
                    legacy = { legacyBloomingAggregation(fixture.spots, fixture.bloomings) },
                    current = { currentBloomingAggregation(fixture.spots, fixture.bloomings) },
                ),
                benchmark(
                    scenario = "Preview image selection",
                    legacy = { legacyPreviewSelection(fixture.spots, fixture.imageStore) },
                    current = { currentPreviewSelection(fixture.spots, fixture.imageStore) },
                ),
                benchmark(
                    scenario = "Flower spot list assembly",
                    legacy = { legacyListAssembly(fixture.spots, fixture.bloomings, fixture.imageStore) },
                    current = { currentListAssembly(fixture.spots, fixture.bloomings, fixture.imageStore) },
                ),
            )

        val report = renderReport(fixture, results)
        val reportPath = Path.of("build", "reports", "performance", "flower-spot-latency.md")
        Files.createDirectories(reportPath.parent)
        Files.writeString(reportPath, report)

        println(report)
        println()
        println("Report written to ${reportPath.toAbsolutePath()}")
    }

    private fun benchmark(
        scenario: String,
        warmups: Int = 5,
        iterations: Int = 12,
        legacy: () -> Any,
        current: () -> Any,
    ): BenchmarkResult {
        repeat(warmups) {
            blackhole = legacy()
            blackhole = current()
        }

        val legacySamples = LongArray(iterations) { measureNanoTime { blackhole = legacy() } }
        val currentSamples = LongArray(iterations) { measureNanoTime { blackhole = current() } }

        val legacyMedianMs = legacySamples.medianMillis()
        val currentMedianMs = currentSamples.medianMillis()
        val speedupPercent = ((legacyMedianMs - currentMedianMs) / legacyMedianMs) * 100.0

        return BenchmarkResult(
            scenario = scenario,
            legacyMedianMs = legacyMedianMs,
            currentMedianMs = currentMedianMs,
            speedupPercent = speedupPercent,
            ratio = legacyMedianMs / currentMedianMs,
        )
    }

    private fun legacyBloomingAggregation(
        spots: List<FlowerSpot>,
        bloomings: List<Blooming>,
    ): List<Pair<Long, BloomingStatus>> =
        spots.map { flowerSpot ->
            val recentBloomings = bloomings.groupBy { it.flowerSpotId }[flowerSpot.id] ?: emptyList()
            recentBloomings.size.toLong() to representativeStatus(recentBloomings)
        }

    private fun currentBloomingAggregation(
        spots: List<FlowerSpot>,
        bloomings: List<Blooming>,
    ): List<Pair<Long, BloomingStatus>> {
        val bloomingsBySpotId = bloomings.groupBy { it.flowerSpotId }

        return spots.map { flowerSpot ->
            val recentBloomings = bloomingsBySpotId[flowerSpot.id] ?: emptyList()
            recentBloomings.size.toLong() to representativeStatus(recentBloomings)
        }
    }

    private fun legacyPreviewSelection(
        spots: List<FlowerSpot>,
        imageStore: SyntheticImageStore,
    ): List<String?> =
        spots.map { flowerSpot ->
            imageStore
                .listImages(flowerSpot.id)
                .sortedByDescending { it.uploadedAt }
                .firstOrNull()
                ?.url
        }

    private fun currentPreviewSelection(
        spots: List<FlowerSpot>,
        imageStore: SyntheticImageStore,
    ): List<String?> =
        spots.map { flowerSpot ->
            imageStore.previewImage(flowerSpot.id)?.url
        }

    private fun legacyListAssembly(
        spots: List<FlowerSpot>,
        bloomings: List<Blooming>,
        imageStore: SyntheticImageStore,
    ): List<FlowerSpotDetails> =
        spots.map { flowerSpot ->
            FlowerSpotDetails.of(
                flowerSpot = flowerSpot,
                bloomings = bloomings.groupBy { it.flowerSpotId }[flowerSpot.id] ?: emptyList(),
                images = imageStore.listImages(flowerSpot.id),
            )
        }

    private fun currentListAssembly(
        spots: List<FlowerSpot>,
        bloomings: List<Blooming>,
        imageStore: SyntheticImageStore,
    ): List<FlowerSpotDetails> {
        val bloomingsBySpotId = bloomings.groupBy { it.flowerSpotId }

        return spots.map { flowerSpot ->
            FlowerSpotDetails.of(
                flowerSpot = flowerSpot,
                bloomings = bloomingsBySpotId[flowerSpot.id] ?: emptyList(),
                images = listOfNotNull(imageStore.previewImage(flowerSpot.id)),
            )
        }
    }

    private fun representativeStatus(bloomings: List<Blooming>): BloomingStatus =
        bloomings
            .groupBy { it.status }
            .maxByOrNull { it.value.size }
            ?.key ?: BloomingStatus.NOT_BLOOMED

    private fun renderReport(
        fixture: BenchmarkFixture,
        results: List<BenchmarkResult>,
    ): String {
        val table =
            results.joinToString(separator = "\n") { result ->
                "| ${result.scenario} | ${result.legacyMedianMs.formatMillis()} ms | ${result.currentMedianMs.formatMillis()} ms | ${result.speedupPercent.formatPercent()} | ${result.ratio.formatRatio()}x |"
            }

        return buildString {
            appendLine("# Flower Spot Latency Comparison")
            appendLine()
            appendLine("Synthetic benchmark comparing the legacy application-layer flow and the current refactored flow.")
            appendLine()
            appendLine("## Dataset")
            appendLine()
            appendLine("- Flower spots: ${fixture.spots.size}")
            appendLine("- Bloomings per spot: ${fixture.bloomingsPerSpot}")
            appendLine("- Images per spot: ${fixture.imagesPerSpot}")
            appendLine("- Warmups: 5")
            appendLine("- Measured iterations: 12")
            appendLine()
            appendLine("## Results")
            appendLine()
            appendLine("| Scenario | Legacy median | Current median | Improvement | Speedup |")
            appendLine("| --- | ---: | ---: | ---: | ---: |")
            appendLine(table)
            appendLine()
            appendLine("## Notes")
            appendLine()
            appendLine("- `Blooming aggregation` isolates repeated `groupBy` removal.")
            appendLine("- `Preview image selection` compares listing all image metadata against selecting one preview image.")
            appendLine("- `Flower spot list assembly` combines both changes into the end-to-end list payload composition path.")
            appendLine("- This benchmark is synthetic and does not replace PostGIS `EXPLAIN ANALYZE` validation for bbox queries.")
        }
    }

    private fun LongArray.medianMillis(): Double {
        val sorted = sorted()
        val middle = size / 2

        return if (size % 2 == 0) {
            ((sorted[middle - 1] + sorted[middle]) / 2.0) / 1_000_000.0
        } else {
            sorted[middle] / 1_000_000.0
        }
    }

    private fun Double.formatMillis(): String = ((this * 100.0).roundToLong() / 100.0).toString()

    private fun Double.formatPercent(): String = ((this * 100.0).roundToLong() / 100.0).toString() + "%"

    private fun Double.formatRatio(): String = ((this * 100.0).roundToLong() / 100.0).toString()

    private var blackhole: Any? = null
}

private data class BenchmarkResult(
    val scenario: String,
    val legacyMedianMs: Double,
    val currentMedianMs: Double,
    val speedupPercent: Double,
    val ratio: Double,
)

private data class BenchmarkFixture(
    val spots: List<FlowerSpot>,
    val bloomings: List<Blooming>,
    val imageStore: SyntheticImageStore,
    val bloomingsPerSpot: Int,
    val imagesPerSpot: Int,
) {
    companion object {
        fun create(
            spotCount: Int = 400,
            bloomingsPerSpot: Int = 8,
            imagesPerSpot: Int = 6,
        ): BenchmarkFixture {
            val spots =
                (1..spotCount).map { id ->
                    FlowerSpot(
                        id = id.toLong(),
                        address = "서울시 강남구 ${id}번지",
                        streetName = "벚꽃길-$id",
                        district = "역삼동",
                        description = "테스트용 벚꽃길-$id",
                        geom =
                            GeoJson.LineString(
                                listOf(
                                    listOf(127.0 + id * 0.0001, 37.5 + id * 0.0001),
                                    listOf(127.0005 + id * 0.0001, 37.5005 + id * 0.0001),
                                ),
                            ),
                        pinPoint = GeoJson.Point(listOf(127.0 + id * 0.0001, 37.5 + id * 0.0001)),
                        region = if (id % 2 == 0) Region.SEOUL else Region.BUSAN,
                        kind = FlowerKind.BLOSSOM,
                        type = FlowerSpotType.WALKING_TRAIL,
                        deletedAt = null,
                    )
                }

            val bloomings =
                spots.flatMap { spot ->
                    (0 until bloomingsPerSpot).map { offset ->
                        Blooming(
                            id = spot.id * 100 + offset,
                            userId = offset.toLong(),
                            flowerSpotId = spot.id,
                            flowerEventId = null,
                            flowerSpotCafeId = null,
                            status =
                                when (offset % 3) {
                                    0 -> BloomingStatus.BLOOMED
                                    1 -> BloomingStatus.LITTLE
                                    else -> BloomingStatus.NOT_BLOOMED
                                },
                            createdAt = LocalDateTime.of(2026, 3, 23, 12, 0).minusHours(offset.toLong()),
                        )
                    }
                }

            val imageSeeds =
                spots.associate { spot ->
                    spot.id to
                        (0 until imagesPerSpot).map { offset ->
                            SyntheticImageSeed(
                                fileName = "spot-${spot.id}-$offset.jpeg",
                                uploadedAt = LocalDateTime.of(2026, 3, 23, 12, 0).minusMinutes(offset.toLong()),
                            )
                        }
                }

            return BenchmarkFixture(
                spots = spots,
                bloomings = bloomings,
                imageStore = SyntheticImageStore(imageSeeds),
                bloomingsPerSpot = bloomingsPerSpot,
                imagesPerSpot = imagesPerSpot,
            )
        }
    }
}

private data class SyntheticImageSeed(
    val fileName: String,
    val uploadedAt: LocalDateTime,
)

private class SyntheticImageStore(
    private val imageSeeds: Map<Long, List<SyntheticImageSeed>>,
) {
    fun listImages(spotId: Long): List<S3ImageInfo> =
        imageSeeds[spotId]
            .orEmpty()
            .map { seed -> seed.toImageInfo(spotId) }

    fun previewImage(spotId: Long): S3ImageInfo? =
        imageSeeds[spotId]
            .orEmpty()
            .maxByOrNull(SyntheticImageSeed::uploadedAt)
            ?.toImageInfo(spotId)

    private fun SyntheticImageSeed.toImageInfo(spotId: Long): S3ImageInfo =
        S3ImageInfo(
            url = "https://cdn.example.com/flowerspot/$spotId/$fileName",
            uploadedAt = uploadedAt,
        )
}
