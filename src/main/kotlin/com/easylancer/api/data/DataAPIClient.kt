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
        var response: DataResponse?

        try {
            response = DataSuccessResponse(
                restTemplate.getForObject(request.url, DataResponseSuccessDTO::class.java) ?:
                throw DataApiUnexpectedResponseException(
                        message = "Received an empty response body",
                        request = request
                )
            )

            return mapper.treeToValue(response.bodyDto.data, T::class.java)
        } catch (e: RestClientResponseException) {
            response = getResponseFromResponseException(e)

            throw DataApiResponseException(
                    message = "Received an error response",
                    request = request,
                    response = response,
                    error = e
            )
        } catch (e: ResourceAccessException) {
            throw DataApiNetworkException(
                    message = "Could not access Data API",
                    request = request,
                    error = e
            )
        } catch (e: RestClientException) {
            if(e.cause is JsonProcessingException) {
                throw DataApiUnexpectedResponseException(
                        message = "Failed to transform success response body",
                        request = request,
                        error = e
                )
            }
            throw DataApiUnhandledException(
                    message = "Unhandled exception occurred",
                    request = request,
                    error = e
            )
        } catch (e: JsonProcessingException) {
            throw DataApiUnexpectedResponseException(
                    message = "Failed to transform response payload to ${T::class.java.name}",
                    request = request,
                    error = e
            )
        }
    }

    private inline fun <reified T> post(url: String, data: JsonNode = mapper.createObjectNode()): T {
        val request = DataRequest(url, HttpMethod.POST, data)
        var response: DataResponse?

        try {
            val body = restTemplate.postForObject(url, data, DataResponseSuccessDTO::class.java)
                    ?: throw DataApiUnexpectedResponseException(
                            message = "Received an empty response body",
                            request = request
                    )
            response = DataSuccessResponse(body)

            return mapper.treeToValue(response.bodyDto.data, T::class.java)
        } catch (e: RestClientResponseException) {
            response = getResponseFromResponseException(e)

            throw DataApiResponseException(
                    message = "Received an error response",
                    request = request,
                    response = response,
                    error = e
            )
        } catch (e: ResourceAccessException) {
            throw DataApiNetworkException(
                    message = "Could not access Data API",
                    request = request,
                    error = e
            )
        } catch (e: RestClientException) {
            if(e.cause is JsonProcessingException) {
                throw DataApiUnexpectedResponseException(
                        message = "Failed to transform success response body",
                        request = request,
                        error = e
                )
            }
            throw DataApiUnhandledException(
                    message = "Unhandled exception occurred",
                    request = request,
                    error = e
            )
        } catch (e: JsonProcessingException) {
            throw DataApiUnexpectedResponseException(
                    message = "Failed to transform response payload to ${T::class.java.name}",
                    request = request,
                    error = e
            )
        }
    }

    private fun put(url: String, data: ObjectNode = mapper.createObjectNode()) {
        val request = DataRequest(url, HttpMethod.PUT, data)
        val response: DataResponse?

        try {
            return restTemplate.put(url, data)
        } catch (e: RestClientResponseException) {
            response = getResponseFromResponseException(e)

            throw DataApiResponseException(
                    message = "Received an error response",
                    request = request,
                    response = response,
                    error = e
            )
        } catch (e: ResourceAccessException) {
            throw DataApiNetworkException(
                    message = "Could not access Data API",
                    request = request,
                    error = e
            )
        } catch (e: RestClientException) {
            throw DataApiUnhandledException(
                    message = "Unhandled exception occurred",
                    request = request,
                    error = e
            )
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