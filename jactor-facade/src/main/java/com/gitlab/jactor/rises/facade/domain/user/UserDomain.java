package com.gitlab.jactor.rises.facade.domain.user;

import com.gitlab.jactor.rises.commons.datatype.EmailAddress;
import com.gitlab.jactor.rises.commons.datatype.Username;
import com.gitlab.jactor.rises.commons.dto.UserDto;
import com.gitlab.jactor.rises.facade.domain.PersistentDomain;
import com.gitlab.jactor.rises.facade.domain.person.PersonDomain;

import java.util.Optional;

public class UserDomain extends PersistentDomain {

    private final UserDto userDto;

    public UserDomain(UserDto userDto) {
        this.userDto = userDto;
    }

    public Username getUsername() {
        return Optional.ofNullable(userDto.getUsername()).map(Username::new).orElse(null);
    }

    PersonDomain getPerson() {
        return Optional.ofNullable(userDto.getPerson()).map(PersonDomain::new).orElse(null);
    }

    boolean isUserNameEmailAddress() {
        Optional<EmailAddress> email = Optional.ofNullable(userDto.getEmailAddress()).map(EmailAddress::new);
        Optional<Username> username = Optional.ofNullable(userDto.getUsername()).map(Username::new);

        return email.isPresent() && username.isPresent() && email.get().isSameAs(username.get());
    }

    @Override public UserDto getDto() {
        return userDto;
    }

    public static UserBuilder aUser() {
        return new UserBuilder();
    }
}
