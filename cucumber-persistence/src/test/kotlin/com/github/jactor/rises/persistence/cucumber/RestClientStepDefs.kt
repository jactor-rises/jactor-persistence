package com.github.jactor.rises.persistence.cucumber

import com.github.jactor.rises.persistence.cucumber.auth.AuthenticationService
import com.github.jactor.rises.persistence.cucumber.auth.JwtToken
import com.willowtreeapps.opentest4k.AssertionFailedError
import io.cucumber.java.no.Gitt
import io.cucumber.java.no.Når
import io.cucumber.java.no.Så
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

class RestClientStepDefs @Autowired constructor(
    private val authenticationService: AuthenticationService,
) {
    private var baseUrl: String? = null
    private var endpoint: String? = null
    private var exchange: WebTestClient.ResponseSpec? = null
    private var jwtToken: JwtToken? = null
    private var responseSpec: ResponseSpec? = null

    val responseJson: String? get() = responseSpec?.responseJson ?: exchange.initAndReturnResponseSpec().responseJson
    val statusCode: Int? get() = responseSpec?.statusCode ?: exchange.initAndReturnResponseSpec().statusCode

    @Gitt("base url {string}")
    fun `gitt base url`(baseUrl: String) {
        this.baseUrl = baseUrl
    }

    @Gitt("at autentiseringstoken genereres")
    fun `gitt autentiseringstoken genereres`() {
        val jactorPassword = requireNotNull(System.getenv("JACTOR_PASSWORD")) {
            "JACTOR_PASSWORD must be set to create an authorization token for Cucumber tests"
        }
        jwtToken = runBlocking {
            authenticationService.authenticate(username = "jactor", password = jactorPassword)
        } ?: assertionError("Could not create an authorization token for Cucumber tests")
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
        val requestJwtToken = jwtTokenForNextRequest()
        responseSpec = null

        val uriSpec = bindWebTestClientToServer()
            .get()
            .uri { builder ->
                builder.path(endpoint ?: assertionError("endpoint er ikke satt!"))
                parameter?.let { builder.queryParam(it.first, it.second) }
                builder.build()
            }

        requestJwtToken?.let { token ->
            uriSpec.headers { it.set(HttpHeaders.AUTHORIZATION, token.createBearerToken()) }
        }

        exchange = uriSpec.exchange()
    }

    private fun performPut() {
        val requestJwtToken = jwtTokenForNextRequest()
        responseSpec = null

        val uriSpec = bindWebTestClientToServer()
            .put()
            .uri { builder ->
                builder.path(endpoint ?: assertionError("endpoint er ikke satt!"))
                builder.build()
            }

        requestJwtToken?.let { token ->
            uriSpec.headers { it.set(HttpHeaders.AUTHORIZATION, token.createBearerToken()) }
        }

        exchange = uriSpec.exchange()
    }

    private fun performPost(body: String?) {
        val requestBody = body ?: assertionError("ingen body request til post!")
        val requestJwtToken = jwtTokenForNextRequest()
        responseSpec = null

        val uriSpec = if (endpoint != null) {
            bindWebTestClientToServer(requestBody).post().uri(endpoint!!)
        } else {
            bindWebTestClientToServer(requestBody).post()
        }

        requestJwtToken?.let { token ->
            uriSpec.headers { it.set(HttpHeaders.AUTHORIZATION, token.createBearerToken()) }
        }

        exchange = uriSpec
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
    }

    private fun jwtTokenForNextRequest(): JwtToken? {
        val latestResponse = responseSpec ?: exchange?.initAndReturnResponseSpec()
        latestResponse?.jwtToken?.let { jwtToken = it }
        return jwtToken
    }

    private fun WebTestClient.ResponseSpec?.initAndReturnResponseSpec() = ResponseSpec(
        responseSpec = this ?: assertionError("Ingen ResponseSpec i exchange")
    ).also { responseSpec = it }
}

private fun assertionError(message: String): Nothing = throw AssertionFailedError(message)

class ResponseSpec(responseSpec: WebTestClient.ResponseSpec) {
    var jwtToken: JwtToken? = null
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
            bodyContentSpec.responseHeaders["Authorization"]
                ?.firstOrNull()
                ?.removePrefix("Bearer ")
                ?.let { tokenValue -> jwtToken = JwtToken(id = UUID.randomUUID(), tokenValue = tokenValue) }
        }
    }
}
