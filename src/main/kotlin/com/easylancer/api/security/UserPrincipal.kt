package com.easylancer.api.security

import com.easylancer.api.data.dto.UserDTO
import org.springframework.security.core.AuthenticatedPrincipal

class UserPrincipal(val user: UserDTO): AuthenticatedPrincipal {
    val id: String = user._id

    override fun getName(): String {
       return id
    }
}