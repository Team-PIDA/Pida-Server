package com.pida.client.aws.image

import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.security.SecureRandom

@Component
class ImageFileConstructor(
    private val environment: Environment,
) {
    companion object {
        private val RANDOM = SecureRandom() // 파일명 생성용 난수기
        private const val LOWERCASE_A = 97
        private const val LOWERCASE_Z = 122
        private const val FILENAME_LENGTH = 24
    }

    fun imageFileName(): String = "${randomFileName()}.jpeg"

    /**
     * @param prefix [String] S3 key prefix: 도메인별 분리
     */
    fun imageFilePath(
        prefix: String,
        prefixId: Long,
    ): String {
        val profile = environment.activeProfiles.firstOrNull() ?: "dev"
        return "$profile/$prefix/$prefixId"
    }

    private fun randomFileName(): String =
        (1..FILENAME_LENGTH)
            .map { RANDOM.nextInt(LOWERCASE_A, LOWERCASE_Z + 1).toChar() }
            .joinToString("")
}
