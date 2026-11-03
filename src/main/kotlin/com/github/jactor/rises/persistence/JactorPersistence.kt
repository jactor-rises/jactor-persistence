package com.github.jactor.rises.persistence

import com.github.jactor.rises.shared.SpringBeanNames
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import kotlin.collections.forEach

private val logger = KotlinLogging.logger {}

@SpringBootApplication
@OpenAPIDefinition(info = Info(title = "jactor-persistence", version = "v2"))
class JactorPersistence {
    companion object {
        fun inspect(
            applicationContext: ApplicationContext,
            args: Array<String>,
        ) {
            logger.info { "Starting application ${list(args)}" }
            SpringBeanNames().loggSimpleNames(applicationContext = applicationContext)
            logger.info { "Ready for service..." }
        }
    }
}

private fun SpringBeanNames.loggSimpleNames(applicationContext: ApplicationContext) {
    applicationContext.beanDefinitionNames.sorted().forEach(this::add)

    logger.info { "Available beans (only simple names):" }

    names.forEach {
        logger.info { "> $it" }
    }
}

private fun list(args: Array<String>) = when (args.isEmpty()) {
    true -> "without arguments!"
    false -> "with arguments: ${args.joinToString { " " }}!"
}

fun main(args: Array<String>) {
    runApplication<JactorPersistence>(*args)
}
