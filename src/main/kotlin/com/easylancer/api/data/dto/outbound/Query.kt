package com.easylancer.api.data.dto.outbound

import com.easylancer.api.helpers.toJson
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class Query {
    private val json: ObjectNode = jacksonObjectMapper().createObjectNode();

    fun filter(property: String, filter: Filter<Any>): Query {
        json.set(property, filter.toJson())
        return this;
    }

    fun toJson(): JsonNode {
        return json;
    }
}