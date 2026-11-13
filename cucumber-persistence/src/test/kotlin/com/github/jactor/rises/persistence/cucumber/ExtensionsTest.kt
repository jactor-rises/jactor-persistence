package com.github.jactor.rises.persistence.cucumber

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import tools.jackson.databind.json.JsonMapper

class ExtensionsTest {
    @Test
    fun `somJson serializes request objects as valid escaped json`() {
        val requestBody = LoginRequest(
            username = "Dr. A",
            password = "Cula"
        )

        val json = requestBody.somJson()
        val objectMapper = JsonMapper.builder().build()

        assertThat(objectMapper.readTree(json))
            .isEqualTo(objectMapper.readTree("""{"username":"Dr. A","password":"Cula"}"""))
    }
}
