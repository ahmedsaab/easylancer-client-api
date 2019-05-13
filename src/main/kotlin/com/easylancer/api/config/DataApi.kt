package com.easylancer.api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("data.api")
data class DataApi(var url: String = "http://localhost", var port: Int = 3000)