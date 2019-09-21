package com.easylancer.api.controllers

import com.easylancer.api.data.DataApiClient
import com.easylancer.api.data.dto.inbound.PaginationDTO
import com.easylancer.api.data.dto.outbound.EqualFilter
import com.easylancer.api.data.dto.outbound.NotEqualFilter
import com.easylancer.api.data.dto.outbound.Query
import com.easylancer.api.data.dto.types.*
import com.easylancer.api.data.exceptions.DataApiBadRequestException
import com.easylancer.api.security.UserPrincipal
import com.easylancer.api.dto.*
import com.easylancer.api.exceptions.http.HttpBadRequestException
import com.easylancer.api.exceptions.http.HttpNotFoundException
import org.bson.types.ObjectId

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.access.prepost.PreAuthorize
import reactor.core.publisher.Mono
import reactor.core.publisher.onErrorMap

@RequestMapping("/my-tasks")
@RestController
class MyOrdersPageController(
        @Autowired private val client: DataApiClient
) {
    fun getCreatedTasks(
            userId: ObjectId,
            status: TaskStatus,
            pageNo: Int,
            pageSize: Int
    ): Mono<PaginationViewDTO<CreatedViewTaskDTO>> {
        return client.searchUserCreatedTasks(
                id = userId,
                status = status,
                pageNo = pageNo - 1,
                pageSize = pageSize
        ).map { pagination ->
            if(pagination.pageNo != 0 && pagination.page.isEmpty()) {
                throw HttpNotFoundException("page not found")
            }
            pagination.transform {
                task -> task.toView()
            }
        }.onErrorMap(DataApiBadRequestException::class) { e ->
            HttpBadRequestException("Invalid 'page' param, 'page' must be an integer not be less than 1", e)
        }
    }

    fun getAppliedTasks(
            userId: ObjectId,
            query: Query,
            pageNo: Int,
            pageSize: Int
    ): Mono<PaginationViewDTO<AppliedViewTaskDTO>> {
        return client.searchUserAppliedTasks(
                id = userId,
                query = query,
                pageNo = pageNo - 1,
                pageSize = pageSize
        ).map { pagination ->
            if(pagination.pageNo != 0 && pagination.page.isEmpty()) {
                throw HttpNotFoundException("page not found")
            }
            pagination.transform {
                task -> task.toView()
            }
        }.onErrorMap(DataApiBadRequestException::class) { e ->
            HttpBadRequestException("Invalid 'page' param, 'page' must be an integer not be less than 1", e)
        }
    }

    @GetMapping("/applied/open")
    fun getAppliedOpenTasks(
            @RequestParam("page") pageNo: Int = 1,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<PaginationViewDTO<AppliedViewTaskDTO>> {
        val query = Query()
            .filter("status",  EqualFilter(TaskStatus.OPEN))

        return getAppliedTasks(user.id, query, pageNo, 1)
    }

    @GetMapping("/applied/history")
    fun getAppliedHistoryTasks(
            @RequestParam("page") pageNo: Int = 1,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<PaginationViewDTO<AppliedViewTaskDTO>> {
        val query = Query()
            .filter("status",  NotEqualFilter(TaskStatus.OPEN))
            .filter("workerUser",  NotEqualFilter(user.id.toHexString()))

        return getAppliedTasks(user.id, query, pageNo, 100)
    }

    @GetMapping("/assigned/started")
    fun getAssignedStartedTasks(
            @RequestParam("page") pageNo: Int = 1,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<PaginationViewDTO<AppliedViewTaskDTO>> {
        val query = Query()
            .filter("status",  EqualFilter(TaskStatus.IN_PROGRESS))
            .filter("workerUser",  EqualFilter(user.id.toHexString()))

        return getAppliedTasks(user.id, query, pageNo, 100)
    }

    @GetMapping("/assigned/pending-worker-review")
    fun getAssignedPendingWorkerTasks(
            @RequestParam("page") pageNo: Int = 1,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<PaginationViewDTO<AppliedViewTaskDTO>> {
        val query = Query()
            .filter("status",  EqualFilter(TaskStatus.PENDING_REVIEW))
            .filter("workerUser",  EqualFilter(user.id.toHexString()))

        return getAppliedTasks(user.id, query, pageNo, 100)
    }

    @GetMapping("/assigned/scheduled")
    fun getAssignedScheduledTasks(
            @RequestParam("page") pageNo: Int = 1,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<PaginationViewDTO<AppliedViewTaskDTO>> {
        val query = Query()
            .filter("status",  EqualFilter(TaskStatus.ASSIGNED))
            .filter("workerUser",  EqualFilter(user.id.toHexString()))

        return getAppliedTasks(user.id, query, pageNo, 100)
    }

    @GetMapping("/assigned/done")
    fun getAssignedDoneTasks(
            @RequestParam("page") pageNo: Int = 1,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<PaginationViewDTO<AppliedViewTaskDTO>> {
        val query = Query()
            .filter("status",  EqualFilter(TaskStatus.DONE))
            .filter("workerUser",  EqualFilter(user.id.toHexString()))

        return getAppliedTasks(user.id, query, pageNo, 100)
    }

    @GetMapping("/assigned/not-done")
    fun getAssignedNotDoneTasks(
            @RequestParam("page") pageNo: Int = 1,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<PaginationViewDTO<AppliedViewTaskDTO>> {
        val query = Query()
            .filter("status",  EqualFilter(TaskStatus.NOT_DONE))
            .filter("workerUser",  EqualFilter(user.id.toHexString()))

        return getAppliedTasks(user.id, query, pageNo, 100)
    }

    @GetMapping("/assigned/pending-owner-review")
    fun getAssignedPendingOwnerTasks(
            @RequestParam("page") pageNo: Int = 1,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<PaginationViewDTO<AppliedViewTaskDTO>> {
        val query = Query()
            .filter("status",  EqualFilter(TaskStatus.PENDING_REVIEW))
            .filter("workerUser",  EqualFilter(user.id.toHexString()))

        return getAppliedTasks(user.id, query, pageNo, 100)
    }

    @GetMapping("/assigned/investigate")
    fun getAssignedInvestigateTasks(
            @RequestParam("page") pageNo: Int = 1,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<PaginationViewDTO<AppliedViewTaskDTO>> {
        val query = Query()
            .filter("status",  EqualFilter(TaskStatus.INVESTIGATE))
            .filter("workerUser",  EqualFilter(user.id.toHexString()))

        return getAppliedTasks(user.id, query, pageNo, 100)
    }

    @GetMapping("/assigned/cancelled")
    fun getAssignedCancelledTasks(
            @RequestParam("page") pageNo: Int = 1,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<PaginationViewDTO<AppliedViewTaskDTO>> {
        val query = Query()
                .filter("status",  EqualFilter(TaskStatus.CANCELLED))
                .filter("workerUser",  EqualFilter(user.id.toHexString()))

        return getAppliedTasks(user.id, query, pageNo, 100)
    }


    @GetMapping("/created/open")
    fun getCreatedOpenTasks(
            @RequestParam("page") pageNo: Int = 1,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<PaginationViewDTO<CreatedViewTaskDTO>> {
        return getCreatedTasks(user.id, TaskStatus.OPEN, pageNo, 100)
    }

    @GetMapping("/created/cancelled")
    fun getCreatedCancelledTasks(
            @RequestParam("page") pageNo: Int = 1,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<PaginationViewDTO<CreatedViewTaskDTO>> {
        return getCreatedTasks(user.id, TaskStatus.CANCELLED, pageNo, 100)
    }

    @GetMapping("/created/started")
    fun getCreatedStartedTasks(
            @RequestParam("page") pageNo: Int = 1,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<PaginationViewDTO<CreatedViewTaskDTO>> {
        return getCreatedTasks(user.id, TaskStatus.IN_PROGRESS, pageNo, 100)
    }

    @GetMapping("/created/pending-owner-review")
    fun getCreatedPendingOwnerTasks(
            @RequestParam("page") pageNo: Int = 1,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<PaginationViewDTO<CreatedViewTaskDTO>> {
        return getCreatedTasks(user.id, TaskStatus.PENDING_REVIEW, pageNo, 100)
    }

    @GetMapping("/created/scheduled")
    fun getCreatedScheduledTasks(
            @RequestParam("page") pageNo: Int = 1,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<PaginationViewDTO<CreatedViewTaskDTO>> {
        return getCreatedTasks(user.id, TaskStatus.ASSIGNED, pageNo, 100)
    }

    @GetMapping("/created/pending-worker-review")
    fun getCreatedPendingWorkerTasks(
            @RequestParam("page") pageNo: Int = 1,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<PaginationViewDTO<CreatedViewTaskDTO>> {
        return getCreatedTasks(user.id, TaskStatus.PENDING_REVIEW, pageNo, 100)
    }

    @GetMapping("/created/done")
    fun getCreatedDoneTasks(
            @RequestParam("page") pageNo: Int = 1,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<PaginationViewDTO<CreatedViewTaskDTO>> {
        return getCreatedTasks(user.id, TaskStatus.DONE, pageNo, 100)
    }

    @GetMapping("/created/not-done")
    fun getCreatedNotDoneTasks(
            @RequestParam("page") pageNo: Int = 1,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<PaginationViewDTO<CreatedViewTaskDTO>> {
        return getCreatedTasks(user.id, TaskStatus.NOT_DONE, pageNo, 100)
    }

    @GetMapping("/created/investigate")
    fun getCreatedInvestigateTasks(
            @RequestParam("page") pageNo: Int = 1,
            @AuthenticationPrincipal user: UserPrincipal
    ) : Mono<PaginationViewDTO<CreatedViewTaskDTO>> {
        return getCreatedTasks(user.id, TaskStatus.INVESTIGATE, pageNo, 100)
    }
}