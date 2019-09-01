package com.easylancer.api.data

import com.easylancer.api.data.dto.*
import com.easylancer.api.data.http.DataRequest
import com.easylancer.api.data.http.DataErrorResponse
import com.easylancer.api.data.exceptions.*
import com.easylancer.api.data.http.DataUnexpectedErrorResponse
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.util.CollectionUtils
import org.springframework.util.MultiValueMap
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
                    response = DataErrorResponse(resp.rawStatusCode(), body)
            )
        }.onErrorMap { e ->
            DataApiUnexpectedResponseException(
                    "Failed to parse error response body",
                    request = req,
                    response = DataUnexpectedErrorResponse(resp.rawStatusCode(), null),
                    cause = e
            )
        }
    }

    private fun handleException(req: DataRequest, e: Throwable): DataApiException {
        when (e) {
            is DataApiException ->
                return if (e is DataApiResponseException) {
                    when (e.response.statusCode) {
                        404 -> DataApiNotFoundException("No entity found with this id", e)
                        400 -> DataApiBadRequestException("Invalid or missing params", e)
                        405 -> DataApiUnprocessableEntityException("Semantically or logically invalid params combination", e)
                        409 -> DataApiConflictException("Requested state is not allowed given current state", e)
                        else -> e
                    }
                } else e
            is JsonProcessingException ->
                return DataApiUnexpectedResponseException(
                        message = "Failed to parse success response body",
                        request = req,
                        response = DataUnexpectedErrorResponse(200, null),
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
                .onErrorMap { e -> handleException(request, e) }
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
                .onErrorMap { e -> handleException(request, e) }
    }

    private inline fun <reified T> getEntities(url: String, filter: Map<String, List<String>> = HashMap<String, List<String>>()): Flux<T> {
        val request = DataRequest(url, HttpMethod.GET);
        val listType = mapper.typeFactory.constructCollectionType(ArrayList::class.java, T::class.java)

        return webClient.get()
            .uri {
                it.path(request.url)
                        .queryParams(CollectionUtils.toMultiValueMap(filter))
                        .build()
            }
            .retrieve()
            .onStatus(
                { httpStatus -> !httpStatus.is2xxSuccessful },
                { response -> transformErrorResponse(request, response) }
            )
            .bodyToMono(DataResponseSuccessDTO::class.java)
            .flatMapIterable { dto ->
                mapper.readValue<ArrayList<T>>(dto.data.toString(), listType)
            }.onErrorMap { e ->
                handleException(request, e)
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
                .bodyToMono(DataResponseSuccessDTO::class.java)
                .map { dto ->
                    mapper.treeToValue(dto.data, T::class.java)
                }
                .onErrorMap { e ->
                    handleException(request, e)
                }
    }

    private inline fun <reified T> deleteEntity(url: String): Mono<T> {
        val request = DataRequest(url, HttpMethod.DELETE);

        return webClient.delete()
                .uri(request.url)
                .retrieve()
                .onStatus(
                        { httpStatus -> !httpStatus.is2xxSuccessful },
                        { response -> transformErrorResponse(request, response) }
                )
                .bodyToMono(DataResponseSuccessDTO::class.java)
                .map { dto ->
                    mapper.treeToValue(dto.data, T::class.java)
                }
                .onErrorMap { e ->
                    handleException(request, e)
                }
    }

    private inline fun <reified T> postEntities(url: String, entity: Any? = mapper.createObjectNode()): Flux<T> {
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
                .bodyToMono(DataResponseSuccessDTO::class.java)
                .flatMapIterable { dto ->
                    mapper.readValue<ArrayList<T>>(dto.data.toString(), listType)
                }.onErrorMap { e ->
                    handleException(request, e)
                }
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
                .onErrorMap { e -> handleException(request, e) }
    }

    fun getUser(id: ObjectId): Mono<UserDTO> {
        return getEntity("/users/$id")
    }

    fun getUserByAuth(auth: String): Mono<UserDTO> {
        return findOneEntity("/users?auth=$auth")
    }

    fun getTask(id: ObjectId): Mono<TaskDTO> {
        return getEntity("/tasks/$id")
    }

    fun getFullTask(id: ObjectId): Mono<FullTaskDTO> {
        return getEntity("/tasks/$id/view")
    }

    fun getAllTasks(): Flux<SearchTaskDTO> {
        return getEntities("/tasks/search")
    }

    fun getUserFinishedTasks(id: ObjectId): Flux<TaskDTO> {
        return getEntities("/users/$id/tasks/finished")
    }

    fun getUserCreatedTasks(id: ObjectId): Flux<TaskDTO> {
        return getEntities("/users/$id/tasks/created")
    }

    fun getUserRelatedTasks(id: ObjectId): Flux<TaskDTO> {
        return getEntities("/users/$id/tasks")
    }

    fun getUserReviews(id: ObjectId): Flux<FullTaskRatingDTO> {
        return getEntities("/users/$id/reviews")
    }

    fun getTaskOffers(id: ObjectId): Flux<FullOfferDTO> {
        val query = HashMap<String, List<String>>()

        query["task"] = listOf(id.toHexString())

        return getEntities("/offers/view", query)
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

    fun taskSeenBy(id: ObjectId, userId: ObjectId): Flux<String> {
        return postEntities("/tasks/$id/seenBy/$userId")
    }

    fun putTask(taskId: ObjectId, task: Any): Mono<TaskDTO> {
        return putEntity("/tasks/$taskId", task)
    }

    fun putUser(userId: ObjectId, user: Any): Mono<UserDTO> {
        return putEntity("/users/$userId", user)
    }

    fun findOneOffer(offer: Map<String, List<String>>): Mono<OfferDTO> {
        return getEntities<OfferDTO>("/offers", offer).next()
    }

    fun deleteOffer(id: ObjectId): Mono<OfferDTO> {
        return deleteEntity("/offers/$id")
    }
}