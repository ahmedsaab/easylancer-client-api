package com.easylancer.api.security

import com.easylancer.api.data.dto.UserDTO
import org.bson.types.ObjectId
import org.springframework.security.core.AuthenticatedPrincipal

class UserPrincipal(val user: UserDTO): AuthenticatedPrincipal {
    val id: ObjectId = user._id

    override fun getName(): String {
       return id.toHexString()
    }
}