# README #

### What is this repository for? ###

* Src code and issues regarding jactor-rises

This project is created as microsevices and not as a monolithic client/server applications and are loosely described on this page using [GitLab Flavoured Markdown](https://gitlab.com/help/user/markdown)

The only hard dependencies between jactor modules are:

```mermaid
graph TD;
    jactor.rises.commons-->jactor.rises.facade;
    jactor.rises.commons-->jactor.rises.model;
    jactor.rises.commons-->jactor.rises.persistence.orm;
    jactor.rises.commons-->jactor.rises.web;
    jactor.rises.model->jactor.rises.facade;
```

### Set up ###

* spring-boot applications are created when building (`mvn install`)
    * jactor-persistence-orm which is a standalone rest application which handles the persistence in an object relational model
    * jactor-facade is a rest applications which handles io from any ui wanting to use jactor-model
    * jactor-web which is a web application on apache tomcat
* these applications can run side by side to get a full working application (using `mvn spring-boot:run` on each application)
* persistence-orm is using h2 (in-memory database), and is not finite
* after started jactor-web, point a browser to http://localhost:8080/jactor-web/

### Disclaimer ###

These applications are not 100% finished, but created for my own learning. That is to say:

* `Blog`s are only persisted in `jactor-persistence-orm` and only have an integration test of the rest api that verifies actual interactions.
* `GuestBook`s have interactions from `jactor-model` and are tested with an integration test, but lack interactions from `jactor-web`.
* `User` (with `Person` and `Address`) is fully integrated with communications from `jactor-web`, which use `jactor-model` and `jactor-persistence-orm` (through `jactor-facade`).
* Persistence in `jactor-persistence-orm` is not satisfactory. There is therefore [an issue](https://github.com/jactor-rises/jactor-rises/issues/1) (help wanted) created for solving a potential "buggy" solution.

### Some technologies used on jactor-rises ###

* [spring framwork 5.x](https://spring.io/projects/spring-framework)
* [spring-boot 2.x](https://spring.io/projects/spring-boot)
* [hibernate 5.x](http://hibernate.org/orm/)
* [h2](http://h2database.com)
* [junit 5.x](https://junit.org/junit5/)
* [assertj](https://joel-costigliola.github.io/assertj/)
* [mockito](http://site.mockito.org)
* [thymeleaf](https://www.thymeleaf.org)
* [git](https://git-scm.com)
