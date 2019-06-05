package com.easylancer.api.helpers

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun Any.toJson(): ObjectNode = jacksonObjectMapper().valueToTree(this) as ObjectNode
