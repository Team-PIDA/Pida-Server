package com.pida.authentication.storage.db.core.auth
import com.pida.authentication.domain.auth.AuthenticationPida
import com.pida.authentication.domain.auth.AuthenticationSns
import com.pida.authentication.domain.auth.LoginIdWithSocialType
import com.pida.authentication.domain.auth.NewAuthenticationSocial
import com.pida.authentication.domain.auth.SocialType
import com.pida.authentication.domain.auth.repository.AuthenticationRepository
import com.pida.authentication.storage.db.core.support.findByIdOrElseThrow
import com.pida.authentication.support.error.AuthenticationErrorException
import com.pida.authentication.support.error.AuthenticationErrorType
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Repository
class AuthenticationCoreRepository(
    private val repository: AuthenticationJpaRepository,
) : AuthenticationRepository {
    override fun findBy(loginId: String): AuthenticationPida? =
        repository
            .findByLoginId(
                loginId = loginId,
            )?.toAuthenticationPida()

    override fun findBy(
        socialId: String,
        socialType: SocialType,
    ): AuthenticationSns? =
        repository
            .findBySocialIdAndSocialType(
                socialId = socialId,
                socialType = socialType,
            )?.toAuthenticationSns()

    override fun readAuthentication(userKey: String): AuthenticationPida {
        val authentication =
            repository.findByUserKey(userKey)
                ?: throw AuthenticationErrorException(AuthenticationErrorType.NOT_FOUND_DATA)
        return authentication.toAuthenticationPida()
    }

    override fun createAuthentication(
        userId: Long,
        userKey: String,
        newAuthenticationSocial: NewAuthenticationSocial,
    ): AuthenticationSns =
        repository
            .save(
                AuthenticationEntity(
                    userId = userId,
                    userKey = userKey,
                    newAuthenticationSocial = newAuthenticationSocial,
                ),
            ).toAuthenticationSns()

    override fun verifyLoginId(loginId: String): Boolean = repository.existsByLoginId(loginId)

    override fun verifySocialId(socialId: String): Boolean = repository.existsBySocialId(socialId)

    @Transactional
    override fun withdrawal(userKey: String) {
        repository.deleteByUserKey(userKey)
    }

    @Transactional
    override fun updatePassword(
        userKey: String,
        hashedNewPassword: String,
    ) {
        val authentication =
            repository.findByUserKey(userKey)
                ?: throw AuthenticationErrorException(AuthenticationErrorType.NOT_FOUND_DATA)
        authentication.updatePassword(hashedNewPassword)
    }

    @Transactional
    override fun updatePassword(
        userKey: String,
        loginId: String,
        hashedNewPassword: String,
    ) {
        val authentication =
            repository.findByUserKeyAndLoginId(userKey, loginId)
                ?: throw AuthenticationErrorException(AuthenticationErrorType.NOT_FOUND_DATA)
        authentication.updatePassword(hashedNewPassword)
    }

    @Transactional
    override fun updateLoginId(
        id: Long,
        loginId: String,
    ) {
        val authentication = repository.findByIdOrElseThrow(id)
        authentication.updateLoginId(loginId)
    }

    override fun readLoginId(userKey: String): List<LoginIdWithSocialType>? =
        repository.findAllByUserKey(userKey)?.map {
            it.toLoginIdWithSocialType()
        }

    override fun readyByLoginIdWithSocialType(userKey: String): LoginIdWithSocialType? =
        repository.findByUserKey(userKey)?.toLoginIdWithSocialType()
}
