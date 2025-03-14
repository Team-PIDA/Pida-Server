package com.pida.presentation.advice

import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorResponse
import com.pida.support.error.ErrorType
import com.pida.support.extension.logger
import com.pida.support.response.ApiResponse
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice(basePackages = ["com.pida"])
class ApiExceptionAdvice : ResponseEntityExceptionHandler() {
    companion object {
        private val log by logger()
    }

    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val errorResponse =
            ErrorResponse.of(ex.javaClass.simpleName, ex.message!!)
        return super.handleExceptionInternal(ex, errorResponse, headers, statusCode, request)
    }

    /**
     * javax.validation.Valid or @Validated 으로 binding error 발생 시 발생한다. HttpMessageConverter 에서 등록한
     * HttpMessageConverter binding 못할 경우 발생 주로 @RequestBody, @RequestPart 어노테이션에서 발생
     */
    override fun handleMethodArgumentNotValid(
        e: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        log.error("MethodArgumentNotValidException : {}", e.message, e)
        val errorMessage: String? = e.bindingResult.allErrors[0].defaultMessage
        val errorResponse =
            ErrorResponse.of(e.javaClass.getSimpleName(), errorMessage!!)
        val response: ApiResponse = ApiResponse.fail(status.value(), errorResponse)
        return ResponseEntity.status(status).body(response)
    }

    /** Request Param Validation 예외 처리  */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(e: ConstraintViolationException): ResponseEntity<ApiResponse> {
        log.error("ConstraintViolationException: {}", e.message, e)

        val bindingErrors =
            e.constraintViolations.associate { violation ->
                val path = violation.propertyPath.toString().substringAfterLast(".", "unknown")
                path to violation.message
            }

        val errorResponse = ErrorResponse.of(e.javaClass.simpleName, bindingErrors.toString())
        val response = ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), errorResponse)

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    /** PathVariable, RequestParam, RequestHeader, RequestBody 에서 타입이 일치하지 않을 경우 발생  */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    protected fun handleMethodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException): ResponseEntity<ApiResponse> {
        log.error("MethodArgumentTypeMismatchException : {}", e.message, e)
        val errorCode: ErrorType = ErrorType.METHOD_ARGUMENT_TYPE_MISMATCH
        val errorResponse =
            ErrorResponse.of(e.javaClass.getSimpleName(), errorCode.message)
        val response: ApiResponse =
            ApiResponse.fail(errorCode.status, errorResponse)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    /** 지원하지 않은 HTTP method 호출 할 경우 발생  */
    override fun handleHttpRequestMethodNotSupported(
        e: HttpRequestMethodNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        log.error("HttpRequestMethodNotSupportedException : {}", e.message, e)
        val errorCode: ErrorType = ErrorType.METHOD_NOT_ALLOWED
        val errorResponse =
            ErrorResponse.of(e.javaClass.getSimpleName(), errorCode.message)
        val response: ApiResponse =
            ApiResponse.fail(errorCode.status, errorResponse)
        return ResponseEntity.status(errorCode.status).body(response)
    }

    /** com.pida.support.error.CustomException 예외 처리  */
    @ExceptionHandler(ErrorException::class)
    fun handleCustomException(e: ErrorException): ResponseEntity<ApiResponse> {
        log.error("pida CustomException : {}", e.message, e)
        val errorCode: ErrorType = e.errorType
        val errorResponse =
            ErrorResponse.of(errorCode.name, errorCode.message)
        val response: ApiResponse =
            ApiResponse.fail(e.errorType.status, errorResponse)
        return ResponseEntity.status(errorCode.status).body(response)
    }

    /** 500번대 에러 처리  */
    @ExceptionHandler(Exception::class)
    protected fun handleException(e: Exception): ResponseEntity<ApiResponse> {
        log.error("Internal Server Error : {}", e.message, e)
        val internalServerError: ErrorType = ErrorType.INTERNAL_SERVER_ERROR
        val errorResponse =
            ErrorResponse.of(e.javaClass.simpleName, internalServerError.message)
        val response: ApiResponse =
            ApiResponse.fail(internalServerError.status, errorResponse)
        return ResponseEntity.status(internalServerError.status).body(response)
    }

    @ExceptionHandler(RuntimeException::class)
    protected fun handleRuntimeException(e: RuntimeException): ResponseEntity<ApiResponse> {
        log.error("Internal Server Runtime Error : {}", e.message, e)
        val internalServerError: ErrorType = ErrorType.INTERNAL_SERVER_ERROR
        val errorResponse =
            ErrorResponse.of(e.javaClass.simpleName, internalServerError.message)
        val response: ApiResponse =
            ApiResponse.fail(internalServerError.status, errorResponse)
        return ResponseEntity.status(internalServerError.status).body(response)
    }
}
