package com.github.jactor.rises.persistence

import kotlinx.coroutines.reactor.awaitSingleOrNull
import reactor.core.publisher.Mono
import reactor.util.context.Context

object UsernameContext {
    private const val USERNAME_CONTEXT_KEY = "com.github.jactor.rises.persistence.username"

    fun currentUsername(): Mono<String> = Mono.deferContextual { contextView ->
        Mono.justOrEmpty(contextView.getOrEmpty(USERNAME_CONTEXT_KEY))
    }

    suspend fun fetchUsernameOrFail(): String = currentUsername().awaitSingleOrNull() ?: error(
        message = "No username in current reactor context!",
    )

    internal fun withUsername(context: Context, username: String): Context = context.put(USERNAME_CONTEXT_KEY, username)
}
