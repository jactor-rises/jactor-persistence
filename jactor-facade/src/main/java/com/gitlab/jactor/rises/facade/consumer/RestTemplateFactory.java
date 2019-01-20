package com.gitlab.jactor.rises.facade.consumer;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplateHandler;

public class RestTemplateFactory {

    private static RestTemplateFactory restTemplateFactory;
    private final RestTemplateInitializer restTemplateInitializer;

    private RestTemplateFactory(RestTemplateInitializer restTemplateInitializer) {
        this.restTemplateInitializer = restTemplateInitializer;
    }

    public static RestTemplate initNew(UriTemplateHandler uriTemplateHandler) {
        RestTemplate restTemplate = restTemplateFactory.restTemplateInitializer.init();
        restTemplate.setUriTemplateHandler(uriTemplateHandler);

        return restTemplate;
    }

    protected static void use(RestTemplateInitializer restTemplateInitializer) {
        restTemplateFactory = new RestTemplateFactory(restTemplateInitializer);
    }

    private static void reset() {
        use(RestTemplate::new);
    }

    static {
        reset();
    }

    @FunctionalInterface
    public interface RestTemplateInitializer {
        RestTemplate init();
    }
}
