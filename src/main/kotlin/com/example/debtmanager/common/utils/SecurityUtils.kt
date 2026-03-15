package com.example.debtmanager.common.utils

import com.example.debtmanager.common.exception.UnauthorizedException
import org.springframework.security.core.context.SecurityContextHolder
import java.util.UUID

object SecurityUtils {
    fun currentUserId(): UUID {
        val auth = SecurityContextHolder.getContext().authentication
        val principal = auth?.principal
        if (principal is UserPrincipal) {
            return principal.userId
        }
        throw UnauthorizedException()
    }
}

data class UserPrincipal(val userId: UUID, val email: String) : org.springframework.security.core.userdetails.UserDetails {
    override fun getAuthorities() = emptyList<org.springframework.security.core.GrantedAuthority>()
    override fun getPassword(): String? = null
    override fun getUsername(): String = email
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
}