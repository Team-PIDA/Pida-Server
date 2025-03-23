package com.pida.storage.db.core.blooming

import com.pida.blooming.BloomingRepository
import org.springframework.stereotype.Repository

@Repository
class BloomingCoreRepository(
    private val bloomingJpaRepository: BloomingJpaRepository,
) : BloomingRepository
