package com.easylancer.api.data

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.HttpMethod

data class DataRequest(val url: String, val method: HttpMethod, val body: JsonNode? = null)