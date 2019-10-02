package com.easylancer.api.data.dto.types

enum class TaskStatus(val displayName: String) {
    DONE("done"),
    NOT_DONE("not-done"),
    ASSIGNED("assigned"),
    IN_PROGRESS("in-progress"),
    INVESTIGATE("investigate"),
    OPEN("open"),
    PENDING_WORKER_REVIEW("pending-worker-review"),
    PENDING_OWNER_REVIEW("pending-owner-review"),
    CANCELLED("cancelled");
}

val FINISHED_STATUSES =
        listOf(TaskStatus.DONE, TaskStatus.NOT_DONE, TaskStatus.INVESTIGATE, TaskStatus.CANCELLED)

val ASSIGNED_STATUSES =
        listOf(TaskStatus.IN_PROGRESS, TaskStatus.ASSIGNED)
