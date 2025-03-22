package com.pida.storage.db.core.user

import com.pida.support.tx.TransactionTemplates
import com.pida.support.tx.TxAdvice
import com.pida.user.UserRepository
import org.springframework.stereotype.Repository

@Repository
class UserCoreRepository(
    private val userJpaRepository: UserJpaRepository,
    private val tx: TransactionTemplates,
    private val txAdvice: TxAdvice,
): UserRepository {
}