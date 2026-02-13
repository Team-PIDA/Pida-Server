package com.pida.support.aws

interface ImageS3Caller {
    /**
     * @param userId [Long] 사용자 ID
     * @param prefix [String] 도메인
     * @param prefixId [Long] 도메인 ID
     */
    fun createUploadUrl(
        userId: Long,
        prefix: String,
        prefixId: Long,
    ): S3ImageUrl

    /**
     * @param prefix [String] 도메인
     * @param prefixId [Long] 도메인 ID
     * @param fileName [String] 파일명 (null일 경우 해당 경로 아래 모든 이미지 조회)
     */
    suspend fun getImageUrl(
        prefix: String,
        prefixId: Long,
        fileName: String?,
    ): List<S3ImageInfo>
}
