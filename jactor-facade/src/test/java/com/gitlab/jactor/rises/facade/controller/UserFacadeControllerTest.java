package com.gitlab.jactor.rises.facade.controller;

import com.gitlab.jactor.rises.commons.datatype.Username;
import com.gitlab.jactor.rises.commons.dto.UserDto;
import com.gitlab.jactor.rises.facade.service.UserDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@DisplayName("The UserController")
@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserFacadeControllerTest {

    private MockMvc mockMvc;
    private @MockBean UserDomainService userDomainServiceMock;

    @BeforeEach void mockMvc() {
        mockMvc = standaloneSetup(new UserFacadeController(userDomainServiceMock)).build();
    }

    @DisplayName("should find all usernames")
    @Test void shouldFindAllUsernames() throws Exception {
        when(userDomainServiceMock.findAllUsernames()).thenReturn(asList("batman", "joker"));

        mockMvc.perform(get("/user/all/usernames").contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]", is(equalTo("batman"))))
                .andExpect(jsonPath("[1]", is(equalTo("joker"))));
    }

    @DisplayName("should not find a user by username")
    @Test void shouldNotFindByUsername() throws Exception {
        when(userDomainServiceMock.find(new Username("me"))).thenReturn(Optional.empty());

        mockMvc.perform(get("/user/find/me")).andExpect(status().isNoContent());
    }

    @DisplayName("should find a user by username")
    @Test void shouldFindByUsername() throws Exception {
        when(userDomainServiceMock.find(new Username("me"))).thenReturn(Optional.of(new UserDto()));

        mockMvc.perform(get("/user/find/me")).andExpect(status().isOk());
    }
}
