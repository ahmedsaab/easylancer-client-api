package com.easylancer.api.data.reactive

import com.easylancer.api.data.dto.DataResponseErrorDTO
import com.easylancer.api.data.dto.DataResponseSuccessDTO
import com.easylancer.api.data.dto.TaskDTO
import com.easylancer.api.data.dto.UserDTO
import com.easylancer.api.data.http.DataRequest
import com.easylancer.api.data.http.DataResponseError
import com.easylancer.api.data.reactive.exceptions.*
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.ConnectException

class DataApiClient(
        @Autowired private val webClient: WebClient
) {
    private val mapper: ObjectMapper = jacksonObjectMapper()

    private fun transformErrorResponse(req: DataRequest, resp: ClientResponse): Mono<DataApiResponseException> {
        return resp.bodyToMono(DataResponseErrorDTO::class.java).map { body ->
            DataApiResponseException(
                    "Received ${resp.rawStatusCode()} response",
                    request = req,
                    response = DataResponseError(resp.rawStatusCode(), body)
            )
        }.onErrorMap { e ->
            DataApiUnexpectedResponseException(
                    "Failed to parse error response body",
                    request = req,
                    response = DataResponseError(resp.rawStatusCode(), null),
                    cause = e
            )
        }
    }

    private fun transformException(req: DataRequest, e: Throwable): DataApiException {
        when (e) {
            is DataApiException ->
                return if (e is DataApiResponseException) {
                    when (e.response.statusCode) {
                        404 -> DataApiNotFoundException("No entity found with this id", e)
                        400 -> DataApiBadRequestException("Invalid or missing params", e)
                        else -> e
                    }
                } else e
            is JsonProcessingException ->
                return DataApiUnexpectedResponseException(
                        message = "Failed to parse success response body",
                        request = req,
                        response = DataResponseError(200, null),
                        cause = e
                )
            is ConnectException ->
                return DataApiNetworkException(
                        "Failed to access server",
                        request = req,
                        cause = e
                )
            else ->
                return DataApiUnhandledException(
                    message = "Unhandled exception occurred",
                    request = req,
                    cause = e
                )
        }
    }

    private inline fun <reified T> getEntity(resourceUrl: String, id: String): Mono<T> {
        val request = DataRequest("$resourceUrl/$id", HttpMethod.GET);

        return webClient.get()
                .uri(request.url)
                .retrieve()
                .onStatus(
                        { httpStatus -> HttpStatus.OK != httpStatus },
                        { response -> transformErrorResponse(request, response) }
                )
                .bodyToMono(JsonNode::class.java)
                .map { body -> mapper.treeToValue(body, DataResponseSuccessDTO::class.java) }
                .map { dto -> mapper.treeToValue(dto.data, T::class.java) }
                .onErrorMap { e -> transformException(request, e) }
    }

    fun getUser(id: String): Mono<UserDTO> {
        return getEntity("/users", id)
    }

    fun getTask(id: String): Mono<TaskDTO> {
        return getEntity("/tasks", id)
    }

}