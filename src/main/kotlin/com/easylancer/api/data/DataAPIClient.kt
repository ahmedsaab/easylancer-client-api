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

    private val mapper: ObjectMapper = jacksonObjectMapper()

    private fun wrapException(e: Exception, request: DataRequest): DataApiException {
        when(e) {
            is RestClientResponseException -> {
                throw DataApiResponseException(
                        message = "Received ${e.rawStatusCode} error response from Data API",
                        request = request,
                        response = getResponseFromResponseException(e),
                        error = e
                )
            }
            is ResourceAccessException -> {
                throw DataApiNetworkException(
                        message = "Could not access Data API",
                        request = request,
                        error = e
                )
            }
            is RestClientException -> {
                if(e.cause is JsonProcessingException) {
                    throw DataApiUnexpectedResponseException(
                            message = "Failed to transform success response body from Data API",
                            request = request,
                            error = e
                    )
                }
                throw DataApiUnhandledException(
                        message = "Unhandled exception occurred calling the Data API",
                        request = request,
                        error = e
                )
            }
            is JsonProcessingException -> {
                throw DataApiUnexpectedResponseException(
                        message = "Failed to transform success response payload returned from Data API",
                        request = request,
                        error = e
                )
            }
        }
        throw e
    }

    private fun getResponseFromResponseException(e: RestClientResponseException): DataErrorResponse {
        return try {
            val body = mapper.readValue(e.responseBodyAsString, DataResponseErrorDTO::class.java)

            DataErrorResponse(e.rawStatusCode, body)
        } catch (eJson: JsonProcessingException) {
            DataErrorResponse(e.rawStatusCode, e.responseBodyAsString)
        }
    }

    private inline fun <reified T> get(url: String): T {
        val request = DataRequest(url, HttpMethod.GET)
        val response: DataResponse?

        try {
            response = DataSuccessResponse(
                    restTemplate.getForObject(request.url, DataResponseSuccessDTO::class.java)!!
            )

            return mapper.treeToValue(response.bodyDto.data, T::class.java)
        } catch (e: Exception) {
            throw wrapException(e, request)
        }
    }

    private inline fun <reified T> post(url: String, data: JsonNode = mapper.createObjectNode()): T {
        val request = DataRequest(url, HttpMethod.POST, data)
        val response: DataResponse?

        try {
            response = DataSuccessResponse(
                    restTemplate.postForObject(url, data, DataResponseSuccessDTO::class.java)!!
            )

            return mapper.treeToValue(response.bodyDto.data, T::class.java)
        } catch (e: Exception) {
            throw wrapException(e, request)
        }
    }

    private fun put(url: String, data: ObjectNode = mapper.createObjectNode()) {
        val request = DataRequest(url, HttpMethod.PUT, data)

        try {
            return restTemplate.put(url, data)
        } catch (e: Exception) {
            throw wrapException(e, request)
        }
    }

    fun getFullTask(id: String): FullTaskDTO {
        return get("/tasks/$id/view")
    }

    fun getTask(id: String): TaskDTO {
        return get("/tasks/$id")
    }

    fun getUser(id: String): UserDTO {
        return get("/users/$id")
    }

    fun getAllTasks(): Array<TaskDTO> {
        return get("/tasks")
    }

    fun getUserFinishedTasks(id: String): Array<TaskDTO> {
        return get("/users/$id/tasks/finished")
    }

    fun getUserCreatedTasks(id: String): Array<TaskDTO> {
        return get("/users/$id/tasks/created")
    }

    fun getUserRelatedTasks(id: String): Array<TaskDTO> {
        return get("/users/$id/tasks")
    }

    fun getUserReviews(id: String): Array<FullTaskRatingDTO> {
        return get("/users/$id/reviews")
    }

    fun getTaskOffers(id: String): Array<FullOfferDTO> {
        return get("/offers/view?task=$id")
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