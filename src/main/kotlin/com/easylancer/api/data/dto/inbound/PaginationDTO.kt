package com.easylancer.api.data.dto.inbound

import com.easylancer.api.dto.PaginationViewDTO
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import kotlin.math.ceil

@JsonPropertyOrder(alphabetic=true)
data class PaginationDTO<T>(
  val pageNo: Int,
  val pageSize: Int,
  val total: Int,
  val page: List<T>
) {
    fun <R>transform(transFunc: (T) -> R): PaginationViewDTO<R> = PaginationViewDTO(
        pageNo = pageNo + 1,
        totalPages = ceil(total.toDouble() / pageSize).toInt(),
        totalCount = total,
        page = page.map { p -> transFunc(p) }
    )
}