package com.easylancer.api.data

import com.easylancer.api.files.FilesApiClient
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired


class EventEmitter(
        @Autowired private val dataClient: DataApiClient,
        @Autowired private val filesClient: FilesApiClient
) {

    fun taskSeenByUser(id: ObjectId, userId: ObjectId) {
        dataClient.taskSeenBy(id, userId).subscribe()
    };

    fun filesUsed(urls: Array<String>) {
        filesClient.confirm(urls).subscribe()
    };

    fun filesRemoved(urls: Array<String>) {
        filesClient.remove(urls).subscribe()
    };
}