package com.gitlab.jactor.rises.web.interceptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;

import static com.gitlab.jactor.rises.web.interceptor.RequestInterceptor.CURRENT_URL;
import static com.gitlab.jactor.rises.web.mvc.LanguageManager.IS_ENGLISH;
import static com.gitlab.jactor.rises.web.mvc.LanguageManager.IS_NORWEGIAN;
import static com.gitlab.jactor.rises.web.mvc.LanguageManager.IS_THAI;
import static com.gitlab.jactor.rises.web.mvc.LanguageManager.LANG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("A RequestInterceptor")
class RequestInterceptorTest {

    private @Autowired RequestInterceptor requestInterceptorToTest;
    private @MockBean HttpServletRequest httpServletRequestMock;

    @DisplayName("should add information to the model")
    @Test void shouldAddInformationToTheModel() {
        LocaleContextHolder.setLocale(new Locale("no"));
        ModelAndView modelAndView = new ModelAndView();

        when(httpServletRequestMock.getRequestURI()).thenReturn("/somewhere");
        when(httpServletRequestMock.getQueryString()).thenReturn("out=there&lang=something&another=param");

        requestInterceptorToTest.postHandle(httpServletRequestMock, null, null, modelAndView);

        Map<String, Object> model = modelAndView.getModel();

        assertAll(
                () -> assertThat(model.get(CURRENT_URL)).as(CURRENT_URL).isEqualTo("/somewhere?out=there&another=param"),
                () -> assertThat(model.get(IS_ENGLISH)).as(IS_ENGLISH).isEqualTo(false),
                () -> assertThat(model.get(IS_NORWEGIAN)).as(IS_NORWEGIAN).isEqualTo(true),
                () -> assertThat(model.get(IS_THAI)).as(IS_THAI).isEqualTo(false),
                () -> assertThat(model.get(LANG)).isEqualTo("no")
        );
    }
}
