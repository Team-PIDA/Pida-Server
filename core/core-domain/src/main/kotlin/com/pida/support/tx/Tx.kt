package com.pida.support.tx

import com.pida.support.annotation.ReadOnlyTransactional
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import kotlin.coroutines.CoroutineContext

@Component
class Tx(
    private val txAdvice: TxAdvice,
) {
    init {
        Tx.txAdvice = txAdvice
    }

    companion object {
        private lateinit var txAdvice: TxAdvice

        fun <T> writeable(block: () -> T): T = txAdvice.writeable(block)

        fun <T> readable(block: () -> T): T = txAdvice.readable(block)

        fun <T> requiresNew(block: () -> T): T = txAdvice.requiresNew(block)

        suspend fun <T> coWriteable(
            coroutineContext: CoroutineContext = Dispatchers.IO,
            block: suspend () -> T,
        ): T = withContext(coroutineContext) { txAdvice.coWriteable(block) }

        suspend fun <T> coReadable(
            coroutineContext: CoroutineContext = Dispatchers.IO,
            block: suspend () -> T,
        ): T = withContext(coroutineContext) { txAdvice.coReadable(block) }

        suspend fun <T> coRequiresNew(
            coroutineContext: CoroutineContext = Dispatchers.IO,
            block: suspend () -> T,
        ): T = withContext(coroutineContext) { txAdvice.coRequiresNew(block) }
    }

    @PostConstruct
    fun init() {
        Tx.txAdvice = txAdvice
    }

    @Component
    class TxAdvice {
        @Transactional
        fun <T> writeable(block: () -> T): T = block()

        @ReadOnlyTransactional
        fun <T> readable(block: () -> T): T = block()

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        fun <T> requiresNew(block: () -> T): T = block()

        @Transactional
        suspend fun <T> coWriteable(block: suspend () -> T): T = block()

        @ReadOnlyTransactional
        suspend fun <T> coReadable(block: suspend () -> T): T = block()

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        suspend fun <T> coRequiresNew(block: suspend () -> T): T = block()
    }
}
