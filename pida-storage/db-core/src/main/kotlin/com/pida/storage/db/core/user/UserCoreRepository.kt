package com.pida.storage.db.core.user

import com.pida.storage.db.core.support.findByIdAndDeletedAtIsNullOrElseThrow
import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import com.pida.support.tx.TransactionTemplates
import com.pida.support.tx.TxAdvice
import com.pida.support.tx.coExecute
import com.pida.user.NewUser
import com.pida.user.NewUserKey
import com.pida.user.SocialUser
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
            userJpaRepository.save(UserEntity(newUser, newUserKey)).toUser()
        }

    override fun readUserById(id: Long): User? =
        txAdvice.readOnly {
            userJpaRepository.findByIdOrNull(id)?.toUser()
        }

    override fun readUser(
        loginId: String,
        password: String,
    ): User =
        txAdvice.readOnly {
            userJpaRepository.findByEmailAndPasswordAndDeletedAtIsNull(loginId, password)?.toUser()
                ?: throw ErrorException(ErrorType.NOT_FOUND_DATA)
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
            userJpaRepository.findByUserKeyAndDeletedAtIsNull(userKey)?.toProfile()
                ?: throw ErrorException(ErrorType.NOT_FOUND_DATA)
        }

    override suspend fun readByUserId(id: Long): UserProfile =
        tx.reader.coExecute {
            userJpaRepository.findByIdAndDeletedAtIsNullOrElseThrow(id).toProfile()
        }

    override fun readUserByEmail(email: String): SocialUser? =
        txAdvice.readOnly {
            userJpaRepository.findByEmailAndDeletedAtIsNull(email)?.toSocialUser()
        }

    override fun existsByEmail(email: String): Boolean =
        txAdvice.readOnly {
            userJpaRepository.existsByEmailAndDeletedAtIsNull(email)
        }

    override fun existsByNickname(nickname: String): Boolean =
        txAdvice.readOnly {
            userJpaRepository.existsByNicknameAndDeletedAtIsNull(nickname)
        }

    override fun updateNickname(
        userKey: String,
        nickname: String,
    ): UserProfile =
        txAdvice.write {
            val user = userJpaRepository.findByUserKeyAndDeletedAtIsNull(userKey) ?: throw ErrorException(ErrorType.NOT_FOUND_DATA)
            user.updateNickname(nickname)
            return@write user.toProfile()
        }

    override fun updateName(
        userKey: String,
        name: String,
    ): UserProfile =
        txAdvice.write {
            val user = userJpaRepository.findByUserKeyAndDeletedAtIsNull(userKey) ?: throw ErrorException(ErrorType.NOT_FOUND_DATA)
            user.updateName(name)
            user.updateNickname(name)
            return@write user.toProfile()
        }

    override suspend fun updateEmail(
        userKey: String,
        email: String,
    ): UserProfile =
        tx.writer.coExecute {
            val user = userJpaRepository.findByUserKeyAndDeletedAtIsNull(userKey) ?: throw ErrorException(ErrorType.NOT_FOUND_DATA)
            user.updateEmail(email)
            return@coExecute user.toProfile()
        }

    override fun delete(userKey: String) =
        txAdvice.write {
            val user = userJpaRepository.findByUserKeyAndDeletedAtIsNull(userKey) ?: throw ErrorException(ErrorType.NOT_FOUND_DATA)
            user.softDelete()
        }
}
