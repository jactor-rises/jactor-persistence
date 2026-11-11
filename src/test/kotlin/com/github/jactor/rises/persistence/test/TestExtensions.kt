package com.github.jactor.rises.persistence.test

import assertk.Assert
import assertk.assertThat
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime

fun LocalDateTime.countSecondsUntilNow(): Long = Duration.between(this, LocalDateTime.now()).seconds

fun JwtEncoder.createToken(): String {
    val issuedAt = Instant.now()
    val claims = JwtClaimsSet.builder()
        .subject("jactor")
        .issuedAt(issuedAt)
        .expiresAt(issuedAt.plusSeconds(900))
        .build()

    return this.encode(
        JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims),
    ).tokenValue
}

infix fun <T> T?.named(name: String): Assert<T?> = assertThat(actual = this, name = name)
