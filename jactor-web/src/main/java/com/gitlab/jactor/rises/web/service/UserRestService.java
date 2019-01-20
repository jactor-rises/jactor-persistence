package com.gitlab.jactor.rises.web.service;

import com.gitlab.jactor.rises.commons.datatype.Username;
import com.gitlab.jactor.rises.commons.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplateHandler;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UserRestService {
    private final String endpoint;
    private final UriTemplateHandler uriTemplateHandler;

    public UserRestService(UriTemplateHandler uriTemplateHandler, String endpoint) {
        this.uriTemplateHandler = uriTemplateHandler;
        this.endpoint = endpoint;
    }

    public Optional<UserDto> find(Username username) {
        RestTemplate restTemplate = RestTemplateFactory.initNew(uriTemplateHandler);
        ResponseEntity<UserDto> responseEntity = restTemplate.getForEntity(
                endpointMethod("/find/" + username.asString()), UserDto.class
        );

        return Optional.ofNullable(bodyOf(responseEntity));
    }

    public List<String> findAllUsernames() {
        RestTemplate restTemplate = RestTemplateFactory.initNew(uriTemplateHandler);
        ResponseEntity<String[]> responseEntity = restTemplate.getForEntity(
                endpointMethod("/all/usernames"), String[].class
        );

        return List.of(Objects.requireNonNull(responseEntity.getBody()));
    }

    private String endpointMethod(String url) {
        return endpoint + url;
    }

    private UserDto bodyOf(ResponseEntity<UserDto> responseEntity) {
        if (responseEntity == null) {
            throw new IllegalStateException("No response from REST service");
        }

        if (isNot2xxSuccessful(responseEntity.getStatusCode())) {
            String badConfiguredResponseMesssage = String.format("Bad configuration of REST service! ResponseCode: %s(%d)",
                    responseEntity.getStatusCode().name(),
                    responseEntity.getStatusCodeValue()
            );

            throw new IllegalStateException(badConfiguredResponseMesssage);
        }

        return responseEntity.getBody();
    }

    private boolean isNot2xxSuccessful(HttpStatus statusCode) {
        return !statusCode.is2xxSuccessful();
    }
}
