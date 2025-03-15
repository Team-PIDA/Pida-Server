package com.pida.authentication.storage.db.core.auth
import com.pida.authentication.domain.auth.SocialType
import org.springframework.data.jpa.repository.JpaRepository

interface AuthenticationJpaRepository : JpaRepository<AuthenticationEntity, Long> {
    fun findByLoginId(loginId: String): AuthenticationEntity?

    fun findBySocialIdAndSocialType(
        socialId: String,
        socialType: SocialType,
    ): AuthenticationEntity?

    fun existsByLoginId(loginId: String): Boolean

    fun existsBySocialId(socialId: String): Boolean

    fun findByUserKey(userKey: String): AuthenticationEntity?

    fun findByUserKeyAndLoginId(
        userKey: String,
        loginId: String,
    ): AuthenticationEntity?

    fun deleteByUserKey(userKey: String)

    fun findAllByUserKey(userKey: String): List<AuthenticationEntity>?
}
