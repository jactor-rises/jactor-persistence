package com.gitlab.jactor.rises.web.mvc;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class CurrentUrlManager {

    private final HttpServletRequest httpServletRequest;
    private final String contextPath;

    public CurrentUrlManager(String contextPath, HttpServletRequest httpServletRequest) {
        this.contextPath = contextPath;
        this.httpServletRequest = httpServletRequest;
    }

    public String fetch() {
        String requestURI = fetchWithoutContextPath();
        String queryString = httpServletRequest.getQueryString();

        if (StringUtils.isBlank(queryString)) {
            return requestURI;
        }

        List<String> parametersWithoutLanguage = stream(queryString.split("&"))
                .filter(param -> !param.startsWith("lang="))
                .collect(Collectors.toList());

        String delimeter = parametersWithoutLanguage.isEmpty() ? "" : "?";

        return requestURI + delimeter + String.join("&", parametersWithoutLanguage);
    }

    public String fetchChosenView() {
        String requestURI = fetchWithoutContextPath();
        return requestURI.charAt(0) == '/' ? requestURI.substring(1) : requestURI;
    }

    private String fetchWithoutContextPath() {
        String requestURI = httpServletRequest.getRequestURI();
        requestURI = requestURI.replaceAll(contextPath, "");

        return requestURI;
    }
}
