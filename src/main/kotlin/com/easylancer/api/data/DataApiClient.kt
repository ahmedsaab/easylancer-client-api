package com.easylancer.api.data

import com.easylancer.api.data.dto.*
import com.easylancer.api.data.http.DataRequest
import com.easylancer.api.data.http.DataResponseError
import com.easylancer.api.data.exceptions.*
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.ConnectException
import java.util.ArrayList


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
                        message = "Failed to parse success response body (unexpected response)",
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
//            is IndexOutOfBoundsException ->
//                DataApiNotFoundException("No entity found with this filter")
            else ->
                return DataApiUnhandledException(
                    message = "Unhandled exception occurred",
                    request = req,
                    cause = e
                )
        }
    }

    private inline fun <reified T> getEntity(url: String): Mono<T> {
        val request = DataRequest(url , HttpMethod.GET);

        return webClient.get()
                .uri(request.url)
                .retrieve()
                .onStatus(
                        { httpStatus -> !httpStatus.is2xxSuccessful },
                        { response -> transformErrorResponse(request, response) }
                )
                .bodyToMono(JsonNode::class.java)
                .map { body -> mapper.treeToValue(body, DataResponseSuccessDTO::class.java) }
                .map { dto -> mapper.treeToValue(dto.data, T::class.java) }
                .onErrorMap { e -> transformException(request, e) }
    }

    private inline fun <reified T> findOneEntity(url: String): Mono<T> {
        val request = DataRequest(url, HttpMethod.GET);
        val listType = mapper.typeFactory.constructCollectionType(ArrayList::class.java, T::class.java)

        return webClient.get()
                .uri(request.url)
                .retrieve()
                .onStatus(
                        { httpStatus -> !httpStatus.is2xxSuccessful },
                        { response -> transformErrorResponse(request, response) }
                )
                .bodyToMono(JsonNode::class.java)
                .map { body -> mapper.treeToValue(body, DataResponseSuccessDTO::class.java) }
                .map { dto -> mapper.readValue<ArrayList<T>>(dto.data.toString(), listType) }
                .map { array -> array[0] }
                .onErrorMap { e -> transformException(request, e) }
    }

    private inline fun <reified T> getEntities(url: String): Flux<T> {
        val request = DataRequest(url, HttpMethod.GET);
        val listType = mapper.typeFactory.constructCollectionType(ArrayList::class.java, T::class.java)

        return webClient.get()
                .uri(request.url)
                .retrieve()
                .onStatus(
                        { httpStatus -> !httpStatus.is2xxSuccessful },
                        { response -> transformErrorResponse(request, response) }
                )
                .bodyToMono(DataResponseSuccessDTO::class.java)
                .flatMapIterable { dto ->
                    mapper.readValue<ArrayList<T>>(dto.data.toString(), listType)
                }.onErrorMap { e ->
                    transformException(request, e)
                }
    }

    private inline fun <reified T> postEntity(url: String, entity: Any): Mono<T> {
        val reqBody = mapper.valueToTree<JsonNode>(entity)
        val request = DataRequest(url, HttpMethod.POST, reqBody);

        return webClient.post()
                .uri(request.url)
                .body(BodyInserters.fromObject(reqBody))
                .retrieve()
                .onStatus(
                        { httpStatus -> !httpStatus.is2xxSuccessful },
                        { response -> transformErrorResponse(request, response) }
                )
                .bodyToMono(JsonNode::class.java)
                .map { body -> mapper.treeToValue(body, DataResponseSuccessDTO::class.java) }
                .map { dto -> mapper.treeToValue(dto.data, T::class.java) }
                .onErrorMap { e -> transformException(request, e) }
    }

    private inline fun <reified T> postEntities(url: String, entity: Any? = null): Flux<T> {
        val reqBody = mapper.valueToTree<JsonNode>(entity)
        val request = DataRequest(url, HttpMethod.POST, reqBody);
        val listType = mapper.typeFactory.constructCollectionType(ArrayList::class.java, T::class.java)

        return webClient.post()
                .uri(request.url)
                .body(BodyInserters.fromObject(reqBody))
                .retrieve()
                .onStatus(
                        { httpStatus -> !httpStatus.is2xxSuccessful },
                        { response -> transformErrorResponse(request, response) }
                )
                .bodyToMono(JsonNode::class.java)
                .map { body -> mapper.treeToValue(body, DataResponseSuccessDTO::class.java) }
                .flatMapIterable { dto -> mapper.readValue<ArrayList<T>>(dto.data.toString(), listType) }
                .onErrorMap { e -> transformException(request, e) }
    }

    private inline fun <reified T> putEntity(url: String, entity: Any): Mono<T> {
        val reqBody = mapper.valueToTree<JsonNode>(entity)
        val request = DataRequest(url, HttpMethod.PUT, reqBody);

        return webClient.put()
                .uri(request.url)
                .body(BodyInserters.fromObject(reqBody))
                .retrieve()
                .onStatus(
                        { httpStatus -> !httpStatus.is2xxSuccessful },
                        { response -> transformErrorResponse(request, response) }
                )
                .bodyToMono(JsonNode::class.java)
                .map { body -> mapper.treeToValue(body, DataResponseSuccessDTO::class.java) }
                .map { dto -> mapper.treeToValue(dto.data, T::class.java) }
                .onErrorMap { e -> transformException(request, e) }
    }

    fun getUser(id: String): Mono<UserDTO> {
        return getEntity("/users/$id")
    }

    fun getUserByAuth(auth: String): Mono<UserDTO> {
        return findOneEntity("/users?auth=$auth")
    }

    fun getTask(id: String): Mono<TaskDTO> {
        return getEntity("/tasks/$id")
    }

    fun getFullTask(id: String): Mono<FullTaskDTO> {
        return getEntity("/tasks/$id/view")
    }

    fun getAllTasks(): Flux<TaskDTO> {
        return getEntities("/tasks")
    }

    fun getUserFinishedTasks(id: String): Flux<TaskDTO> {
        return getEntities("/users/$id/tasks/finished")
    }

    fun getUserCreatedTasks(id: String): Flux<TaskDTO> {
        return getEntities("/users/$id/tasks/created")
    }

    fun getUserRelatedTasks(id: String): Flux<TaskDTO> {
        return getEntities("/users/$id/tasks")
    }

    fun getUserReviews(id: String): Flux<FullTaskRatingDTO> {
        return getEntities("/users/$id/reviews")
    }

    fun getTaskOffers(id: String): Flux<FullOfferDTO> {
        return getEntities("/offers/view?task=$id")
    }

    fun postTask(task: ObjectNode): Mono<TaskDTO> {
        return postEntity("/tasks", task)
    }

    fun postUser(user: ObjectNode): Mono<UserDTO> {
        return postEntity("/users", user)
    }

    fun postOffer(offer: ObjectNode): Mono<OfferDTO> {
        return postEntity("/offers", offer)
    }

    fun taskSeenBy(id: String, userId: String): Flux<String> {
        return postEntities("/tasks/$id/seenBy/$userId")
    }

    fun putTask(taskId: String, task: Any): Mono<TaskDTO> {
        return putEntity("/tasks/$taskId", task)
    }

    fun putUser(userId: String, user: Any): Mono<UserDTO> {
        return putEntity("/users/$userId", user)
    }

}