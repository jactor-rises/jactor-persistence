package com.github.jactor.rises.persistence.admin

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/admin"], produces = [MediaType.APPLICATION_JSON_VALUE])
class AdminController(private val adminService: AdminService) {
    @PutMapping("/move/{username}")
    suspend fun moveInactiveUser(
        @PathVariable username: String,
    ): ResponseEntity<Void> = when (adminService.moveInactiveUser(username = username)) {
        true -> ResponseEntity.ok().build()
        false -> ResponseEntity.noContent().build()
    }
}
