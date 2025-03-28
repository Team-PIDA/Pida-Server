package com.pida.blooming

import org.springframework.stereotype.Component

@Component
class BloomingAppender(
    private val bloomingRepository: BloomingRepository,
) {
    fun add(newBlooming: NewBlooming): Blooming = bloomingRepository.add(newBlooming)
}
