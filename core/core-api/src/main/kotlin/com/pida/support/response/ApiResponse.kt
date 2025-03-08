package com.pida.support.response

import com.pida.support.error.ErrorResponse
import java.time.LocalDateTime

data class ApiResponse(
    val success: Boolean,
    val status: Int,
    val data: Any,
    val timestamp: LocalDateTime,
) {
    companion object {
        @JvmStatic
        fun success(
            status: Int,
            data: Any,
        ) = ApiResponse(true, status, data, LocalDateTime.now())

        fun fail(
            status: Int,
            errorResponse: ErrorResponse,
        ) = ApiResponse(false, status, errorResponse, LocalDateTime.now())
    }
}
