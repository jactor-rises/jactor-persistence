plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.versions)
}

version = "0.0.0-SNAPSHOT"

dependencies {
    // spring-boot
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.spring.boot.starter.webflux)

    // jetbrains-exposed
    implementation(libs.exposed.core)
    implementation(libs.exposed.java.time)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.exposed.spring.boot.starter)
    implementation(libs.uuid.generator)

    // misc third party dependencies
    implementation(libs.kotlin.logging)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactor)
    implementation(libs.springdoc.openapi.ui)

    // internal project dependencies
    implementation(libs.jactor.shared)

    // runtime dependencies
    runtimeOnly(libs.spring.boot.flyway)
    runtimeOnly(libs.h2database)

    // test implementations
    testImplementation(libs.assertk)
    testImplementation(libs.cucumber.java)
    testImplementation(libs.cucumber.java8)
    testImplementation(libs.cucumber.junit.platform.engine)
    testImplementation(libs.cucumber.spring)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.springmockk)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webflux.test)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.jvm.get().toInt())
    }

    withSourcesJar()
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://jitpack.io") }
}

tasks.register("printVersion") {
    doLast { println(project.version) }
}

tasks {
    val ktlintFormatAndCheck by registering {
        dependsOn("ktlintFormat")
        doLast {
            ProcessBuilder("${project.rootDir}/gradlew", "ktlintCheck")
                .directory(project.rootDir).inheritIO()
                .start().waitFor() // venter på at prosessen skal fullføre før vi går videre til testene
        }
    }

    matching { it.name.startsWith("runKtlintCheck") }.configureEach {
        mustRunAfter(project.tasks.matching { it.name.startsWith("runKtlintFormat") })
    }

    test {
        useJUnitPlatform()

        jvmArgs("--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED")
        exclude("**/RunCucumberTest*")
        dependsOn(ktlintFormatAndCheck)

        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }
}
