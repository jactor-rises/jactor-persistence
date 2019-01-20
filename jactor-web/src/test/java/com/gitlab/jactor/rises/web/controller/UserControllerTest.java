package com.gitlab.jactor.rises.web.controller;

import com.gitlab.jactor.rises.commons.datatype.Name;
import com.gitlab.jactor.rises.commons.datatype.Username;
import com.gitlab.jactor.rises.commons.dto.UserDto;
import com.gitlab.jactor.rises.web.JactorWeb;
import com.gitlab.jactor.rises.web.menu.MenuFacade;
import com.gitlab.jactor.rises.web.menu.MenuItem;
import com.gitlab.jactor.rises.web.service.UserRestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.gitlab.jactor.rises.web.menu.MenuItem.aMenuItem;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {JactorWeb.class})
@DisplayName("The UserController")
@PropertySource("classpath:application.properties")
class UserControllerTest {

    private static final String REQUEST_USER = "choose";
    private static final String USER_ENDPOINT = "/user";
    private static final String USER_JACTOR = "jactor";

    private MockMvc mockMvc;
    private @MockBean UserRestService userRestServiceMock;
    private @MockBean MenuFacade menuFacadeMock;
    private @Value("jactor-web.menu.users") String menuNameUsers;
    private @Value("${spring.mvc.view.prefix}") String prefix;
    private @Value("${spring.mvc.view.suffix}") String suffix;

    @BeforeEach void mockMvcWithViewResolver() {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setPrefix(prefix);
        internalResourceViewResolver.setSuffix(suffix);

        mockMvc = standaloneSetup(new UserController(userRestServiceMock, menuFacadeMock, menuNameUsers))
                .setViewResolvers(internalResourceViewResolver)
                .build();
    }

    @DisplayName("should not fetch user by username if the username is missing from the request")
    @Test void shouldNotFetchUserByUsernameIfTheUsernameIsMissongFromTheRequest() throws Exception {
        mockMvc.perform(get(USER_ENDPOINT)).andExpect(status().isOk());

        verify(userRestServiceMock, never()).find(any(Username.class));
    }

    @DisplayName("should not fetch user by username when the username is requested, but is only whitespace")
    @Test void shouldNotFetchUserByUsernameIfTheUsernameInTheRequestIsNullOrAnEmptyString() throws Exception {
        mockMvc.perform(
                get(USER_ENDPOINT).requestAttr(REQUEST_USER, " \n \t")
        ).andExpect(status().isOk());

        verify(userRestServiceMock, never()).find(any(Username.class));
    }

    @DisplayName("should fetch user by username when the username is requested")
    @Test void shouldFetchTheUserIfChooseParameterExist() throws Exception {
        when(userRestServiceMock.find(new Username(USER_JACTOR))).thenReturn(Optional.of(new UserDto()));

        ModelAndView modelAndView = mockMvc.perform(
                get(USER_ENDPOINT).param(REQUEST_USER, USER_JACTOR)
        ).andExpect(status().isOk()).andReturn().getModelAndView();

        var model = modelAndView != null ? modelAndView.getModel() : new HashMap<>();
        assertThat(model.get("user")).isNotNull();
    }

    @DisplayName("should fetch user by username, but not find user")
    @Test void shouldFetchTheUserByUsernameButNotFindUser() throws Exception {
        when(userRestServiceMock.find(any(Username.class))).thenReturn(Optional.empty());

        ModelAndView modelAndView = mockMvc.perform(
                get(USER_ENDPOINT).param(REQUEST_USER, "someone")
        ).andExpect(status().isOk()).andReturn().getModelAndView();

        assertThat(Objects.requireNonNull(modelAndView).getModel().get("unknownUser")).isEqualTo("someone");
    }

    @DisplayName("should add the users menu to the model")
    @Test void shouldAddUserMenuToTheModel() throws Exception {
        when(menuFacadeMock.fetchMenuItems(Name.of(menuNameUsers))).thenReturn(singletonList(aMenuItem().build()));

        Map<String, Object> model = Objects.requireNonNull(
                mockMvc.perform(
                        get(USER_ENDPOINT).param(REQUEST_USER, USER_JACTOR)
                ).andExpect(status().isOk()).andReturn().getModelAndView()
        ).getModel();

        //noinspection unchecked
        assertThat((Collection<MenuItem>) model.get("usersMenu")).isNotEmpty();
    }
}
