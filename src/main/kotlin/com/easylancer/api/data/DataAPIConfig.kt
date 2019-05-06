package com.easylancer.api.data

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("data.api")
class DataAPIConfig {
    var url: String = "http://localhost";
    var port: String = "5000";
}