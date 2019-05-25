package com.easylancer.api.data.blocking.exceptions

import com.easylancer.api.data.http.DataResponseError
import com.easylancer.api.data.http.DataRequest
import com.easylancer.api.data.dto.DataResponseErrorDTO
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class DataApiBadRequestException(
        message: String, dataRequest: DataRequest,
        val dataResponseError: DataResponseError,
        cause: Exception? = null
): DataApiException(message, dataRequest, dataResponseError, cause) {
    val invalidParams: JsonNode = if (dataResponseError.statusCode != 400 || dataResponseError.body !is DataResponseErrorDTO)
        jacksonObjectMapper().createObjectNode() else cleanInvalidParamsArray(dataResponseError.body.message)

    constructor(message: String, e: DataApiResponseException): this(message, e.dataRequest, e.dataResponseError, e)

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