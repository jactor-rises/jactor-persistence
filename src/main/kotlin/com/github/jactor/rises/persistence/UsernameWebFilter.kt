package com.github.jactor.rises.persistence

import com.github.jactor.rises.shared.whenTrue
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.Optional

@Component
class UsernameWebFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> = exchange
        .getPrincipal<JwtAuthenticationToken>()
        .map { authentication -> Optional.of(authentication.name) }
        .defaultIfEmpty(Optional.empty())
        .flatMap { nameOfAuthenticatedUser ->
            val downstream = chain.filter(exchange)

            nameOfAuthenticatedUser.isPresent.whenTrue {
                val username = nameOfAuthenticatedUser.get()
                username.isBlank().whenTrue { error("The username is blank, no authentication name could be gathered") }

                downstream.contextWrite { context -> UsernameContext.withUsername(context, username) }
            } ?: downstream
        }
}
