package com.easylancer.api.data

import com.easylancer.api.data.dto.*
import com.easylancer.api.data.exceptions.*
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.web.client.*

class DataAPIClient(@Autowired private val restTemplate: RestTemplate) {

    private var mapper: ObjectMapper = jacksonObjectMapper();


    private inline fun <reified T> get(url: String): T {
        val request = DataRequest(url, HttpMethod.GET)
        try {
            val response = restTemplate.getForObject(url, DataResponseSuccessDTO::class.java) ?:
                throw DataApiUnknownResponseException("API returned an empty response body", request)
            try {
                return mapper.treeToValue(response.data, T::class.java)
            } catch (e: JsonProcessingException) {
                throw DataApiMappingException("Failed to map API response to DTO", MappingExceptionReason(response.data, T::class.java.name), request)
            }
        } catch (e: JsonProcessingException) {
            throw DataApiUnknownResponseException("API returned an unexpected response body", request)
        } catch (e: RestClientResponseException) {
            try {
                val responseDTO = mapper.readValue(e.responseBodyAsString, DataResponseErrorDTO::class.java)

                throw DataApiResponseException("API returned an error response", responseDTO, e.rawStatusCode, request)
            } catch (e: JsonProcessingException) {
                throw DataApiUnknownResponseException("API returned an unexpected response body", request)
            }
        } catch (e: RestClientException) {
            throw DataApiUnhandledException("Unhandled API exception occurred", request, e)
        }
    }

    private inline fun <reified T> post(url: String, data: JsonNode = mapper.createObjectNode()): T {
        val request = DataRequest(url, HttpMethod.POST, data)
        try {
            val json: JsonNode = restTemplate.postForObject(url, data)

            val obj = json.get("data")
            try {
                return mapper.treeToValue(obj, T::class.java)
            } catch (e: JsonProcessingException) {
                throw DataApiMappingException("Failed to map API response to DTO", MappingExceptionReason(obj, T::class.java.name), request)
            }
        } catch (e: JsonProcessingException) {
            throw DataApiUnknownResponseException("API returned an unexpected response format (expected JSON)", request)
        } catch (e: RestClientResponseException) {
            try {
                val responseDTO = mapper.readValue(e.responseBodyAsString, DataResponseErrorDTO::class.java)
                throw DataApiResponseException("API returned an error response", responseDTO, e.rawStatusCode, request)
            } catch (e: JsonProcessingException) {
                throw DataApiUnknownResponseException("Failed to parse API error response DTO", request)
            }
        } catch (e: RestClientException) {
            throw DataApiUnhandledException("Unhandled API exception occurred", request, e)
        }
    }

    private fun put(url: String, data: ObjectNode = mapper.createObjectNode()): Unit {
        val request = DataRequest(url, HttpMethod.PUT, data)
        try {
            return restTemplate.put(url, data)
        } catch (e: RestClientResponseException) {
            try {
                val responseDTO = mapper.readValue(e.responseBodyAsString, DataResponseErrorDTO::class.java)
                throw DataApiResponseException("API returned an error response", responseDTO, e.rawStatusCode, request)
            } catch (e: JsonProcessingException) {
                throw DataApiUnknownResponseException("Failed to parse API error response DTO", request)
            }
        } catch (e: RestClientException) {
            throw DataApiUnhandledException("Unhandled API exception occurred", request, e)
        }
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