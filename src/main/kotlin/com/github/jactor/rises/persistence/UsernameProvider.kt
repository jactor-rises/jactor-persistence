package com.github.jactor.rises.persistence

import org.springframework.stereotype.Component

interface UsernameProvider {
    suspend fun fetchUsername(): String
}

@Component
class ContextUsernameProvider : UsernameProvider {
    override suspend fun fetchUsername(): String = UsernameContext.fetchUsernameOrFail()
}
