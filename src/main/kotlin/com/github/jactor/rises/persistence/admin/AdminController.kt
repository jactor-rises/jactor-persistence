package com.github.jactor.rises.persistence.admin

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/admin"], produces = [MediaType.APPLICATION_JSON_VALUE])
class AdminController(
    private val adminService: AdminService,
    private val authenticationService: AuthenticationService,
) {
    @PostMapping("/login", consumes = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun login(
        @RequestBody loginRequest: LoginRequest,
    ): ResponseEntity<Void> = authenticationService.authenticate(
        username = loginRequest.username,
        password = loginRequest.password,
    )?.let { jwtToken ->
        ResponseEntity
            .ok()
            .header(HttpHeaders.AUTHORIZATION, jwtToken.createBearerToken())
            .build()
    } ?: ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

    @PutMapping("/move/{username}")
    suspend fun moveInactiveUser(
        @PathVariable username: String,
    ): ResponseEntity<Void> = when (adminService.moveInactiveUser(username = username)) {
        true -> ResponseEntity.ok().build()
        false -> ResponseEntity.noContent().build()
    }
}

@JvmRecord
data class LoginRequest(
    val username: String,
    val password: String,
)
