package com.github.jactor.rises.persistence.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.github.jactor.rises.persistence.JactorPersistence
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.CommandLineRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.json.JsonMapper
import java.text.SimpleDateFormat

@Configuration
@OpenAPIDefinition(info = Info(title = "jactor-persistence.misc", version = "v2"))
class JactorPersistenceConfig {
    @Bean
    fun commandLineRunner(applicationContext: ApplicationContext): CommandLineRunner = CommandLineRunner {
        JactorPersistence.inspect(applicationContext, it)
    }

    @Bean
    fun objectMapper(): ObjectMapper = JsonMapper.builder()
        .defaultDateFormat(SimpleDateFormat("yyyy-MM-dd"))
        .changeDefaultPropertyInclusion { it.withValueInclusion(JsonInclude.Include.NON_NULL) }
        .build()
}
