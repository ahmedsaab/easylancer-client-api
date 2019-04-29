package com.easylancer.api.data

import com.easylancer.api.data.dto.*
import com.easylancer.api.data.exceptions.DataApiMappingException
import com.easylancer.api.data.exceptions.DataApiResponseException
import com.easylancer.api.data.exceptions.DataApiUnknownResponseException
import com.easylancer.api.data.exceptions.MappingParams
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.web.client.*
import java.lang.RuntimeException

class DataAPIClient(@Autowired private val restTemplate: RestTemplate) {

    private var mapper: ObjectMapper = jacksonObjectMapper();

    private inline fun <reified T> get(url: String): T {
        val request = DataApiRequest(url, HttpMethod.GET)
        try {
            val json = restTemplate.getForObject(url, JsonNode::class.java)

            if(json == null) {
                throw DataApiUnknownResponseException("API returned an empty response body", request)
            } else {
                val data = json.get("data")
                try {
                    return mapper.treeToValue(data, T::class.java)
                } catch (e: JsonProcessingException) {
                    throw DataApiMappingException(MappingParams(data, T::class.java.name), request)
                }
            }
        } catch (e: JsonProcessingException) {
            throw DataApiUnknownResponseException("API returned an unexpected response format (expected JSON)", request)
        } catch (e: RestClientResponseException) {
            try {
                val errorDto = mapper.convertValue(e.responseBodyAsString, ErrorDTO::class.java)
                throw DataApiResponseException(errorDto, e.rawStatusCode, request)
            } catch (e: JsonProcessingException) {
                throw DataApiUnknownResponseException("Failed to parse API error response DTO", request)
            }
        }
    }

    private inline fun <reified T> post(url: String, data: JsonNode = mapper.createObjectNode()): T {
        val request = DataApiRequest(url, HttpMethod.POST, data)
        try {
            val json: JsonNode = restTemplate.postForObject(url, data)

            val obj = json.get("data")
            try {
                return mapper.treeToValue(obj, T::class.java)
            } catch (e: JsonProcessingException) {
                throw DataApiMappingException(MappingParams(obj, T::class.java.name), request)
            }
        } catch (e: JsonProcessingException) {
            throw DataApiUnknownResponseException("API returned an unexpected response format (expected JSON)", request)
        } catch (e: RestClientResponseException) {
            try {
                val errorDto = mapper.convertValue(e.responseBodyAsString, ErrorDTO::class.java)
                throw DataApiResponseException(errorDto, e.rawStatusCode, request)
            } catch (e: JsonProcessingException) {
                throw DataApiUnknownResponseException("Failed to parse API error response DTO", request)
            }
        }
    }

    private fun put(url: String, data: ObjectNode = mapper.createObjectNode()): Unit {
        return restTemplate.put(url, data)
    }

    fun getFullTask(id: String): FullTaskDTO {
        return get("/tasks/$id/view");
    }

    fun getTask(id: String): TaskDTO {
        return get("/tasks/$id");
    }

    fun getUser(id: String): UserDTO {
        return get("/users/$id");
    }

    fun getAllTasks(): Array<TaskDTO> {
        return get("/tasks");
    }

    fun getUserFinishedTasks(id: String): Array<TaskDTO> {
        return get("/users/$id/tasks/finished");
    }

    fun getUserCreatedTasks(id: String): Array<TaskDTO> {
        return get("/users/$id/tasks/created");
    }

    fun getUserRelatedTasks(id: String): Array<TaskDTO> {
        return get("/users/$id/tasks");
    }

    fun getUserReviews(id: String): Array<FullTaskRatingDTO> {
        return get("/users/$id/reviews");
    }

    fun getTaskOffers(id: String): Array<FullOfferDTO> {
        return get("/offers/view?task=$id");
    }

    fun postTask(task: ObjectNode): TaskDTO {
        return post("/tasks", task)
    }

    fun putTask(taskId: String, task: ObjectNode) {
        put("/tasks/$taskId", task)
    }

    fun putUser(userId: String, user: ObjectNode) {
        put("/users/$userId", user)
    }

    fun postOffer(offer: ObjectNode): OfferDTO {
        return post("/offers", offer)
    }

    fun taskSeenBy(id: String, userId: String): Array<String> {
        return post("/tasks/$id/seenBy/$userId")
    }
}