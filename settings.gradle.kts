rootProject.name = "jactor-persistence"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()
        maven {
            name = "GitHubPackages-jactor-shared"
            url = uri("https://maven.pkg.github.com/jactor-rises/jactor-shared")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: providers.gradleProperty("gpr.user").orNull
                password = System.getenv("GITHUB_TOKEN") ?: providers.gradleProperty("gpr.key").orNull
            }
        }
    }
}
