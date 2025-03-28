package com.pida.support.response

import com.pida.support.error.ErrorResponse
import java.time.LocalDateTime

data class ApiResponse<Data>(
    val success: Boolean,
    val status: Int,
    val data: Data? = null,
    val timestamp: LocalDateTime,
) {
    companion object {
        fun <Data> success(
            status: Int,
            data: Data,
        ): ApiResponse<Data> = ApiResponse(true, status, data, LocalDateTime.now())

        fun fail(
            status: Int,
            errorResponse: ErrorResponse,
        ): ApiResponse<ErrorResponse> = ApiResponse(false, status, errorResponse, LocalDateTime.now())
    }
}
