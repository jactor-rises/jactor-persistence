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
    private var responseSpec: ResponseSpec? = null

    val responseJson: String? get() = responseSpec?.responseJson ?: exchange.initAndReturnResponseSpec().responseJson
    val statusCode: Int? get() = responseSpec?.statusCode ?: exchange.initAndReturnResponseSpec().statusCode

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

    @Når("man utfører en http put")
    fun `når man utfører en http put`() {
        performPut()
    }

    @Når("man utfører en http get med parameter {string} = {string}")
    fun `når man utfører en http get med parameter {string} = {string}`(name: String, value: String) {
        performGet(parameter = name to value)
    }

    @Når("en post gjøres for body:")
    fun `en post gjøres for body`(json: String?) {
        performPost(body = json)
    }

    @Så("skal statuskode være {int}")
    fun `skal statuskode være`(kode: Int?) {
        assertThat(statusCode).isEqualTo(kode)
    }

    @Så("skal statuskode være enten {int} eller {int}")
    fun `skal statuskode være enten-eller`(enKode: Int?, enAnnenKode: Int?) {
        assertThat(statusCode).isIn(enKode, enAnnenKode)
    }

    @Så("responsen skal inneholde {string}")
    fun `skal responsen inneholde`(responseJson: String?) {
        assertThat(this.responseJson).isNotNull().contains(responseJson)
    }

    private fun bindWebTestClientToServer() = WebTestClient
        .bindToServer()
        .baseUrl(baseUrl ?: assertionError("baseUrl er ikke satt!"))
        .build()

    private fun performGet(parameter: Pair<String, String>? = null) {
        exchange = bindWebTestClientToServer()
            .get()
            .uri { builder ->
                builder.path(endpoint ?: assertionError("endpoint er ikke satt!"))
                parameter?.let { builder.queryParam(it.first, it.second) }
                builder.build()
            }.exchange()
    }

    private fun performPut() {
        exchange = bindWebTestClientToServer()
            .put()
            .uri { builder ->
                builder.path(endpoint ?: assertionError("endpoint er ikke satt!"))
                builder.build()
            }.exchange()
    }

    private fun performPost(body: String?) {
        exchange = if (endpoint != null) {
            bindWebTestClientToServer().post().uri(endpoint!!)
        } else {
            bindWebTestClientToServer().post()
        }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body ?: assertionError("ingen body request til post!"))
            .exchange()
    }

    private fun WebTestClient.ResponseSpec?.initAndReturnResponseSpec() = ResponseSpec(
        responseSpec = this ?: assertionError("Ingen ResponseSpec i exchange")
    ).also { responseSpec = it }
}

private fun assertionError(message: String): Nothing = throw AssertionFailedError(message)

class ResponseSpec(responseSpec: WebTestClient.ResponseSpec) {
    var responseJson: String? = null
    var statusCode: Int? = null

    init {
        val bodyContentSpec = responseSpec.expectBody().returnResult().also {
            println("> status=${it.status}")
            println("> headers=${it.responseHeaders}")
            println("> body=${it.responseBody?.decodeToString()}")
        }

        bodyContentSpec.responseBody?.decodeToString()?.let { responseJson = it }
        statusCode = bodyContentSpec.status.value()
    }
}
