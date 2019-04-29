package com.easylancer.api.data

import kotlinx.coroutines.*
import org.springframework.beans.factory.annotation.Autowired


class EventEmitter(@Autowired private val dataClient: DataAPIClient) {

    fun taskSeenByUser(id: String, userId: String) = GlobalScope.launch {
        dataClient.taskSeenBy(id, userId)
    };
}