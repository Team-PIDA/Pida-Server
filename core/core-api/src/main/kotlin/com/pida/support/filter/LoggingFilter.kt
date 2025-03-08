package com.pida.support.filter

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.WebUtils
import java.io.IOException
import java.io.UnsupportedEncodingException

@Component
class LoggingFilter(
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val isFirstRequest = !this.isAsyncDispatch(request)
        var wrapper = request
        if (isFirstRequest && request !is ContentCachingRequestWrapper) {
            wrapper = ContentCachingRequestWrapper(request)
        }
        try {
            filterChain.doFilter(wrapper, response)
        } finally {
            if (!this.isAsyncStarted(wrapper)) {
                createMessage(wrapper)
            }
        }
    }

    @Throws(JsonProcessingException::class)
    private fun createMessage(request: HttpServletRequest) {
        val logData = objectMapper.createObjectNode()
        setClient(request, logData)
        setMethod(request, logData)
        setUri(request, logData)
        setParameters(request, logData)
        setPayload(request, logData)
        val json = objectMapper.writeValueAsString(logData)
        log.info("REQUEST : $json")
    }

    private fun setParameters(
        request: HttpServletRequest,
        logData: ObjectNode,
    ) {
        request.queryString.takeIf { !it.isNullOrBlank() } ?: return

        val parametersNode = objectMapper.createObjectNode()
        val parameterMap = request.parameterMap

        parameterMap.forEach { (key: String?, values: Array<String?>) ->
            if (values.size == 1) {
                parametersNode.put(key, values[0])
            } else {
                val arrayNode = objectMapper.createArrayNode()
                for (value in values) {
                    arrayNode.add(value)
                }
                parametersNode.set<JsonNode>(key, arrayNode)
            }
        }

        val queryString = request.queryString
        parametersNode.put("_queryString", queryString)

        logData.set<JsonNode>("parameters", parametersNode)
    }

    private fun setPayload(
        request: HttpServletRequest,
        node: ObjectNode,
    ) {
        val wrapper =
            WebUtils.getNativeRequest(
                request,
                ContentCachingRequestWrapper::class.java,
            )
        if (wrapper != null) {
            val buf = wrapper.contentAsByteArray
            if (buf.isNotEmpty()) {
                try {
                    node.set<JsonNode>("payload", objectMapper.readTree(buf))
                } catch (e: IOException) {
                    try {
                        val content = String(buf, charset(wrapper.characterEncoding))
                        node.put("payload", content)
                    } catch (ex: UnsupportedEncodingException) {
                        node.put("payload", "Failed to parse payload")
                    }
                }
            }
        }
    }

    private fun setUri(
        request: HttpServletRequest,
        logData: ObjectNode,
    ) {
        logData.put("uri", request.requestURI)
    }

    private fun setMethod(
        request: HttpServletRequest,
        logData: ObjectNode,
    ) {
        logData.put("method", request.method)
    }

    private fun setClient(
        request: HttpServletRequest,
        logData: ObjectNode,
    ) {
        request.remoteAddr?.takeIf { it.isNotBlank() }?.let { logData.put("client", it) }
        setSession(request, logData)
        setMember(request, logData)
    }

    private fun setSession(
        request: HttpServletRequest,
        logData: ObjectNode,
    ) {
        val session = request.getSession(false)
        if (session != null) {
            logData.put("session", session.id)
        }
    }

    private fun setMember(
        request: HttpServletRequest,
        logData: ObjectNode,
    ) {
        val remoteMember = request.remoteUser
        if (remoteMember != null) {
            logData.put("member", remoteMember)
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(LoggingFilter::class.java)
    }
}
