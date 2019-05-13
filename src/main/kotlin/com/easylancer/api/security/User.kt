package com.easylancer.api.security

import com.easylancer.api.data.dto.UserDTO
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails
import java.util.stream.Collectors

class User(val dto: UserDTO) : UserDetails {
    val id = dto._id

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return AuthorityUtils.commaSeparatedStringToAuthorityList(
                getRoles().stream().map { rn -> "ROLE_" + rn.name }.collect(Collectors.joining(",")))
    }

    private fun getRoles(): List<Role> {
        return if(dto.email == "ahmed.saab.dev@gmail.com") {
            listOf(Role.ADMIN, Role.USER)
        } else {
            listOf(Role.USER)
        }
    }

    override fun getUsername(): String {
        return dto.email
    }

    override fun getPassword(): String {
        return dto.password
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}
