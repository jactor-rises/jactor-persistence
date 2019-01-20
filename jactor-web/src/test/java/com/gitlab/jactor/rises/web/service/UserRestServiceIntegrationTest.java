package com.gitlab.jactor.rises.web.service;

import com.gitlab.jactor.rises.commons.datatype.Username;
import com.gitlab.jactor.rises.commons.dto.UserDto;
import com.gitlab.jactor.rises.test.util.SpringBootActuatorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DisplayName("The UserRestService")
@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRestServiceIntegrationTest {

    private @Autowired UserRestService userRestServiceToTest;

    @BeforeEach void assumeMicroservicesIsRunning() throws IOException {
        assumeTrue(
                SpringBootActuatorUtil.isServerRunning("http://localhost:1099/jactor-persistence-orm"),
                "jactor-persistence-orm appears not to run"
        );

        assumeTrue(
                SpringBootActuatorUtil.isServerRunning("http://localhost:1337/jactor-facade"),
                "jactor-facade appears not to run"
        );
    }

    @DisplayName("should find the default persisted users")
    @Test void shouldFindTheDefaultPersistedUsers() {
        var usernames = userRestServiceToTest.findAllUsernames();

        assertThat(usernames).contains("tip", "jactor");
    }

    @DisplayName("should find the user named jactor")
    @Test void shouldFindTheUserNamedJactor() {
        var possibleUser = userRestServiceToTest.find(new Username("jactor"));

        assertAll(
                () -> assertThat(possibleUser).as("possible user").isPresent(),
                () -> {
                    var user = possibleUser.orElse(new UserDto());

                    assertAll(
                            () -> assertThat(user.getPerson().getFirstName()).as("user.person.firstName").isEqualTo("Tor Egil"),
                            () -> assertThat(user.getEmailAddress()).as("user.emailaddress").isEqualTo("tor.egil.jacobsen@gmail.com")
                    );
                }
        );
    }
}