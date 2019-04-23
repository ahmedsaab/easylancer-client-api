package com.easylancer.api.data

import com.easylancer.api.data.dto.FullOfferDTO
import com.easylancer.api.data.dto.FullTaskDTO
import com.easylancer.api.data.dto.OfferDTO
import com.easylancer.api.data.dto.TaskDTO
import com.easylancer.api.dto.CreateOfferDTO
import com.easylancer.api.dto.CreateTaskDTO
import com.easylancer.api.dto.UpdateTaskDTO
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
import org.springframework.http.HttpEntity




class DataAPIClient(@Autowired private val restTemplate: RestTemplate) {

    private var mapper: ObjectMapper = jacksonObjectMapper();

    private fun createEntity(bearerToken: String, data: ObjectNode = mapper.createObjectNode()): HttpEntity<JsonNode> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("Authorization", "Bearer $bearerToken")

        return HttpEntity<JsonNode>(data, headers)
    }

    private fun get(url: String): JsonNode {
        return restTemplate.getForObject(url, JsonNode::class.java) ?:
            throw Exception("Data-Api call returned an empty response")
    }

    private fun post(url: String, data: ObjectNode = mapper.createObjectNode()): ResponseEntity<JsonNode> {
        return restTemplate.postForEntity(url, data)
    }

    private fun post(url: String, data: HttpEntity<JsonNode>): ResponseEntity<JsonNode> {
        return restTemplate.postForEntity(url, data)
    }

    private fun put(url: String, data: ObjectNode = mapper.createObjectNode()): Unit {
        return restTemplate.put(url, data)
    }

    private fun put(url: String, data: HttpEntity<JsonNode>): Unit {
        return restTemplate.put(url, data)
    }

    suspend fun getTaskAsync(id: String): Deferred<FullTaskDTO> = coroutineScope {
        async {
            try {
                val respNode = get("/tasks/${id}/view")
                val dataNode = respNode.get("data")

                mapper.treeToValue(dataNode, FullTaskDTO::class.java)
            } catch (e: Exception) {
                throw DataApiException("Client API function failed: ${e.message}");
            }
        }
    }

    suspend fun getAllTasksAsync(): Deferred<Array<TaskDTO>> = coroutineScope {
        async {
            try {
                val respNode = get("/tasks")
                val dataArray = respNode.get("data")

                mapper.treeToValue(dataArray, Array<TaskDTO>::class.java)
            } catch (e: Exception) {
                throw DataApiException("Client API function failed: ${e.message}");
            }
        }
    }

    suspend fun getTaskOffersAsync(id: String): Deferred<Array<FullOfferDTO>> = coroutineScope {
        async {
            try {
                val respNode = get("/tasks/$id/offers")
                val dataArray = respNode.get("data")

                mapper.treeToValue(dataArray, Array<FullOfferDTO>::class.java)
            } catch (e: Exception) {
                throw DataApiException("Client API function failed: ${e.message}");
            }
        }
    }

    suspend fun postTaskAsync(userId: String, task: CreateTaskDTO): Deferred<TaskDTO> = coroutineScope {
        async {
            try {

                val taskBody = mapper.valueToTree<ObjectNode>(task);

                taskBody.put("creatorUser", userId);
                val responseBody = post("/tasks", taskBody).body

                if (responseBody != null) {
                    mapper.convertValue(responseBody.get("data"), TaskDTO::class.java)
                } else {
                    throw DataApiException("Empty body response")
                }
            } catch (e: Exception) {
                throw DataApiException("Client API function failed: ${e.message}");
            }
        }
    }

    suspend fun putTaskAsync(taskId: String, userId: String, task: UpdateTaskDTO): Deferred<Unit> = coroutineScope {
        async {
            try {
                val entity = createEntity(userId, mapper.valueToTree<ObjectNode>(task))

                put("/tasks/$taskId", entity)
            } catch (e: Exception) {
                throw DataApiException("Client API function failed: ${e.message}");
            }
        }
    }

    suspend fun postOfferAsync(userId: String, taskId: String, offer: CreateOfferDTO): Deferred<OfferDTO> = coroutineScope {
        async {
            try {

                val offerBody = mapper.valueToTree<ObjectNode>(offer);

                offerBody.put("workerUser", userId)

                val responseBody = post("/tasks/$taskId/offers", offerBody).body

                if (responseBody != null) {
                    mapper.convertValue(responseBody.get("data"), OfferDTO::class.java)
                } else {
                    throw DataApiException("Empty body response")
                }
            } catch (e: Exception) {
                throw DataApiException("Client API function failed: ${e.message}");
            }
        }
    }

    fun taskSeenBy(id: String, userId: String): List<String> {
        try {
            val responseBody = post("/tasks/$id/seenBy/$userId").body

            if (responseBody != null) {
                return responseBody.get("data").map{ it.textValue()}
            } else {
                throw Exception("Empty body response")
            }
        } catch (e: Exception) {
            throw DataApiException("Client API function failed: ${e.message}");
        }
    }
}

class DataApiException(message: String): Exception(message) {

}