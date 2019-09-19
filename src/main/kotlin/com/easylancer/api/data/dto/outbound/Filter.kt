package com.easylancer.api.data.dto.outbound

import com.easylancer.api.data.dto.types.FilterType

abstract class Filter<T>(
    val type: FilterType,
    val value: T? = null,
    val values: Array<T>? = null
)

abstract class SingleFilter<T>(
    type: FilterType,
    value: T
) : Filter<T>(type, value)

abstract class MultiFilter<T>(
    type: FilterType,
    values: Array<T>
) : Filter<T>(type, null, values)

class EqualFilter<T>(value: T): SingleFilter<T>(type = FilterType.EQ, value = value)

class NotEqualFilter<T>(value: T): SingleFilter<T>(type = FilterType.NQ, value = value)

class InFilter<T>(values: Array<T>): MultiFilter<T>(type = FilterType.IN, values = values)


