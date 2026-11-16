package com.github.jactor.rises.persistence.cucumber

import com.github.jactor.rises.persistence.cucumber.auth.AuthenticationServiceBean
import com.github.jactor.rises.persistence.cucumber.auth.SecurityConfig
import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

@CucumberContextConfiguration
@ContextConfiguration(classes = [CucumberSpringConfiguration::class])
@TestPropertySource(
    properties = [
        $$"app.security.jwt-secret=${JWT_SECRET}",
        $$"app.security.jactor-password-hash=${JACTOR_PASSWORD_HASH}",
    ],
)
class CucumberSpringContextConfiguration

@Configuration
@Import(SecurityConfig::class, AuthenticationServiceBean::class)
class CucumberSpringConfiguration
