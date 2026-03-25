package com.pida.presentation.v2.flowerspotcafe

import com.pida.flowerspot.FlowerSpotCafeAdminFacade
import com.pida.presentation.v2.flowerspotcafe.request.FlowerSpotCafeCreateRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Tag(name = "🌸 Flower Spot Admin API", description = "관리자용 벚꽃 연관 데이터 관리 API")
@RestController
@RequestMapping("/test")
class FlowerSpotCafeController(
    private val flowerSpotCafeAdminFacade: FlowerSpotCafeAdminFacade,
) {
    @PostMapping("/flower-spot-cafe")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "벚꽃 명소 카페 일괄 등록",
        description = "벚꽃 명소 카페 데이터를 일괄 등록합니다. 썸네일 이미지는 등록 후 개별 업로드 API를 사용해주세요.",
    )
    suspend fun addFlowerSpotCafes(
        @RequestBody data: List<FlowerSpotCafeCreateRequest>,
    ) {
        flowerSpotCafeAdminFacade.processBatch(data.map { it.toNewFlowerSpotCafe() })
    }

    @PostMapping("/flower-spot-cafe/{cafeId}/thumbnail", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "벚꽃 명소 카페 썸네일 업로드",
        description = "벚꽃 명소 카페 썸네일 이미지를 업로드합니다.",
    )
    suspend fun uploadThumbnail(
        @PathVariable cafeId: Long,
        @RequestPart("thumbnail") thumbnail: MultipartFile,
    ) {
        flowerSpotCafeAdminFacade.uploadThumbnail(cafeId, thumbnail.bytes)
    }
}
