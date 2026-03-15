package com.example.debtmanager.application.service

import com.example.debtmanager.application.dto.GoogleAuthCommand
import com.example.debtmanager.common.exception.UnauthorizedException
import com.example.debtmanager.domain.model.Provider
import com.example.debtmanager.domain.model.User
import com.example.debtmanager.domain.repository.UserRepository
import com.example.debtmanager.infrastructure.security.GoogleTokenVerifier
import com.example.debtmanager.security.JwtTokenProvider
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val googleTokenVerifier: GoogleTokenVerifier,
    private val jwtTokenProvider: JwtTokenProvider
) {
    data class AuthResult(val token: String, val user: User)

    fun authenticate(command: GoogleAuthCommand): AuthResult {
        val payload = try {
            googleTokenVerifier.verify(command.idToken)
        } catch (ex: Exception) {
            throw UnauthorizedException("Invalid Google token")
        }
        val email = payload.email
        val name = payload["name"] as? String ?: payload.email
        val picture = payload["picture"] as? String ?: ""

        val existing = userRepository.findByEmail(email)
        val user = existing ?: userRepository.save(
            User(
                id = UUID.randomUUID(),
                email = email,
                name = name,
                pictureUrl = picture,
                provider = Provider.GOOGLE,
                createdAt = Instant.now()
            )
        )
        val token = jwtTokenProvider.generateToken(user.id, user.email)
        return AuthResult(token, user)
    }
}