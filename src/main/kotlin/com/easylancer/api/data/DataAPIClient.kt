package com.easylancer.api.data

import com.easylancer.api.data.dto.*
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
import kotlinx.coroutines.delay
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity
import org.springframework.http.HttpEntity




class DataAPIClient(@Autowired private val restTemplate: RestTemplate) {

    private var mapper: ObjectMapper = jacksonObjectMapper();

    private fun get(url: String): JsonNode {
        return restTemplate.getForObject(url, JsonNode::class.java) ?:
            throw Exception("Data-Api call returned an empty response")
    }

    private fun post(url: String, data: ObjectNode = mapper.createObjectNode()): ResponseEntity<JsonNode> {
        return restTemplate.postForEntity(url, data)
    }

    private fun put(url: String, data: ObjectNode = mapper.createObjectNode()): Unit {
        return restTemplate.put(url, data)
    }

    fun getFullTask(id: String): FullTaskDTO {
        try {
            val dataNode = get("/tasks/${id}/view").get("data")

            return mapper.treeToValue(dataNode, FullTaskDTO::class.java)
        } catch (e: Exception) {
            throw DataApiException("Client API function failed: ${e.message}");
        }
    }

    fun getTask(id: String): TaskDTO {
        try {
            val data = get("/tasks/$id").get("data")

            return mapper.treeToValue(data, TaskDTO::class.java)
        } catch (e: Exception) {
            throw DataApiException("Client API function failed: ${e.message}");
        }
    }

    fun getUser(id: String): UserDTO {
        try {
            val data = get("/users/$id").get("data")

            return mapper.treeToValue(data, UserDTO::class.java)
        } catch (e: Exception) {
            throw DataApiException("Client API function failed: ${e.message}");
        }
    }

    fun getAllTasks(): Array<TaskDTO> {
        try {
            val respNode = get("/tasks")
            val dataArray = respNode.get("data")

            return mapper.treeToValue(dataArray, Array<TaskDTO>::class.java)
        } catch (e: Exception) {
            throw DataApiException("Client API function failed: ${e.message}");
        }
    }

    fun getUserFinishedTasks(id: String): Array<TaskDTO> {
        try {
            val respNode = get("/users/$id/tasks/finished")
            val dataArray = respNode.get("data")

            return mapper.treeToValue(dataArray, Array<TaskDTO>::class.java)
        } catch (e: Exception) {
            throw DataApiException("Client API function failed: ${e.message}");
        }
    }

    fun getUserCreatedTasks(id: String): Array<TaskDTO> {
        try {
            val respNode = get("/users/$id/tasks/created")
            val dataArray = respNode.get("data")

            return mapper.treeToValue(dataArray, Array<TaskDTO>::class.java)
        } catch (e: Exception) {
            throw DataApiException("Client API function failed: ${e.message}");
        }
    }

    fun getUserReviews(id: String): Array<FullTaskRatingDTO> {
        try {
            val respNode = get("/users/$id/reviews")
            val dataArray = respNode.get("data")

            return mapper.treeToValue(dataArray, Array<FullTaskRatingDTO>::class.java)
        } catch (e: Exception) {
            throw DataApiException("Client API function failed: ${e.message}");
        }
    }

    fun getTaskOffers(id: String): Array<FullOfferDTO> {
        try {
            val respNode = get("/tasks/$id/offers")
            val dataArray = respNode.get("data")

            return mapper.treeToValue(dataArray, Array<FullOfferDTO>::class.java)
        } catch (e: Exception) {
            throw DataApiException("Client API function failed: ${e.message}");
        }
    }

    fun postTask(task: ObjectNode): TaskDTO {
        try {
            val responseBody = post("/tasks", task).body

            if (responseBody != null) {
                return mapper.convertValue(responseBody.get("data"), TaskDTO::class.java)
            } else {
                throw DataApiException("Empty body response")
            }
        } catch (e: Exception) {
            throw DataApiException("Client API function failed: ${e.message}");
        }
    }

    fun putTask(taskId: String, task: ObjectNode) {
        try {
            put("/tasks/$taskId", task)
        } catch (e: Exception) {
            throw DataApiException("Client API function failed: ${e.message}");
        }
    }

    fun putUser(userId: String, user: ObjectNode) {
        try {
            put("/users/$userId", user)
        } catch (e: Exception) {
            throw DataApiException("Client API function failed: ${e.message}");
        }
    }

    fun postOffer(taskId: String, offer: ObjectNode): OfferDTO {
        try {
            val responseBody = post("/tasks/$taskId/offers", offer).body

            if (responseBody != null) {
                return mapper.convertValue(responseBody.get("data"), OfferDTO::class.java)
            } else {
                throw DataApiException("Empty body response")
            }
        } catch (e: Exception) {
            throw DataApiException("Client API function failed: ${e.message}");
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