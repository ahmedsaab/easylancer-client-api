package com.easylancer.api

import com.easylancer.api.DataApiResponseDTO
import com.easylancer.api.TaskDTO
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity


class DataAPIClient(@Autowired private val restTemplate: RestTemplate) {

    private var mapper: ObjectMapper = jacksonObjectMapper();

    private fun get(url: String): JsonNode {
        return restTemplate.getForObject(url, JsonNode::class.java) ?:
            throw Exception("Data-Api call returned an empty response")
    }

    private fun post(url: String, data: ObjectNode = mapper.createObjectNode()): ResponseEntity<JsonNode> {
        return restTemplate.postForEntity(url, data)
    }

    suspend fun getTask(id: String): TaskDTO  {
        val respNode = get("/tasks/${id}")

        return mapper.treeToValue(respNode.get("data"), TaskDTO::class.java)
    }

    suspend fun taskSeenBy(id: String, userId: String): List<String> {
        try {
            val responseEntity = post("/tasks/$id/seenBy/$userId")
            val responseBody = responseEntity.body;

            if (responseBody != null) {
                return responseBody.get("data").map{ it.textValue()}
            } else {
                throw DataApiException("Empty body response")
            }
        } catch (e: Exception) {
            throw DataApiException("Unexpected API response");
        }
    }
}

class DataApiException(message: String): Exception(message) {

}