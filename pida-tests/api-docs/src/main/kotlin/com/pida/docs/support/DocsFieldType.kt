package com.pida.docs.support

import org.springframework.restdocs.payload.JsonFieldType

sealed class DocsFieldType(
    val type: JsonFieldType,
)

/**
 * ARRAY : 정렬 타입
 */
data object ARRAY : DocsFieldType(JsonFieldType.ARRAY)

/**
 * BOOLEAN : 부울 타입
 */
data object BOOLEAN : DocsFieldType(JsonFieldType.BOOLEAN)

/**
 * OBJECT : 오브젝트 타입
 */
data object OBJECT : DocsFieldType(JsonFieldType.OBJECT)

/**
 * NUMBER : 숫자 타입
 */
data object NUMBER : DocsFieldType(JsonFieldType.NUMBER)

/**
 * NULL : NULL 타입
 */
data object NULL : DocsFieldType(JsonFieldType.NULL)

/**
 * STRING : 문자열 타입
 */
data object STRING : DocsFieldType(JsonFieldType.STRING)

/**
 * ANY : 다중 타입
 */
data object ANY : DocsFieldType(JsonFieldType.VARIES)

/**
 * DATE : 날짜 타입
 */
data object DATE : DocsFieldType(JsonFieldType.STRING)

/**
 * DATETIME : 날짜 시간 타입
 */
data object DATETIME : DocsFieldType(JsonFieldType.STRING)
