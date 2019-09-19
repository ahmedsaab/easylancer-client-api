package com.easylancer.api.security

import com.easylancer.api.data.dto.inbound.UserDTO
import com.easylancer.api.data.dto.inbound.UserSettingsDTO
import org.bson.types.ObjectId
import org.springframework.security.core.AuthenticatedPrincipal

class UserPrincipal(val user: UserDTO): AuthenticatedPrincipal {
    val id: ObjectId = user._id
    val settings: UserSettingsDTO = user.settings

    override fun getName(): String {
       return id.toHexString()
    }
}