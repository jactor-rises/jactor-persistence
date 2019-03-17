# jactor-persistence #

### What is this repository for? ###

* Src code and issues regarding jactor-persistence

This project is a microservice dealing with persistence to an database using
jpa (java persistence api) via spring-data-jpa and is a microservice to use under
`com.github.jactor-rises` (formerly as part of the project `jactor-rises`)

### Set up ###

* a spring-boot 2 application is created when building (`mvn install`)
  * is using h2 (in-memory database)
  * run it with `mvn spring-boot:run`

### Some technologies used ###

* [spring-boot 2.1.x](https://spring.io/projects/spring-boot)
  * with spring-data-jpa
* [h2](http://h2database.com)
* [junit 5.x](https://junit.org/junit5/)
* [assertj](https://joel-costigliola.github.io/assertj/)
* [mockito](http://site.mockito.org)
* [kotlin](https://kotlinlang.org)
