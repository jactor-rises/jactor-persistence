package com.github.jactor.rises.persistence

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class UsernameWebFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> = exchange
        .getPrincipal<JwtAuthenticationToken>()
        .switchIfEmpty(Mono.error(IllegalStateException("No authentication name could be gathered")))
        .flatMap { authentication ->
            val username = authentication.name

            if (username.isBlank()) {
                Mono.error(IllegalStateException("The username is blank, no authentication name could be gathered"))
            } else {
                chain.filter(exchange).contextWrite { context ->
                    UsernameContext.withUsername(context, username)
                }
            }
        }
}
