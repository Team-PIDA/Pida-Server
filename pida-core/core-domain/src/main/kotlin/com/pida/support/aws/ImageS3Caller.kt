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

    /**
     * @param prefix [String] 도메인
     * @param prefixId [Long] 도메인 ID
     * @return 최신 미리보기 이미지 (없으면 null)
     */
    suspend fun getPreviewImage(
        prefix: String,
        prefixId: Long,
    ): S3ImageInfo?

    /**
     * S3 key로부터 presigned GET URL 생성 (로컬 서명, 네트워크 호출 없음)
     *
     * @param s3Key [String] S3 객체 전체 키 (e.g. "prod/flowerspot/42/abcdef.jpeg")
     */
    fun generatePresignedUrl(s3Key: String): String

    /**
     * 서버 사이드 이미지 업로드
     *
     * @param prefix [String] 도메인
     * @param prefixId [Long] 도메인 ID
     * @param subPath [String] 추가 하위 경로 (e.g. "thumbnail")
     * @param contentType [String] 이미지 Content-Type
     * @param bytes [ByteArray] 이미지 데이터
     * @return 업로드된 이미지의 공개 URL
     */
    fun uploadImage(
        prefix: String,
        prefixId: Long,
        subPath: String,
        contentType: String,
        bytes: ByteArray,
    ): S3UploadResult
}
