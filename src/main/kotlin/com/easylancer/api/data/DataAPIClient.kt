package com.easylancer.api.data

import com.easylancer.api.data.dto.FullTaskDTO
import com.easylancer.api.data.dto.TaskDTO
import com.easylancer.api.dto.CreateTaskDTO
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

    suspend fun getTaskAsync(id: String): Deferred<FullTaskDTO> = coroutineScope {
        try {
            async {
                val respNode =  get("/tasks/${id}/view")

                mapper.treeToValue(respNode.get("data"), FullTaskDTO::class.java)
            }
        } catch (e: Exception) {
            throw DataApiException("Unexpected API response: ${e.message}");
        }
    }

    suspend fun postTaskAsync(userId: String, task: CreateTaskDTO): Deferred<TaskDTO> = coroutineScope {
        try {
            async {
                val taskBody = mapper.valueToTree<ObjectNode>(task);

                taskBody.put("creatorUser", userId);
                val responseBody = post("/tasks", taskBody).body

                if (responseBody != null) {
                    mapper.convertValue(responseBody.get("data"), TaskDTO::class.java)
                } else {
                    throw DataApiException("Empty body response")
                }
            }
        } catch (e: Exception) {
            throw DataApiException("Unexpected API response: ${e.message}");
        }
    }

    fun taskSeenBy(id: String, userId: String): List<String> {
        try {
            val responseBody = post("/tasks/$id/seenBy/$userId").body

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