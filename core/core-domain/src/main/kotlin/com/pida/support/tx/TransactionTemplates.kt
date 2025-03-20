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
class TransactionTemplates(
    transactionManager: PlatformTransactionManager,
) {
    val writer = TransactionTemplate(transactionManager).apply {
        propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRED
    }

    val newTxWriter = TransactionTemplate(transactionManager).apply {
        propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
    }

    val reader = TransactionTemplate(transactionManager).apply {
        propagationBehavior = TransactionDefinition.PROPAGATION_SUPPORTS
        isReadOnly = true
    }
}

// 코루틴용 확장 함수
suspend fun <T> TransactionTemplate.coExecute(
    coroutineContext: CoroutineContext = Dispatchers.IO,
    block: () -> T
): T = withContext(coroutineContext) {
    this@coExecute.execute { block() }
} ?: throw ErrorException(ErrorType.FAIL_TO_TRANSACTION_TEMPLATE_EXECUTE_ERROR)

// 일반 블록용 확장
fun <T> TransactionTemplate.executeOrThrow(block: () -> T): T =
    this.execute { block() } ?: throw ErrorException(ErrorType.FAIL_TO_TRANSACTION_TEMPLATE_EXECUTE_ERROR)
