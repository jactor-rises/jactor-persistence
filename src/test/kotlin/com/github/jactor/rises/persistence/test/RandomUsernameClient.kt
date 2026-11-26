package com.github.jactor.rises.persistence.test

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jactor.rises.persistence.UsernameProvider
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class RandomUsernameClient(
    restClientBuilder: RestClient.Builder = RestClient.builder(),
    baseUrl: String = "https://randomuser.me",
) : UsernameProvider {
    private val usernames = mutableListOf<String>()
    private val restClient = restClientBuilder
        .baseUrl(baseUrl)
        .build()

    internal fun fetchUsernames(total: Int = 1, nat: String? = null): List<String> = restClient.get()
        .uri { uriBuilder ->
            uriBuilder
                .path("/api/")
                .queryParam("inc", "login")
                .queryParam("results", total.coerceIn(1, 100))

            nat.whenNotNullOrBlank {
                uriBuilder.queryParam("nat", it)
            }

            uriBuilder.build()
        }.let { requestHeaderSpec ->
            val usernames = requestHeaderSpec.retrieve()
                .body<RandomUserResponse>()
                ?.users
                ?.map { it.login.username }

            usernames?.let { this.usernames.addAll(it) }

            usernames ?: emptyList()
        }

    override suspend fun fetchUsername(): String = usernames.random()
}

@JsonIgnoreProperties(ignoreUnknown = true)
private data class RandomUserResponse(
    @JsonProperty("results")
    val users: List<User> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class User(
    val login: LoginInfo,
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class LoginInfo(
    val username: String,
)
