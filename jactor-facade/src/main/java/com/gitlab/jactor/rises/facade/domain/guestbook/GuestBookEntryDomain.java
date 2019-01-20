package com.gitlab.jactor.rises.facade.domain.guestbook;

import com.gitlab.jactor.rises.commons.datatype.Name;
import com.gitlab.jactor.rises.commons.dto.GuestBookEntryDto;
import com.gitlab.jactor.rises.facade.domain.PersistentDomain;

import java.time.LocalDateTime;
import java.util.Optional;

public class GuestBookEntryDomain extends PersistentDomain {

    private final GuestBookEntryDto guestBookEntryDto;

    public GuestBookEntryDomain(GuestBookEntryDto guestBookEntryDto) {
        this.guestBookEntryDto = guestBookEntryDto;
    }

    public GuestBookDomain getGuestBook() {
        return Optional.ofNullable(guestBookEntryDto.getGuestBook()).map(GuestBookDomain::new).orElse(null);
    }

    public LocalDateTime getCreatedTime() {
        return guestBookEntryDto.getCreationTime();
    }

    public String getEntry() {
        return guestBookEntryDto.getEntry();
    }

    public Name getCreatorName() {
        return Optional.ofNullable(guestBookEntryDto.getCreatorName()).map(Name::new).orElse(null);
    }

    @Override public GuestBookEntryDto getDto() {
        return guestBookEntryDto;
    }

    public static GuestBookEntryBuilder aGuestBookEntry() {
        return new GuestBookEntryBuilder();
    }
}
