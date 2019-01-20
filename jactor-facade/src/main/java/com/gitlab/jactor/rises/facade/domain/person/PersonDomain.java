package com.gitlab.jactor.rises.facade.domain.person;

import com.gitlab.jactor.rises.commons.datatype.Name;
import com.gitlab.jactor.rises.commons.dto.PersonDto;
import com.gitlab.jactor.rises.facade.domain.PersistentDomain;
import com.gitlab.jactor.rises.facade.domain.address.AddressDomain;

import java.util.Optional;

public class PersonDomain extends PersistentDomain {

    private final PersonDto personDto;

    public PersonDomain(PersonDto personDto) {
        this.personDto = personDto;
    }

    Name getSurname() {
        return Optional.ofNullable(personDto.getSurname()).map(Name::new).orElse(null);
    }

    AddressDomain getAddress() {
        return Optional.ofNullable(personDto.getAddress()).map(AddressDomain::new).orElse(null);
    }

    @Override public PersonDto getDto() {
        return personDto;
    }

    public static PersonBuilder aPerson() {
        return new PersonBuilder();
    }
}
