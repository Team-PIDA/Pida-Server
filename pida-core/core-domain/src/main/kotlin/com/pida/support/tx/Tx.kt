package com.pida.support.tx

import com.pida.support.annotation.ReadOnlyTransactional
import kotlinx.coroutines.Dispatchers
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import kotlin.coroutines.CoroutineContext

@Component
class Tx(
    _txAdvice: TxAdvice,
) {
    init {
        txAdvice = _txAdvice
    }

    companion object {
        private lateinit var txAdvice: TxAdvice

        fun <T> writeable(function: () -> T): T = txAdvice.write(function)

        fun <T> readable(function: () -> T): T = txAdvice.readOnly(function)

        fun <T> requiresNew(function: () -> T): T = txAdvice.requiresNew(function)

        suspend fun <T> coWriteable(
            coroutineContext: CoroutineContext = Dispatchers.IO,
            function: () -> T,
        ): T = txAdvice.coWrite(coroutineContext, function)

        suspend fun <T> coReadable(
            coroutineContext: CoroutineContext = Dispatchers.IO,
            function: () -> T,
        ): T = txAdvice.coReadOnly(coroutineContext, function)

        suspend fun <T> coRequiresNew(
            coroutineContext: CoroutineContext = Dispatchers.IO,
            function: () -> T,
        ): T = txAdvice.coRequiresNew(coroutineContext, function)
    }

    @Component
    class TxAdvice(
        transactionManager: PlatformTransactionManager,
    ) {
        private val writeTemplate =
            TransactionTemplate(transactionManager).apply {
                propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRED
            }

        private val requiresNewTemplate =
            TransactionTemplate(transactionManager).apply {
                propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
            }

        private val readOnlyTemplate =
            TransactionTemplate(transactionManager).apply {
                propagationBehavior = TransactionDefinition.PROPAGATION_SUPPORTS
                isReadOnly = true
            }

        @Transactional
        fun <T> write(block: () -> T): T = block()

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        fun <T> requiresNew(block: () -> T): T = block()

        @ReadOnlyTransactional
        fun <T> readOnly(block: () -> T): T = block()

        suspend fun <T> coWrite(
            coroutineContext: CoroutineContext = Dispatchers.IO,
            block: () -> T,
        ): T = writeTemplate.coExecute(coroutineContext, block)

        suspend fun <T> coRequiresNew(
            coroutineContext: CoroutineContext = Dispatchers.IO,
            block: () -> T,
        ): T = requiresNewTemplate.coExecute(coroutineContext, block)

        suspend fun <T> coReadOnly(
            coroutineContext: CoroutineContext = Dispatchers.IO,
            block: () -> T,
        ): T = readOnlyTemplate.coExecute(coroutineContext, block)
    }
}
