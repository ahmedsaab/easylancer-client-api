package com.easylancer.api.dto

import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder(alphabetic=true)
data class PaginationViewDTO<T>(
  val pageNo: Int,
  val totalPages: Int,
  val totalCount: Int,
  val page: List<T>
)