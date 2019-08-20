package com.easylancer.api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("data.api")
data class DataApiConfig(var url: String = "http://localhost", var port: Int = 3000)

@Component
@ConfigurationProperties("files.api")
data class FilesApiConfig(var url: String = "http://localhost:4000")