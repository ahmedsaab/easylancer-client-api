package com.easylancer.api.data

import com.easylancer.api.files.FilesApiClient
import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.awaitLast
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired


class EventEmitter(
        @Autowired private val dataClient: DataApiClient,
        @Autowired private val filesClient: FilesApiClient
) {

    fun taskSeenByUser(id: ObjectId, userId: ObjectId) = GlobalScope.launch {
        dataClient.taskSeenBy(id, userId).awaitLast()
    };

    fun filesUsed(urls: Array<String>) = GlobalScope.launch {
        filesClient.confirm(urls).awaitLast()
    };
}