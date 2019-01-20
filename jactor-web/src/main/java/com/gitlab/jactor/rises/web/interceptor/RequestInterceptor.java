package com.gitlab.jactor.rises.web.interceptor;

import com.gitlab.jactor.rises.web.mvc.CurrentUrlManager;
import com.gitlab.jactor.rises.web.mvc.LanguageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@PropertySource("classpath:application.properties")
public class RequestInterceptor implements HandlerInterceptor {

    private static final String CHOSEN_VIEW = "chosenView";
    static final String CURRENT_URL = "currentUrl";

    private final String contextPath;
    private final LanguageManager languageManager;

    @Autowired public RequestInterceptor(@Value("${server.servlet.context-path}") String contextPath, LanguageManager languageManager) {
        this.contextPath = contextPath;
        this.languageManager = languageManager;
    }

    @Override public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView
    ) {
        if (modelAndView != null) {
            CurrentUrlManager currentUrlManager = new CurrentUrlManager(contextPath, request);
            modelAndView.addObject(CHOSEN_VIEW, currentUrlManager.fetchChosenView());
            modelAndView.addObject(CURRENT_URL, currentUrlManager.fetch());
            languageManager.putAllLanguageAttributes(modelAndView.getModel());
        }
    }
}
