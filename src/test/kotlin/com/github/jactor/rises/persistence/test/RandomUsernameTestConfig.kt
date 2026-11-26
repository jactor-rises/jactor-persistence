package com.github.jactor.rises.persistence.test

import com.github.jactor.rises.persistence.UsernameProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class RandomUsernameTestConfig @Autowired constructor(
    private val randomUsernameClient: RandomUsernameClient,
) {
    @Bean
    @Primary
    fun randomUsernameProvider(): UsernameProvider = randomUsernameClient.also {
        it.fetchUsernames(total = 100)
    }
}
