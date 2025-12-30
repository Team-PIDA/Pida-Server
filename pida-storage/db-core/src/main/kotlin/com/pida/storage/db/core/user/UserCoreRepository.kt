package com.pida.storage.db.core.user

import com.pida.storage.db.core.support.findByIdAndDeletedAtIsNullOrElseThrow
import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import com.pida.support.tx.Tx
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
) : UserRepository {
    override fun create(
        newUser: NewUser,
        newUserKey: NewUserKey,
    ): User =
        Tx.writeable {
            userJpaRepository.save(UserEntity(newUser, newUserKey)).toUser()
        }

    override fun readUserById(id: Long): User? =
        Tx.readable {
            userJpaRepository.findByIdOrNull(id)?.toUser()
        }

    override fun readUser(
        loginId: String,
        password: String,
    ): User =
        Tx.readable {
            userJpaRepository.findByEmailAndPasswordAndDeletedAtIsNull(loginId, password)?.toUser()
                ?: throw ErrorException(ErrorType.NOT_FOUND_DATA)
        }

    override suspend fun readByUserIdOrNull(id: Long): UserProfile? =
        Tx.coReadable {
            userJpaRepository.findByIdAndDeletedAtIsNull(id)?.toProfile()
        }

    override suspend fun readAllByUserIds(userIds: List<Long>): List<UserProfile> =
        Tx.coReadable {
            userJpaRepository.findAllByIdIn(userIds).map { it.toProfile() }
        }

    override suspend fun readByUserKey(userKey: String): UserProfile =
        Tx.coReadable {
            userJpaRepository.findByUserKeyAndDeletedAtIsNull(userKey)?.toProfile()
                ?: throw ErrorException(ErrorType.NOT_FOUND_DATA)
        }

    override suspend fun readByUserId(id: Long): UserProfile =
        Tx.coReadable {
            userJpaRepository.findByIdAndDeletedAtIsNullOrElseThrow(id).toProfile()
        }

    override fun readUserByEmail(email: String): SocialUser? =
        Tx.readable {
            userJpaRepository.findByEmailAndDeletedAtIsNull(email)?.toSocialUser()
        }

    override suspend fun existsByEmail(email: String): Boolean =
        Tx.coReadable {
            userJpaRepository.existsByEmailAndDeletedAtIsNull(email)
        }

    override suspend fun existsByNickname(nickname: String): Boolean =
        Tx.coReadable {
            userJpaRepository.existsByNicknameAndDeletedAtIsNull(nickname)
        }

    override fun updateNickname(
        userKey: String,
        nickname: String,
    ): UserProfile =
        Tx.writeable {
            val user = userJpaRepository.findByUserKeyAndDeletedAtIsNull(userKey) ?: throw ErrorException(ErrorType.NOT_FOUND_DATA)
            user.updateNickname(nickname)
            return@writeable user.toProfile()
        }

    override fun updateName(
        userKey: String,
        name: String,
    ): UserProfile =
        Tx.writeable {
            val user = userJpaRepository.findByUserKeyAndDeletedAtIsNull(userKey) ?: throw ErrorException(ErrorType.NOT_FOUND_DATA)
            user.updateName(name)
            user.updateNickname(name)
            return@writeable user.toProfile()
        }

    override suspend fun updateEmail(
        userKey: String,
        email: String,
    ): UserProfile =
        Tx.coWriteable {
            val user = userJpaRepository.findByUserKeyAndDeletedAtIsNull(userKey) ?: throw ErrorException(ErrorType.NOT_FOUND_DATA)
            user.updateEmail(email)
            return@coWriteable user.toProfile()
        }

    override fun delete(userKey: String) =
        Tx.writeable {
            val user = userJpaRepository.findByUserKeyAndDeletedAtIsNull(userKey) ?: throw ErrorException(ErrorType.NOT_FOUND_DATA)
            user.softDelete()
        }
}
