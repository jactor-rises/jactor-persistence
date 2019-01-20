package com.gitlab.jactor.rises.web.service;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplateHandler;

class RestTemplateFactory {

    private static RestTemplateFactory instance;

    private final InitRestTemplate initRestTemplate;

    private RestTemplateFactory(InitRestTemplate initRestTemplate) {
        this.initRestTemplate = initRestTemplate;
    }

    static RestTemplate initNew(UriTemplateHandler uriTemplateHandler) {
        RestTemplate restTemplate = instance.initRestTemplate.init();
        restTemplate.setUriTemplateHandler(uriTemplateHandler);

        return restTemplate;
    }

    static void use(InitRestTemplate initRestTemplate) {
        instance = new RestTemplateFactory(initRestTemplate);
    }

    static void reset() {
        use(RestTemplate::new);
    }

    static {
        use(RestTemplate::new);
    }

    @FunctionalInterface
    interface InitRestTemplate {
        RestTemplate init();
    }
}
