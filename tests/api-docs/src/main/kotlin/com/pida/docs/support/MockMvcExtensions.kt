package com.pida.docs.support

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.pida.docs.support.RestDocsUtils.pathVariablesToSwagger
import com.pida.docs.support.RestDocsUtils.requestBody
import com.pida.docs.support.RestDocsUtils.requestHeadersToSwagger
import com.pida.docs.support.RestDocsUtils.requestParametersToSwagger
import com.pida.docs.support.RestDocsUtils.responseBody
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse
import org.springframework.restdocs.headers.HeaderDescriptor
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.snippet.Snippet

object MockMvcExtensions {
    fun ValidatableMockMvcResponse.makeDocument(
        identifier: String,
        docsTag: DocsTag,
        pathVariable: List<ParameterDescriptor> = emptyList(),
        requestHeader: List<HeaderDescriptor> = emptyList(),
        requestParameters: List<ParameterDescriptor> = emptyList(),
        requestSchema: String = "",
        responseSchema: String = "",
        requestBody: List<FieldDescriptor> = emptyList(),
        responseBody: List<FieldDescriptor> = emptyList(),
    ): ValidatableMockMvcResponse {
        val builder =
            ResourceSnippetParameters
                .builder()
                .tags(docsTag.tagName)
                .summary(identifier)
                .requestSchema(Schema.schema(requestSchema))
                .responseSchema(Schema.schema(responseSchema))

        val snippets = mutableListOf<Snippet>()

        if (pathVariable.isNotEmpty()) {
            builder.pathParameters(pathVariablesToSwagger(pathVariable))
            snippets.add(RequestDocumentation.pathParameters(pathVariable))
        }
        if (requestHeader.isNotEmpty()) {
            builder.requestHeaders(requestHeadersToSwagger(requestHeader))
            snippets.add(HeaderDocumentation.requestHeaders(requestHeader))
        }
        if (requestParameters.isNotEmpty()) {
            builder.queryParameters(requestParametersToSwagger(requestParameters))
            snippets.add(RequestDocumentation.queryParameters(requestParameters))
        }
        if (requestBody.isNotEmpty()) {
            builder.requestFields(requestBody)
            snippets.add(PayloadDocumentation.requestFields(requestBody))
        }

        if (responseBody.isNotEmpty()) {
            builder.responseFields(responseBody)
            snippets.add(PayloadDocumentation.responseFields(responseBody))
        }
        return this.apply(
            MockMvcRestDocumentationWrapper.document(
                identifier = identifier,
                requestPreprocessor = RestDocsUtils.requestPreprocessor(),
                responsePreprocessor = RestDocsUtils.responsePreprocessor(),
                resourceDetails = builder,
                snippets = snippets.toTypedArray(),
            ),
        )
    }

    /**
     * RestDocs makeDocument (Not Header, Not PathVariable)
     *
     * @param identifier [String] Document 식별값
     * @param docsTag [DocsTag] DocsTag Object
     * @param requestSchema [String] Request Schema name
     * @param responseSchema [String] Response Schema name
     * @param requestField [requestBody] requestBody function
     * @param responseField [responseBody] responseBody function
     * @param snippets [Snippet] 추가 적으로 필요한 Snippets
     */
    fun ValidatableMockMvcResponse.makeDocument(
        identifier: String,
        docsTag: DocsTag,
        requestSchema: String,
        responseSchema: String,
        requestField: List<FieldDescriptor>,
        responseField: List<FieldDescriptor>,
        vararg snippets: Snippet,
    ): ValidatableMockMvcResponse =
        this.apply(
            MockMvcRestDocumentationWrapper.document(
                identifier,
                RestDocsUtils.requestPreprocessor(),
                RestDocsUtils.responsePreprocessor(),
                PayloadDocumentation.requestFields(requestField),
                PayloadDocumentation.responseFields(responseField),
                *snippets,
                ResourceDocumentation.resource(
                    ResourceSnippetParameters
                        .builder()
                        .tags(docsTag.tagName)
                        .summary(identifier)
                        .requestSchema(Schema.schema(requestSchema))
                        .requestFields(requestField)
                        .responseSchema(Schema.schema(responseSchema))
                        .responseFields(responseField)
                        .build(),
                ),
            ),
        )

    /**
     * RestDocs makeDocument (header)
     *
     * @param identifier [String] Document 식별값
     * @param docsTag [DocsTag] DocsTag Object
     * @param requestHeader [RestDocsUtils.headers] headers function
     * @param requestSchema [String] Request Schema name
     * @param responseSchema [String] Response Schema name
     * @param requestField [requestBody] requestBody function
     * @param responseField [responseBody] responseBody function
     * @param snippets [Snippet] 추가 적으로 필요한 Snippets
     */
    @JvmName("makeDocumentHeader")
    fun ValidatableMockMvcResponse.makeDocument(
        identifier: String,
        docsTag: DocsTag,
        requestHeader: List<HeaderDescriptor>,
        requestSchema: String,
        responseSchema: String,
        requestField: List<FieldDescriptor>,
        responseField: List<FieldDescriptor>,
        vararg snippets: Snippet,
    ): ValidatableMockMvcResponse =
        this.apply(
            MockMvcRestDocumentationWrapper.document(
                identifier,
                RestDocsUtils.requestPreprocessor(),
                RestDocsUtils.responsePreprocessor(),
                HeaderDocumentation.requestHeaders(requestHeader),
                PayloadDocumentation.requestFields(requestField),
                PayloadDocumentation.responseFields(responseField),
                *snippets,
                ResourceDocumentation.resource(
                    ResourceSnippetParameters
                        .builder()
                        .tags(docsTag.tagName)
                        .summary(identifier)
                        .requestHeaders(requestHeadersToSwagger(requestHeader))
                        .requestSchema(Schema.schema(requestSchema))
                        .requestFields(requestField)
                        .responseSchema(Schema.schema(responseSchema))
                        .responseFields(responseField)
                        .build(),
                ),
            ),
        )

    /**
     * RestDocs makeDocument (pathVariable)
     *
     * @param identifier [String] Document 식별값
     * @param docsTag [DocsTag] DocsTag Object
     * @param pathVariable [RestDocsUtils.pathVariables] pathVariables function
     * @param requestSchema [String] Request Schema name
     * @param responseSchema [String] Response Schema name
     * @param requestField [requestBody] requestBody function
     * @param responseField [responseBody] responseBody function
     * @param snippets [Snippet] 추가 적으로 필요한 Snippets
     */
    @JvmName("makeDocumentPathVariable")
    fun ValidatableMockMvcResponse.makeDocument(
        identifier: String,
        docsTag: DocsTag,
        pathVariable: List<ParameterDescriptor>,
        requestSchema: String,
        responseSchema: String,
        requestField: List<FieldDescriptor>,
        responseField: List<FieldDescriptor>,
        vararg snippets: Snippet,
    ): ValidatableMockMvcResponse =
        this.apply(
            MockMvcRestDocumentationWrapper.document(
                identifier,
                RestDocsUtils.requestPreprocessor(),
                RestDocsUtils.responsePreprocessor(),
                RequestDocumentation.pathParameters(pathVariable),
                PayloadDocumentation.responseFields(responseField),
                *snippets,
                ResourceDocumentation.resource(
                    ResourceSnippetParameters
                        .builder()
                        .tags(docsTag.tagName)
                        .summary(identifier)
                        .pathParameters(RestDocsUtils.pathVariablesToSwagger(pathVariable))
                        .requestSchema(Schema.schema(requestSchema))
                        .requestFields(requestField)
                        .responseSchema(Schema.schema(responseSchema))
                        .responseFields(responseField)
                        .build(),
                ),
            ),
        )

    /**
     * RestDocs makeDocument (header to pathVariable)
     *
     * @param identifier [String] Document 식별값
     * @param docsTag [DocsTag] DocsTag Object
     * @param requestHeader [RestDocsUtils.headers] headers function
     * @param pathVariable [RestDocsUtils.pathVariables] pathVariables function
     * @param requestSchema [String] Request Schema name
     * @param responseSchema [String] Response Schema name
     * @param requestField [requestBody] requestBody function
     * @param responseField [responseBody] responseBody function
     * @param snippets [Snippet] 추가 적으로 필요한 Snippets
     */
    fun ValidatableMockMvcResponse.makeDocument(
        identifier: String,
        docsTag: DocsTag,
        requestHeader: List<HeaderDescriptor>,
        pathVariable: List<ParameterDescriptor>,
        requestSchema: String,
        responseSchema: String,
        requestField: List<FieldDescriptor>,
        responseField: List<FieldDescriptor>,
        vararg snippets: Snippet,
    ): ValidatableMockMvcResponse =
        this.apply(
            MockMvcRestDocumentationWrapper.document(
                identifier,
                RestDocsUtils.requestPreprocessor(),
                RestDocsUtils.responsePreprocessor(),
                HeaderDocumentation.requestHeaders(requestHeader),
                RequestDocumentation.pathParameters(pathVariable),
                PayloadDocumentation.responseFields(responseField),
                *snippets,
                ResourceDocumentation.resource(
                    ResourceSnippetParameters
                        .builder()
                        .tags(docsTag.tagName)
                        .summary(identifier)
                        .requestHeaders(requestHeadersToSwagger(requestHeader))
                        .pathParameters(RestDocsUtils.pathVariablesToSwagger(pathVariable))
                        .requestSchema(Schema.schema(requestSchema))
                        .requestFields(requestField)
                        .responseSchema(Schema.schema(responseSchema))
                        .responseFields(responseField)
                        .build(),
                ),
            ),
        )

    /**
     * RestDocs makeDocument (header to pathVariable)
     *
     * @param identifier [String] Document 식별값
     * @param docsTag [DocsTag] DocsTag Object
     * @param responseSchema [String] Response Schema name
     * @param requestParameters [RestDocsUtils.requestParameters] requestBody function
     * @param responseField [responseBody] responseBody function
     * @param snippets [Snippet] 추가 적으로 필요한 Snippets
     */
    fun ValidatableMockMvcResponse.makeDocument(
        identifier: String,
        docsTag: DocsTag,
        responseSchema: String,
        requestParameters: List<ParameterDescriptor>,
        responseField: List<FieldDescriptor>,
        vararg snippets: Snippet,
    ): ValidatableMockMvcResponse =
        this.apply(
            MockMvcRestDocumentationWrapper.document(
                identifier,
                RestDocsUtils.requestPreprocessor(),
                RestDocsUtils.responsePreprocessor(),
                RequestDocumentation.queryParameters(requestParameters),
                PayloadDocumentation.responseFields(responseField),
                *snippets,
                ResourceDocumentation.resource(
                    ResourceSnippetParameters
                        .builder()
                        .tags(docsTag.tagName)
                        .summary(identifier)
                        .queryParameters(requestParametersToSwagger(requestParameters))
                        .responseSchema(Schema.schema(responseSchema))
                        .responseFields(responseField)
                        .build(),
                ),
            ),
        )

    /**
     * RestDocs makeDocument (header, Not pathVariable, Not RequestBody)
     *
     * @param identifier [String] Document 식별값
     * @param docsTag [DocsTag] DocsTag Object
     * @param requestHeader [RestDocsUtils.headers] headers function
     * @param responseSchema [String] Response Schema name
     * @param responseField [responseBody] responseBody function
     * @param snippets [Snippet] 추가 적으로 필요한 Snippets
     */
    fun ValidatableMockMvcResponse.makeDocument(
        identifier: String,
        docsTag: DocsTag,
        requestHeader: List<HeaderDescriptor>,
        responseSchema: String,
        responseField: List<FieldDescriptor>,
        vararg snippets: Snippet,
    ): ValidatableMockMvcResponse =
        this.apply(
            MockMvcRestDocumentationWrapper.document(
                identifier,
                RestDocsUtils.requestPreprocessor(),
                RestDocsUtils.responsePreprocessor(),
                HeaderDocumentation.requestHeaders(requestHeader),
                PayloadDocumentation.responseFields(responseField),
                *snippets,
                ResourceDocumentation.resource(
                    ResourceSnippetParameters
                        .builder()
                        .tags(docsTag.tagName)
                        .summary(identifier)
                        .requestHeaders(requestHeadersToSwagger(requestHeader))
                        .responseSchema(Schema.schema(responseSchema))
                        .responseFields(responseField)
                        .build(),
                ),
            ),
        )
}
