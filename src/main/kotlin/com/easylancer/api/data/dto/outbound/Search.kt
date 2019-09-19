package com.easylancer.api.data.dto.outbound

import com.fasterxml.jackson.databind.JsonNode

data class Search (
    val pageNo: Int,
    val pageSize: Int,
    val query: JsonNode
)