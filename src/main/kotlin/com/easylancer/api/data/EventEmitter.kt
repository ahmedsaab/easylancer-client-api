package com.easylancer.api.data

import com.easylancer.api.data.blocking.DataApiClient
import kotlinx.coroutines.*
import org.springframework.beans.factory.annotation.Autowired


class EventEmitter(@Autowired private val dataClient: DataApiClient) {

    fun taskSeenByUser(id: String, userId: String) = GlobalScope.launch {
        dataClient.taskSeenBy(id, userId)
    };
}