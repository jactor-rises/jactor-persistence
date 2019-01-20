package com.gitlab.jactor.rises.facade.consumer;

import com.gitlab.jactor.rises.commons.dto.BlogDto;
import com.gitlab.jactor.rises.commons.dto.BlogEntryDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplateHandler;

import java.io.Serializable;

public class PersistentBlogConsumer extends AbstractRestTemplateConsumer {

    private final String endpoint;
    private final UriTemplateHandler uriTemplateHandler;

    public PersistentBlogConsumer(UriTemplateHandler uriTemplateHandler, String endpoint) {
        this.uriTemplateHandler = uriTemplateHandler;
        this.endpoint = endpoint;
    }

    public BlogDto saveOrUpdate(BlogDto blogDto) {
        RestTemplate restTemplate = RestTemplateFactory.initNew(uriTemplateHandler);
        String endpointMethod = endpoint + "/persist";

        RethrowHttpClientError<BlogDto> rethrowHttpClientError = execution ->
                restTemplate.exchange(
                        endpointMethod, HttpMethod.POST, new HttpEntity<>(blogDto), BlogDto.class
                );

        ResponseEntity<BlogDto> responseEntity = RethrowHttpClientError.tryExecution(
                endpointMethod,
                rethrowHttpClientError
        );

        return bodyOf(responseEntity);
    }

    BlogEntryDto saveOrUpdate(BlogEntryDto blogEntryDto) {
        RestTemplate restTemplate = RestTemplateFactory.initNew(uriTemplateHandler);
        String endpointMethod = endpoint + "/entry/persist";

        RethrowHttpClientError<BlogEntryDto> rethrowHttpClientError = execution ->
                restTemplate.exchange(
                        endpointMethod, HttpMethod.POST, new HttpEntity<>(blogEntryDto), BlogEntryDto.class
                );

        ResponseEntity<BlogEntryDto> responseEntity = RethrowHttpClientError.tryExecution(
                endpointMethod,
                rethrowHttpClientError
        );

        return bodyOf(responseEntity);
    }

    public BlogDto fetch(Serializable id) {
        RestTemplate restTemplate = RestTemplateFactory.initNew(uriTemplateHandler);
        String endpointMethod = endpoint + "/get/" + id;

        ResponseEntity<BlogDto> responseEntity = RethrowHttpClientError.tryExecution(
                endpointMethod,
                execution -> restTemplate.getForEntity(endpointMethod, BlogDto.class)
        );

        return bodyOf(responseEntity);
    }

    public BlogEntryDto fetchEntry(Serializable entryId) {
        RestTemplate restTemplate = RestTemplateFactory.initNew(uriTemplateHandler);
        String endpointMethod = endpoint + "/entry/get/" + entryId;

        ResponseEntity<BlogEntryDto> responseEntity = RethrowHttpClientError.tryExecution(
                endpointMethod,
                execution -> restTemplate.getForEntity(endpointMethod, BlogEntryDto.class)
        );

        return bodyOf(responseEntity);
    }
}
