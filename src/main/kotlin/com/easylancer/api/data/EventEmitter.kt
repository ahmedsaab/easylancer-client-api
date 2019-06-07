package com.easylancer.api.data

import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.awaitLast
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired


class EventEmitter(@Autowired private val dataClient: DataApiClient) {

    fun taskSeenByUser(id: ObjectId, userId: ObjectId) = GlobalScope.launch {
        dataClient.taskSeenBy(id, userId).awaitLast()
    };
}