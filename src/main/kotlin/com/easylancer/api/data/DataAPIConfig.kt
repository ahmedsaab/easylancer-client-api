package com.easylancer.api.data

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("data.api")
data class DataAPIConfig(var url: String = "http://localhost", var port: Int = 3000)