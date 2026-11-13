package com.github.jactor.rises.persistence.cucumber

import com.willowtreeapps.opentest4k.AssertionFailedError
import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Så
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.fail
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.HeaderAssertions
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

class RestClientStepDefs {
    private var baseUrl: String? = null
    private var endpoint: String? = null
    private var exchange: WebTestClient.ResponseSpec? = null
    private var loginRequest: LoginRequest? = null
    private var responseSpec: ResponseSpec? = null
    private var authorizationToken: String? = null

    val responseJson: String? get() = responseSpec?.responseJson ?: exchange.initAndReturnResponseSpec().responseJson
    val statusCode: Int? get() = responseSpec?.statusCode ?: exchange.initAndReturnResponseSpec().statusCode

    @Gitt("base url {string}")
    fun `gitt base url`(baseUrl: String) {
        this.baseUrl = baseUrl
    }

    @Gitt("en body for innlogging som inneholder brukernavn {string} og passord hentet fra {string}")
    fun `gitt en body for innlogging`(brukernavn: String, fraPassordLagring: String) {
        loginRequest = LoginRequest(
            username = brukernavn,
            password = when (fraPassordLagring) {
                "JACTOR_PASSWORD" -> System.getenv("JACTOR_PASSWORD") ?: randomUuidSomTekst()
                else -> randomUuidSomTekst()
            }
        )
    }

    private fun randomUuidSomTekst(): String = UUID.randomUUID().toString()

    @Gitt("endpoint {string}")
    fun endpoint(endpoint: String?) {
        this.endpoint = endpoint
    }

    @Når("man utfører en http get")
    fun `når man utfører en http get`() {
        performGet()
    }

    @Når("man utfører en http post")
    fun `når man utfører en http post`() {
        performPost(body = loginRequest?.somJson())
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

    @Så("sendes denne body med en http post")
    fun `så sendes denne body med en http post`() {
        performPost(body = loginRequest?.somJson())
    }

    @Så("responsen skal inneholde {string}")
    fun `skal responsen inneholde`(responseJson: String?) {
        assertThat(this.responseJson).isNotNull().contains(responseJson)
    }

    @Så("responsen skal inneholde en header {string} som starter med strengen {string}")
    fun `skal responsen inneholde header som starter med strengen`(headerNavn: String, starterMed: String?) {
        val expectHeader: HeaderAssertions = exchange?.expectHeader() ?: fail { "Expected headere fra response" }

        expectHeader.exists(headerNavn)
        expectHeader.value(headerNavn) { value ->
            assertThat(value).isNotNull().startsWith(starterMed)
        }
    }

    private fun bindWebTestClientToServer(requestBody: String? = null) = WebTestClient
        .bindToServer()
        .baseUrl(baseUrl ?: assertionError("baseUrl er ikke satt!"))
        .filter { request, next ->
            val requestHeaders: Map<String, List<String>> = request.headers().headerSet().associate { header ->
                val headerValues = if (header.key.equals(HttpHeaders.AUTHORIZATION, ignoreCase = true) ||
                    header.key.equals(HttpHeaders.PROXY_AUTHORIZATION, ignoreCase = true) ||
                    header.key.equals(HttpHeaders.COOKIE, ignoreCase = true)
                ) {
                    listOf("[REDACTED]")
                } else {
                    header.value
                }
                header.key to headerValues
            }
            println("> request uri=${request.url()}")
            println("> request headers=$requestHeaders")
            println("> request body=${requestBody ?: "<none>"}")
            next.exchange(request)
        }
        .build()

    private fun performGet(parameter: Pair<String, String>? = null) {
        val bearerToken = bearerTokenForNextRequest()
        responseSpec = null

        val uriSpec = bindWebTestClientToServer()
            .get()
            .uri { builder ->
                builder.path(endpoint ?: assertionError("endpoint er ikke satt!"))
                parameter?.let { builder.queryParam(it.first, it.second) }
                builder.build()
            }

        bearerToken?.let { token -> uriSpec.headers { it.setBearerAuth(token) } }

        exchange = uriSpec.exchange()
    }

    private fun performPut() {
        val bearerToken = bearerTokenForNextRequest()
        responseSpec = null

        val uriSpec = bindWebTestClientToServer()
            .put()
            .uri { builder ->
                builder.path(endpoint ?: assertionError("endpoint er ikke satt!"))
                builder.build()
            }

        bearerToken?.let { token -> uriSpec.headers { it.setBearerAuth(token) } }

        exchange = uriSpec.exchange()
    }

    private fun performPost(body: String?) {
        val requestBody = body ?: assertionError("ingen body request til post!")
        val bearerToken = bearerTokenForNextRequest()
        responseSpec = null

        val uriSpec = if (endpoint != null) {
            bindWebTestClientToServer(requestBody).post().uri(endpoint!!)
        } else {
            bindWebTestClientToServer(requestBody).post()
        }

        bearerToken?.let { token -> uriSpec.headers { it.setBearerAuth(token) } }

        exchange = uriSpec
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
    }

    private fun bearerTokenForNextRequest(): String? {
        val latestResponse = responseSpec ?: exchange?.initAndReturnResponseSpec()
        latestResponse?.bearerToken?.let { authorizationToken = it }
        return authorizationToken
    }

    private fun WebTestClient.ResponseSpec?.initAndReturnResponseSpec() = ResponseSpec(
        responseSpec = this ?: assertionError("Ingen ResponseSpec i exchange")
    ).also { responseSpec = it }
}

private fun assertionError(message: String): Nothing = throw AssertionFailedError(message)

class ResponseSpec(responseSpec: WebTestClient.ResponseSpec) {
    var bearerToken: String? = null
    var responseJson: String? = null
    var statusCode: Int? = null

    init {
        val bodyContentSpec = responseSpec.expectBody().returnResult().also { result ->
            println("> status=${result.status}")
            println("> headers=${result.responseHeaders}")
            println("> body=${result.responseBody?.decodeToString()?.let { "length: ${it.count()} characters" }}")
        }

        bodyContentSpec.responseBody?.decodeToString()?.let { responseJson = it }
        statusCode = bodyContentSpec.status.value()
        bodyContentSpec.responseHeaders.containsHeader("Authorization").whenTrue {
            bearerToken = bodyContentSpec.responseHeaders["Authorization"]?.firstOrNull()?.removePrefix("Bearer ")
        }
    }
}

@JvmRecord
data class LoginRequest(
    val username: String,
    val password: String,
)
