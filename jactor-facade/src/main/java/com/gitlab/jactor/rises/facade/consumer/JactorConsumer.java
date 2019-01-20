package com.gitlab.jactor.rises.facade.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.util.UriTemplateHandler;

@Configuration
@PropertySource("classpath:application.properties")
public class JactorConsumer {

    @Value("${persistence.root.url}")
    private String persistenceRootUrl;

    @Bean public PersistentBlogConsumer blogRestService() {
        return new PersistentBlogConsumer(uriTemplateHandler(), "/blog");
    }

    @Bean public PersistentGuestBookConsumer guestBookRestService() {
        return new PersistentGuestBookConsumer(uriTemplateHandler(), "/guestBook");
    }

    @Bean public PersistentUserConsumer userRestService() {
        return new PersistentUserConsumer(uriTemplateHandler(), "/user");
    }

    private UriTemplateHandler uriTemplateHandler() {
        return new RootUriTemplateHandler(persistenceRootUrl);
    }
}
