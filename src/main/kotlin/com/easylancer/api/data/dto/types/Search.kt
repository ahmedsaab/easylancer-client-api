package com.easylancer.api.data.dto.types

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty

enum class FilterType {
    @JsonProperty("eq") EQ,
    @JsonProperty("nq") NQ,
    @JsonProperty("in") IN;
}
