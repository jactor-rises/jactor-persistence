package com.github.jactor.rises.persistence.cucumber

import com.willowtreeapps.opentest4k.AssertionFailedError
import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Så
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

class RestClientStepDefs {
    private var baseUrl: String? = null
    private var endpoint: String? = null
    private var exchange: WebTestClient.ResponseSpec? = null

    val responseJson: String
        get() = (exchange ?: assertionError("Ingen exchange er gjennomført!"))
            .expectBody(String::class.java)
            .returnResult().responseBody ?: assertionError("Ingen respons fra server!")
    val statusCode: Int
        get() = (exchange ?: assertionError("Ingen exchange er gjennomført!"))
            .returnResult().status.value()

    @Gitt("base url {string}")
    fun `gitt base url`(baseUrl: String) {
        this.baseUrl = baseUrl
    }

    @Gitt("endpoint {string}")
    fun endpoint(endpoint: String?) {
        this.endpoint = endpoint
    }

    @Når("man utfører en http get")
    fun `når man utfører en http get`() {
        performGet()
    }

    @Når("en post gjøres for body:")
    fun `en post gjøres for body`(json: String?) {
        performPost(body = json)
    }

    @Så("skal statuskode være {int}")
    fun `skal statuskode være`(kode: Int?) {
        assertThat(statusCode).isEqualTo(kode)
    }

    @Så("responsen skal inneholde {string}")
    fun `skal responsen inneholde`(responseJson: String?) {
        assertThat(this.responseJson).isNotNull().contains(responseJson)
    }

    private fun bindWebTestClientToServer() = WebTestClient
        .bindToServer()
        .baseUrl(baseUrl ?: assertionError("baseUrl er ikke satt!"))
        .build()

    private fun performGet() {
        exchange = bindWebTestClientToServer ()
            .get()
            .uri(endpoint ?: assertionError("endpoint er ikke satt!"))
            .exchange()
    }

    private fun performPost(body: String?) {
        exchange = if (endpoint != null) {
            bindWebTestClientToServer()
                .post()
                .uri(endpoint!!)
        } else {
            bindWebTestClientToServer()
                .post()
        }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body ?: throw AssertionFailedError("ingen body request til post!"))
            .exchange()
    }

    private fun assertionError(message: String): Nothing = throw AssertionFailedError(message)
}
