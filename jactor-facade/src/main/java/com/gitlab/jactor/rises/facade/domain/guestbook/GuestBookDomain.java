package com.gitlab.jactor.rises.facade.domain.guestbook;

import com.gitlab.jactor.rises.commons.dto.GuestBookDto;
import com.gitlab.jactor.rises.facade.domain.PersistentDomain;
import com.gitlab.jactor.rises.facade.domain.user.UserDomain;

import java.util.Optional;

public class GuestBookDomain extends PersistentDomain {

    private final GuestBookDto guestBookDto;

    public GuestBookDomain(GuestBookDto guestBookDto) {
        this.guestBookDto = guestBookDto;
    }

    public String getTitle() {
        return guestBookDto.getTitle();
    }

    public UserDomain getUser() {
        return Optional.ofNullable(guestBookDto.getUser()).map(UserDomain::new).orElse(null);
    }

    @Override public GuestBookDto getDto() {
        return guestBookDto;
    }

    public static GuestBookBuilder aGuestBook() {
        return new GuestBookBuilder();
    }
}
