package com.pida.docs.support

import com.pida.docs.support.RestDocsAttributeKeys.Companion.KEY_DEFAULT_VALUE
import com.pida.docs.support.RestDocsAttributeKeys.Companion.KEY_FORMAT
import com.pida.docs.support.RestDocsAttributeKeys.Companion.KEY_PARAM_TYPE
import com.pida.docs.support.RestDocsAttributeKeys.Companion.KEY_SAMPLE
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import kotlin.reflect.KClass

sealed class DocsParamType(
    val type: String,
)

data object FILE : DocsParamType("file")

data object TEXT : DocsParamType("string")

data object DOUBLE : DocsParamType("double")

data object LONG : DocsParamType("long")

data object INT : DocsParamType("integer")

data object BOOL : DocsParamType("boolean")

open class Parameter(
    val descriptor: ParameterDescriptor,
) {
    val isIgnored: Boolean = descriptor.isIgnored
    protected open var default: String
        get() = descriptor.attributes.getOrDefault(KEY_DEFAULT_VALUE, "") as String
        set(value) {
            descriptor.attributes(RestDocsUtils.defaultValue(value))
        }

    open var format: String
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

    open infix fun typedAs(value: String): Parameter {
        this.type = value
        return this
    }

    open infix fun means(value: String): Parameter {
        descriptor.description(value)
        return this
    }

    open infix fun attributes(block: Parameter.() -> Unit): Parameter {
        block()
        return this
    }

    open infix fun withDefaultValue(value: String): Parameter {
        this.default = value
        return this
    }

    open infix fun formattedAs(value: String): Parameter {
        this.format = value
        return this
    }

    open infix fun example(value: String): Parameter {
        this.sample = value
        return this
    }

    open infix fun isOptional(value: Boolean): Parameter {
        if (value) descriptor.optional()
        return this
    }

    open infix fun isIgnored(value: Boolean): Parameter {
        if (value) descriptor.ignored()
        return this
    }
}

data class ParamEnum<T : Enum<T>>(
    val enums: Collection<T>,
) : DocsParamType("string") {
    constructor(clazz: KClass<T>) : this(clazz.java.enumConstants.asList())
}

infix fun <T : Enum<T>> String.parameterType(enumFieldType: ParamEnum<T>): Parameter {
    val param = createParameter(this)
    param.format = EnumFormattingUtils.enumFormat(enumFieldType.enums)
    param typedAs enumFieldType.type
    return param
}

infix fun String.parameterType(type: DocsParamType): Parameter {
    val param = createParameter(this)
    param typedAs type.type
    return param
}

private fun createParameter(value: String): Parameter {
    val descriptor =
        parameterWithName(value)
            .attributes(
                RestDocsUtils.emptySample(),
                RestDocsUtils.emptyFormat(),
                RestDocsUtils.emptyDefaultValue(),
            ).description("")
    return Parameter(descriptor)
}
