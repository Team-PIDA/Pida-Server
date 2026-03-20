package com.pida.presentation.v1.region.response

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pida.support.geo.Region
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test

class RegionResponseTest {
    @Test
    fun `지역 목록 응답은 code와 name을 함께 포함한다`() {
        val response = RegionAllResponse.from(Region.entries)

        response.list shouldHaveSize Region.entries.size
        response.list.first() shouldBe RegionResponse(code = Region.SEOUL, name = "서울")

        val json = jacksonObjectMapper().writeValueAsString(response)

        json shouldContain "\"code\":\"SEOUL\""
        json shouldContain "\"name\":\"서울\""
    }
}
