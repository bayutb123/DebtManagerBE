package com.example.debtmanager.security

import com.example.debtmanager.common.utils.UserPrincipal
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.time.Instant
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Component
class JwtTokenProvider(
    @Value("\${security.jwt.secret}") private val secret: String,
    @Value("\${security.jwt.expiration-minutes}") private val expirationMinutes: Long
) {

    private val key: Key by lazy {
        SecretKeySpec(secret.toByteArray(), SignatureAlgorithm.HS256.jcaName)
    }

    fun generateToken(userId: UUID, email: String): String {
        val now = Instant.now()
        val expiry = Date.from(now.plusSeconds(expirationMinutes * 60))
        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(Date.from(now))
            .setExpiration(expiry)
            .claim("email", email)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getAuthentication(token: String): UserPrincipal {
        val claims: Claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
        val userId = UUID.fromString(claims.subject)
        val email = claims["email", String::class.java]
        return UserPrincipal(userId, email)
    }
}