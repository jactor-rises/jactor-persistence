package com.gitlab.jactor.rises.web.mvc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("A CurrentUrlManager")
@PropertySource("classpath:application.properties")
class CurrentUrlManagerTest {

    private CurrentUrlManager currentUrlManager;
    private @MockBean HttpServletRequest httpServletRequestMock;
    private @Value("${server.servlet.context-path}") String contextPath;

    @BeforeEach void initCurrentUrlManager() {
        currentUrlManager = new CurrentUrlManager(contextPath, httpServletRequestMock);
    }

    @DisplayName("should fetch currentUrl and attach it to the model")
    @Test void shouldInitCurrentUrl() {
        when(httpServletRequestMock.getRequestURI()).thenReturn("/user");
        when(httpServletRequestMock.getQueryString()).thenReturn("choose=jactor");

        assertThat(currentUrlManager.fetch()).isEqualTo("/user?choose=jactor");
    }

    @DisplayName("should not add query string to currentUrl if query string is blank")
    @Test void shouldNotAddQueryStringToCurrentUrl() {
        when(httpServletRequestMock.getRequestURI()).thenReturn("/user");
        when(httpServletRequestMock.getQueryString()).thenReturn("");

        assertThat(currentUrlManager.fetch()).isEqualTo("/user");
    }

    @DisplayName("should not add parameter called lang")
    @Test void shouldNotAddLangParameter() {
        when(httpServletRequestMock.getRequestURI()).thenReturn("/home");
        when(httpServletRequestMock.getQueryString()).thenReturn("lang=en");

        String languageParam = currentUrlManager.fetch();

        when(httpServletRequestMock.getRequestURI()).thenReturn("/user");
        when(httpServletRequestMock.getQueryString()).thenReturn("lang=no&choose=tip");

        String langAndOtherParam = currentUrlManager.fetch();

        assertAll(
                () -> assertThat(languageParam).as("only language param").isEqualTo("/home"),
                () -> assertThat(langAndOtherParam).as("one language and one user params").isEqualTo("/user?choose=tip")
        );
    }

    @DisplayName("should not add context-path to current url")
    @Test void shouldNotAddContextPath() {
        when(httpServletRequestMock.getRequestURI()).thenReturn(contextPath + "/home");

        assertThat(currentUrlManager.fetch()).isEqualTo("/home");
    }

    @DisplayName("should not add centext-path to the view name")
    @Test void shouldNotAddViewNameToTheModel() {
        when(httpServletRequestMock.getRequestURI()).thenReturn(contextPath + "/someView");

        assertThat(currentUrlManager.fetchChosenView()).isEqualTo("someView");
    }
}
