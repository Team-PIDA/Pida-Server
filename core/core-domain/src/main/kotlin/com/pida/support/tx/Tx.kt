package com.pida.support.tx

import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate
import kotlin.coroutines.CoroutineContext

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

    fun <T> write(block: () -> T): T = execute(writeTemplate, block)

    fun <T> requiresNew(block: () -> T): T = execute(requiresNewTemplate, block)

    fun <T> readOnly(block: () -> T): T = execute(readOnlyTemplate, block)

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

    private fun <T> execute(
        template: TransactionTemplate,
        block: () -> T,
    ): T =
        template.execute { block() }
            ?: throw ErrorException(ErrorType.FAIL_TO_TRANSACTION_TEMPLATE_EXECUTE_ERROR)
}

suspend fun <T> TransactionTemplate.coExecute(
    coroutineContext: CoroutineContext = Dispatchers.IO,
    block: () -> T,
): T =
    withContext(coroutineContext) {
        this@coExecute.execute { block() }
    } ?: throw ErrorException(ErrorType.FAIL_TO_TRANSACTION_TEMPLATE_EXECUTE_ERROR)
