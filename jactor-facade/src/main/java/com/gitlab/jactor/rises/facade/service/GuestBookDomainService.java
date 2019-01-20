package com.gitlab.jactor.rises.facade.service;

import com.gitlab.jactor.rises.commons.dto.GuestBookDto;
import com.gitlab.jactor.rises.commons.dto.GuestBookEntryDto;
import com.gitlab.jactor.rises.facade.consumer.PersistentGuestBookConsumer;
import com.gitlab.jactor.rises.facade.domain.guestbook.GuestBookDomain;
import com.gitlab.jactor.rises.facade.domain.guestbook.GuestBookEntryDomain;

import java.io.Serializable;
import java.util.Optional;

public class GuestBookDomainService {
    private final PersistentGuestBookConsumer persistentGuestBookConsumer;

    public GuestBookDomainService(PersistentGuestBookConsumer persistentGuestBookConsumer) {
        this.persistentGuestBookConsumer = persistentGuestBookConsumer;
    }

    public GuestBookDomain saveOrUpdate(GuestBookDomain guestBookDomain) {
        return new GuestBookDomain(persistentGuestBookConsumer.saveOrUpdate(guestBookDomain.getDto()));
    }

    public GuestBookEntryDomain saveOrUpdateEntry(GuestBookEntryDomain guestBookEntryDomain) {
        return new GuestBookEntryDomain(persistentGuestBookConsumer.saveOrUpdate(guestBookEntryDomain.getDto()));
    }

    public Optional<GuestBookDomain> find(Serializable id) {
        GuestBookDto guestBookDto = persistentGuestBookConsumer.fetch(id);

        return Optional.ofNullable(guestBookDto).map(GuestBookDomain::new);
    }

    public Optional<GuestBookEntryDomain> findEntry(Serializable id) {
        GuestBookEntryDto guestBookEntryDto = persistentGuestBookConsumer.fetchEntry(id);

        return Optional.ofNullable(guestBookEntryDto).map(GuestBookEntryDomain::new);
    }
}

