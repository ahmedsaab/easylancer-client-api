package com.easylancer.api.data

import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.awaitLast
import org.springframework.beans.factory.annotation.Autowired


class EventEmitter(@Autowired private val dataClient: DataApiClient) {

    fun taskSeenByUser(id: String, userId: String) = GlobalScope.launch {
        dataClient.taskSeenBy(id, userId).awaitLast()
    };
}