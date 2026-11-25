package com.github.jactor.rises.persistence.test

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class RandomUsernameClient(
    restClientBuilder: RestClient.Builder = RestClient.builder(),
    baseUrl: String = "https://randomuser.me",
) {
    private val restClient = restClientBuilder
        .baseUrl(baseUrl)
        .build()

    fun fetchUsernames(count: Int = 5, nat: String? = null): List<String> = restClient.get()
        .uri { uriBuilder ->
            uriBuilder
                .path("/api/")
                .queryParam("inc", "login")
                .queryParam("results", count.coerceIn(1, 100))

            nat.whenNotNullOrBlank {
                uriBuilder.queryParam("nat", it)
            }

            uriBuilder.build()
        }
        .retrieve()
        .body<RandomUserResponse>()
        ?.users
        ?.map { it.login.username }
        ?: emptyList()
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
