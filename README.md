# jactor-persistence

[![build and test](https://github.com/jactor-rises/jactor-persistence/actions/workflows/build-test.yaml/badge.svg)](https://github.com/jactor-rises/jactor-persistence/actions/workflows/build-test.yaml)

[![jactor-shared](https://img.shields.io/github/v/release/jactor-rises/jactor-shared)](https://github.com/jactor-rises/jactor-shared/releases) of jactor-shared

### What is this repository for?

The main purpose is to learn about programming microservices using spring-boot and Kotlin, (with REST and jetbrains
exposed). I also use GitHub to get a deeper understanding of continuous integration/DevOps using GitHub workflow/actions
and gradle. Integration testing to be done with cucumber.

This project is a microservice dealing with persistence to a database using jetbrains exposed (formerly as part of the
archived project [jactor-rises](https://github.com/jactor-rises/jactor-rises))

### Set up

* a spring-boot 4 application
    * run it with spring-boot or gradle `.gradlew bootRun`
* this application is documented with swagger. After startup, use link
  [swagger ui](http://localhost:1099/jactor-persistence/swagger-ui/index.html?configUrl=/jactor-persistence/v3/api-docs/swagger-config#/)

### Build

This is application is build with [gradle](https://gradle.org).

From the root of the source code:

```
./gradlew build
```

### Run

How to build and run this application. Gradle and podman commands are executed from the root of the source code.

1. Set up and run the database with [podman](https://podman.io). If on mac, it can be found with
[homebrew](https://brew.sh)
   * ```brew install podman```
2. Run the database with [podman-compose](https://docs.podman.io/en/latest/markdown/podman-compose.1.html)
   * `podman-compose up -d` (-d option seperates the database in a separate process)
3. Start the applicaiton with [gradle](https://gradle.org)
   * `./gradlew bootRun`

### Some technologies used

#### Implementation

* [spring-boot 4.x](https://spring.io/projects/spring-boot)
* [jetbrains-exposed](https://www.jetbrains.com/exposed/)
* [kotlin 2.x](https://kotlinlang.org)
* [PostgreSQL](https://www.postgresql.org)

#### Unit testing

* [assertk](https://github.com/willowtreeapps/assertk)
* [junit jupiter](https://junit.org/)
* [mockk](http://mockk.io) and [springmockk](https://github.com/Ninja-Squad/springmockk)

#### Build and documentation

* [flyway](https://flywaydb.org)
* [github actions](https://docs.github.com/en/actions/learn-github-actions)
* [gradle 9.x](https://gradle.org)
* [podman](https://podman.io)
* [swagger-ui](https://swagger.io/tools/swagger-ui/)
    * herunder [springdoc](https://springdoc.org)
* [sdk man](https://sdkman.io)
