package com.pida.support.error

class CustomException(
	val errorCode: ErrorCode,
) : RuntimeException(errorCode.message)
