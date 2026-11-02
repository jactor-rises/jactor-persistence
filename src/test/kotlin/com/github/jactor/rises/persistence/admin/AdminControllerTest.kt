package com.github.jactor.rises.persistence.admin

import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(AdminController::class)
class AdminControllerTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    @MockkBean private val adminServiceMockk: AdminService,
) {
    @Test
    fun `should move deadbeat user from user to inactive user table`() {
        coEvery { adminServiceMockk.moveInactiveUser(username = "deadbeat") } returns true

        webTestClient
            .put()
            .uri("/admin/move/deadbeat")
            .exchange()
            .expectStatus()
            .isOk()
    }

    @Test
    fun `should not move deadbeat user from user to inactive user table when deadbeat is not a active user`() {
        coEvery { adminServiceMockk.moveInactiveUser(username = "deadbeat") } returns false

        webTestClient
            .put()
            .uri("/admin/move/deadbeat")
            .exchange()
            .expectStatus()
            .isNoContent()
    }
}
