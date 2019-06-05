package com.easylancer.api.security

import com.easylancer.api.data.dto.UserDTO
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.oauth2.jwt.Jwt
import java.util.stream.Collectors
import java.util.stream.Stream

class UserAuthToken(val user: UserDTO, private val jwt: Jwt): AbstractAuthenticationToken(null) {

    init {
        this.isAuthenticated = true
    }

    override fun getPrincipal(): UserPrincipal {
        return UserPrincipal(user)
    }

    override fun getCredentials(): Jwt {
        return jwt;
    }

    // TODO: implement authorities based on user dto info
    override fun getAuthorities(): Collection<GrantedAuthority> {
        val rolesStream = getRoles().stream().map { rn -> "ROLE_" + rn.name };
        val dataAuthStream = getDataAuthorities().stream();


        return AuthorityUtils.commaSeparatedStringToAuthorityList(
            Stream.concat(rolesStream, dataAuthStream)
                    .collect(Collectors.joining(","))
        )
    }

    // TODO: implement roles based on some flag in user dto info
    private fun getRoles(): List<UserRole> {
        return if (principal.id == "ahmed.saab.dev@gmail.com") {
            listOf(UserRole.ADMIN, UserRole.USER)
        } else {
            listOf(UserRole.USER)
        }
    }

    private fun getDataAuthorities(): List<String> {
        val authorities = mutableListOf<String>()

        authorities.add("user:read:${user._id}")
        authorities.add("user:edit:${user._id}")
        user.createdTasks.forEach { taskId ->
            authorities.add("task:owner:$taskId")
        }
        user.acceptedTasks.forEach { taskId ->
            authorities.add("task:worker:$taskId")
        }
        user.appliedTasks.forEach { taskId ->
            authorities.add("task:applied:$taskId")
        }

        return authorities
    }
}