package com.easylancer.api.data.exceptions

import com.easylancer.api.data.http.DataErrorResponse
import com.easylancer.api.data.http.DataRequest
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class DataApiBadRequestException(
        message: String,
        request: DataRequest,
        override val response: DataErrorResponse,
        cause: Exception? = null
): DataApiException(message, request, response, cause) {
    val invalidParams: JsonNode =
            if (response.statusCode != 400)
                jacksonObjectMapper().createObjectNode()
            else
                cleanInvalidParamsArray(response.body.message)

    constructor(message: String, e: DataApiResponseException): this(message, e.request, e.response, e)

    private fun cleanInvalidParamsArray(node: JsonNode): ArrayNode {
        val paramsArray = jacksonObjectMapper().createArrayNode();

        node.map { it as ObjectNode }.forEach {
            val param = it.deepCopy()

            param.remove("target")
            param.remove("children")

            paramsArray.add(param)
        }

        return paramsArray;
    }
}