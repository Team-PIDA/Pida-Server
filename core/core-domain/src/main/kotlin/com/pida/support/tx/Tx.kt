package com.pida.support.tx

import com.pida.support.annotation.ReadOnlyTransactional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import kotlin.coroutines.CoroutineContext

object Tx {
    @Suppress("ktlint:standard:backing-property-naming")
    private lateinit var _txRunner: TxRunner

    fun initialize(txRunner: TxRunner) {
        _txRunner = txRunner
    }

    fun <T> writeable(function: () -> T): T = _txRunner.runTx(function)

    fun <T> readable(function: () -> T): T = _txRunner.runReadOnly(function)

    fun <T> requiresNew(function: () -> T): T = _txRunner.runRequiresNew(function)

    suspend fun <T> coWriteable(
        coroutineContext: CoroutineContext = Dispatchers.IO,
        function: suspend () -> T,
    ): T = withContext(coroutineContext) { _txRunner.runTxSuspend(function) }

    suspend fun <T> coReadable(
        coroutineContext: CoroutineContext = Dispatchers.IO,
        function: suspend () -> T,
    ): T = withContext(coroutineContext) { _txRunner.runReadOnlySuspend(function) }

    suspend fun <T> coRequiresNew(
        coroutineContext: CoroutineContext = Dispatchers.IO,
        function: suspend () -> T,
    ): T = withContext(coroutineContext) { _txRunner.runRequiresNewSuspend(function) }
}

@Configuration
class TxConfig {
    @Bean("txInitBean")
    fun txInitialize(txRunner: TxRunner): InitializingBean = InitializingBean { Tx.initialize(txRunner) }
}

@Component
class TxRunner {
    @Transactional
    fun <T> runTx(block: () -> T): T = block()

    @ReadOnlyTransactional
    fun <T> runReadOnly(block: () -> T): T = block()

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun <T> runRequiresNew(block: () -> T): T = block()

    // ✅ suspend 함수 지원
    @Transactional
    suspend fun <T> runTxSuspend(block: suspend () -> T): T = block()

    @ReadOnlyTransactional
    suspend fun <T> runReadOnlySuspend(block: suspend () -> T): T = block()

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    suspend fun <T> runRequiresNewSuspend(block: suspend () -> T): T = block()
}
