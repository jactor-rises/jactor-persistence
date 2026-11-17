package com.github.jactor.rises.persistence.test

import com.github.jactor.rises.persistence.UsernameContext
import com.github.jactor.rises.persistence.UsernameProvider
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import java.util.concurrent.atomic.AtomicReference

@TestConfiguration
class RandomUsernameTestConfig {
    @Bean
    @Primary
    fun randomUsernameProvider(): UsernameProvider = RandomUsernameProvider()
}

class RandomUsernameProvider : UsernameProvider {
    companion object {
        val usernameHolder: AtomicReference<String?> = AtomicReference()
    }

    override suspend fun fetchUsername(): String =
        UsernameContext.currentUsername().awaitSingleOrNull()
            ?: usernameHolder.get()
            ?: error("Random username extension did not initialize this test")
}
