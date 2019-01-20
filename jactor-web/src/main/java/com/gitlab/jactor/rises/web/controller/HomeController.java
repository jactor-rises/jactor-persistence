package com.gitlab.jactor.rises.web.controller;

import com.gitlab.jactor.rises.web.dto.HomePageDto;
import com.gitlab.jactor.rises.web.i18n.MyMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import static java.util.Arrays.asList;

@Controller
public class HomeController {

    private static final String HOME_VIEW = "home";

    private final MyMessages myMessages;

    public @Autowired HomeController(MyMessages myMessages) {
        this.myMessages = myMessages;
    }

    @RequestMapping(value = {"/", HOME_VIEW}, method = RequestMethod.GET)
    public ModelAndView get() {
        return new ModelAndView(HOME_VIEW)
                .addObject("homepage", new HomePageDto(
                                asList(
                                        myMessages.fetchMessage("page.home.paragraph.a"),
                                        myMessages.fetchMessage("page.home.paragraph.b"),
                                        myMessages.fetchMessage("page.home.paragraph.c")
                                ),
                                asList(
                                        new HomePageDto.Technology("Maven", "http://maven.apache.org", myMessages.fetchMessage("page.home.tech.maven")),
                                        new HomePageDto.Technology("Spring Framework", "https://spring.io/projects/spring-framework", myMessages.fetchMessage("page.home.tech.springframework")),
                                        new HomePageDto.Technology("Spring Boot", "https://spring.io/projects/spring-boot", myMessages.fetchMessage("page.home.tech.springboot")),
                                        new HomePageDto.Technology("Thymeleaf", "https://www.thymeleaf.org", myMessages.fetchMessage("page.home.tech.thymeleaf")),
                                        new HomePageDto.Technology("Junit", "https://junit.org/junit5/", myMessages.fetchMessage("page.home.tech.junit")),
                                        new HomePageDto.Technology("Mockito", "http://site.mockito.org", myMessages.fetchMessage("page.home.tech.mockito")),
                                        new HomePageDto.Technology("AssertJ", "https://joel-costigliola.github.io/assertj/", myMessages.fetchMessage("page.home.tech.assertj")),
                                        new HomePageDto.Technology("Git", "https://git-scm.com", myMessages.fetchMessage("page.home.tech.git"))
                                )
                        )
                );
    }
}
