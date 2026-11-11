package com.github.jactor.rises.persistence

import assertk.assertThat
import assertk.assertions.isNotNull
import com.github.jactor.rises.persistence.test.AbstractSpringBootNoDirtyContextTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.test.web.reactive.server.WebTestClient

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class JactorPersistenceTest @Autowired constructor(
    private val commandLineRunner: CommandLineRunner?,
    private val webTestClient: WebTestClient,
) : AbstractSpringBootNoDirtyContextTest() {
    @Test
    fun `should contain bean named CommandlineRunner`() {
        assertThat(commandLineRunner).isNotNull()
    }

    @Test
    fun `should redirect to Swagger UI under the application base path`() {
        webTestClient
            .get()
            .uri("/swagger-ui.html")
            .exchange()
            .expectStatus()
            .is3xxRedirection
            .expectHeader()
            .exists("Location")

        webTestClient
            .get()
            .uri("/swagger-ui/index.html")
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun `should serve OpenAPI endpoints under the application base path`() {
        webTestClient
            .get()
            .uri("/v3/api-docs/swagger-config")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.configUrl")
            .isEqualTo("/jactor-persistence/v3/api-docs/swagger-config")
            .jsonPath("$.url")
            .isEqualTo("/jactor-persistence/v3/api-docs")

        webTestClient
            .get()
            .uri("/v3/api-docs")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.openapi")
            .exists()
    }
}
