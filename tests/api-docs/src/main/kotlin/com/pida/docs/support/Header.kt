package com.pida.docs.support

import com.pida.docs.support.RestDocsAttributeKeys.Companion.KEY_DEFAULT_VALUE
import com.pida.docs.support.RestDocsAttributeKeys.Companion.KEY_FORMAT
import com.pida.docs.support.RestDocsAttributeKeys.Companion.KEY_PARAM_TYPE
import com.pida.docs.support.RestDocsAttributeKeys.Companion.KEY_SAMPLE
import org.springframework.restdocs.headers.HeaderDescriptor
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName

sealed class DocsHeaderType(
    val type: String,
)

data object Authorization : DocsHeaderType("Authorization")

data object Device : DocsHeaderType("X-DEVICE-ID")

open class Header(
    val descriptor: HeaderDescriptor,
) {
    val isOptional: Boolean = descriptor.isOptional
    protected open var default: String
        get() = descriptor.attributes.getOrDefault(KEY_DEFAULT_VALUE, "") as String
        set(value) {
            descriptor.attributes(RestDocsUtils.defaultValue(value))
        }

    protected open var format: String
        get() = descriptor.attributes.getOrDefault(KEY_FORMAT, "") as String
        set(value) {
            descriptor.attributes(RestDocsUtils.customFormat(value))
        }

    protected open var sample: String
        get() = descriptor.attributes.getOrDefault(KEY_SAMPLE, "") as String
        set(value) {
            descriptor.attributes(RestDocsUtils.customSample(value))
        }

    protected open var type: String
        get() = descriptor.attributes.getOrDefault(KEY_PARAM_TYPE, "") as String
        set(value) {
            descriptor.attributes(RestDocsUtils.customParamType(value))
        }

    open infix fun typedAs(value: String): Header {
        this.type = value
        return this
    }

    open infix fun attributes(block: Header.() -> Unit): Header {
        block()
        return this
    }

    open infix fun example(value: String): Header {
        this.sample = value
        return this
    }

    open infix fun isOptional(value: Boolean): Header {
        if (value) descriptor.optional()
        return this
    }
}

infix fun String.headerType(type: DocsHeaderType): Header {
    val header = createHeader(this, false)
    header typedAs type.type
    return header
}

private fun createHeader(
    value: String,
    optional: Boolean,
): Header {
    val descriptor =
        headerWithName(value)
            .attributes(
                RestDocsUtils.emptySample(),
                RestDocsUtils.emptyFormat(),
                RestDocsUtils.emptyDefaultValue(),
            ).description("")

    if (optional) descriptor.optional()

    return Header(descriptor)
}
