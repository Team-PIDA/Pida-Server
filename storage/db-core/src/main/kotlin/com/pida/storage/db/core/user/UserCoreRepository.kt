package com.pida.storage.db.core.user

import com.pida.storage.db.core.support.findByIdAndDeletedAtIsNullOrElseThrow
import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import com.pida.support.tx.TransactionTemplates
import com.pida.support.tx.TxAdvice
import com.pida.support.tx.coExecute
import com.pida.user.NewUser
import com.pida.user.NewUserKey
import com.pida.user.User
import com.pida.user.UserProfile
import com.pida.user.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class UserCoreRepository(
    private val userJpaRepository: UserJpaRepository,
    private val tx: TransactionTemplates,
    private val txAdvice: TxAdvice,
) : UserRepository {
    override fun create(
        newUser: NewUser,
        newUserKey: NewUserKey,
    ): User =
        txAdvice.write {
            return@write userJpaRepository.save(UserEntity(newUser, newUserKey)).toUser()
        }

    override fun readUserById(id: Long): User? =
        txAdvice.readOnly {
            userJpaRepository.findByIdOrNull(id)?.toUser()
        }

    override suspend fun readByUserIdOrNull(id: Long): UserProfile? =
        tx.reader.coExecute {
            userJpaRepository.findByIdAndDeletedAtIsNull(id)?.toProfile()
        }

    override suspend fun readAllByUserIds(userIds: List<Long>): List<UserProfile> =
        tx.reader.coExecute {
            userJpaRepository.findAllByIdIn(userIds).map { it.toProfile() }
        }

    override suspend fun readByUserKey(userKey: String): UserProfile =
        tx.reader.coExecute {
            userJpaRepository.findByUserKey(userKey)?.toProfile()
                ?: throw ErrorException(ErrorType.NOT_FOUND_DATA)
        }

    override suspend fun readByUserId(id: Long): UserProfile =
        tx.reader.coExecute {
            userJpaRepository.findByIdAndDeletedAtIsNullOrElseThrow(id).toProfile()
        }

    override fun readUserByEmail(email: String): User? = userJpaRepository.findByEmail(email)?.toUser()

    override suspend fun existsByEmail(email: String): Boolean =
        tx.reader.coExecute {
            userJpaRepository.existsByEmailAndDeletedAtIsNull(email)
        }

    override suspend fun updateNickname(
        userKey: String,
        nickname: String,
    ): UserProfile =
        tx.writer.coExecute {
            val user = userJpaRepository.findByUserKey(userKey) ?: throw ErrorException(ErrorType.NOT_FOUND_DATA)
            user.updateNickname(nickname)
            return@coExecute user.toProfile()
        }

    override suspend fun updateEmail(
        userKey: String,
        email: String,
    ): UserProfile =
        tx.writer.coExecute {
            val user = userJpaRepository.findByUserKey(userKey) ?: throw ErrorException(ErrorType.NOT_FOUND_DATA)
            user.updateEmail(email)
            return@coExecute user.toProfile()
        }

    override suspend fun delete(userKey: String) = tx.writer.coExecute { userJpaRepository.deleteByUserKey(userKey) }
}
