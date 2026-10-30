import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.versions)
}

version = "0.0.0-SNAPSHOT"

val jactorSharedDependency = GithubPackageDependency(
    project = "jactor-shared",
    versionNameFromToml = "jactor-shared-lib",
)

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
    implementation(files(jactorSharedDependency.jarFile))

    // runtime dependencies
    runtimeOnly(libs.flywayPostgres)
    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.spring.boot.flyway)

    // test implementations
    testImplementation(libs.assertk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.springmockk)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webflux.test)
    testImplementation(libs.zonky)
    testImplementation(libs.zonky.postgres)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.jvm.get().toInt())
    }

    withSourcesJar()
}

tasks.register("printVersion") {
    description = "Prints the version of jactor-persistence"
    doLast { println(project.version) }
}

tasks {
    val ktlintFormatAndCheck = register("ktlintFormatAndCheck") {
        description = "Run ktlintFormat and ktlintCheck before testing (and commit)"
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

    register("downloadJactorSharedJar") {
        inputs.property("jactorSharedDownloadUrl", jactorSharedDependency.downloadUrl)
        outputs.file(jactorSharedDependency.jarFile)

        doLast {
            val target = jactorSharedDependency.jarFile.get().asFile
            target.parentFile.mkdirs()

            jactorSharedDependency.downloadUriAsUrl().openStream().use { input ->
                Files.copy(input, target.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

    named("compileKotlin") {
        dependsOn("downloadJactorSharedJar")
    }

    named("test") {
        dependsOn("downloadJactorSharedJar")
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

data class GithubPackageDependency(
    private val owner: String = "jactor-rises",
    private val project: String,
    private val versionNameFromToml: String,
    private val version: String = Regex("""(?m)^\s*$versionNameFromToml\s*=\s*"([^"]+)"""")
        .find(file("gradle/libs.versions.toml").readText())
        ?.groupValues?.get(1)
        ?: error("Could not determine jactor-shared version from gradle/libs.versions.toml"),
    private val artifactVersion: String = version.removePrefix("v"),
    private val jarName: String = "jactor-shared-$artifactVersion.jar",
) {
    val jarFile: Provider<RegularFile> = layout.buildDirectory.file("downloaded-dependencies/$jarName")
    val downloadUrl: String = "https://github.com/$owner/$project/releases/download/$version/$jarName"

    fun downloadUriAsUrl(): URL = URI(downloadUrl).toURL()
}
