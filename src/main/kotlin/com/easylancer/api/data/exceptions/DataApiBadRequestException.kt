package com.easylancer.api.data.exceptions

import com.easylancer.api.data.http.ResponseError
import com.easylancer.api.data.http.Request
import com.easylancer.api.data.dto.DataResponseErrorDTO
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class DataApiBadRequestException(
        message: String, request: Request,
        val responseError: ResponseError,
        cause: Exception? = null
): DataApiException(message, request, responseError, cause) {
    val invalidParams: JsonNode = if (responseError.statusCode != 400 || responseError.body !is DataResponseErrorDTO)
        jacksonObjectMapper().createObjectNode() else cleanInvalidParamsArray(responseError.body.message)

    constructor(message: String, e: DataApiResponseException): this(message, e.request, e.responseError, e)

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