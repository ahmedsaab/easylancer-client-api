package com.easylancer.api.data.dto.types

enum class TaskStatus(val displayName: String) {
    DONE("done"),
    NOT_DONE("not-done"),
    ASSIGNED("assigned"),
    IN_PROGRESS("in-progress"),
    INVESTIGATE("investigate"),
    OPEN("open"),
    PENDING_REVIEW("pending-review"),
    CANCELLED("cancelled");
}

val FINISHED_STATUSES =
        listOf(TaskStatus.DONE, TaskStatus.NOT_DONE, TaskStatus.INVESTIGATE)

val ASSIGNED_STATUSES =
        listOf(TaskStatus.IN_PROGRESS, TaskStatus.ASSIGNED)
