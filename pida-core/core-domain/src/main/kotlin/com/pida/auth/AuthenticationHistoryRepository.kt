package com.pida.auth

import java.time.LocalDateTime

interface AuthenticationHistoryRepository {
    fun create(newAuthenticationHistory: NewAuthenticationHistory): AuthenticationHistory

    fun findUserKeyWithDeviceWithRefreshToken(
        userKey: String,
        deviceId: String?,
        refreshToken: String,
    ): AuthenticationHistory?

    fun update(updateAuthenticationHistory: UpdateAuthenticationHistory): AuthenticationHistory?

    fun findUserKey(userKey: String): AuthenticationHistory?

    fun findUserId(userId: Long): AuthenticationHistory?

    fun removeToken(userKey: String): List<String>?

    fun remove(token: String): String

    /**
     * 특정 날짜 이후 로그인한 활성 사용자 ID 목록 조회
     *
     * @param sinceDate 기준 날짜
     * @return 사용자 ID 목록
     */
    fun findActiveUsersSince(sinceDate: LocalDateTime): List<Long>
}
