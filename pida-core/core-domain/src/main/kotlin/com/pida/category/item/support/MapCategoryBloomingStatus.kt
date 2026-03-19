package com.pida.category.item.support

import com.pida.blooming.BloomingDetails
import com.pida.blooming.BloomingStatus

fun BloomingDetails.representativeBloomingStatus(): BloomingStatus =
    details.values
        .flatMap { dailyDetails ->
            dailyDetails.map { (statusName, statusDetails) ->
                BloomingStatus.valueOf(statusName) to statusDetails.peopleCount
            }
        }.groupBy(
            keySelector = { it.first },
            valueTransform = { it.second },
        ).maxByOrNull { (_, counts) -> counts.sum() }
        ?.key
        ?: BloomingStatus.NOT_BLOOMED
