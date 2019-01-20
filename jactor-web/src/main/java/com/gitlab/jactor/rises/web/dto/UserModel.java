package com.gitlab.jactor.rises.web.dto;


import com.gitlab.jactor.rises.commons.dto.AddressDto;
import com.gitlab.jactor.rises.commons.dto.PersonDto;
import com.gitlab.jactor.rises.commons.dto.UserDto;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserModel {
    private final AddressDto address;
    private final PersonDto person;
    private final UserDto user;
    private String fullDescription;

    public UserModel(UserDto user) {
        this.user = user;
        person = Optional.ofNullable(user).map(UserDto::getPerson).orElse(null);
        address = Optional.ofNullable(person).map(PersonDto::getAddress).orElse(null);
    }

    public List<String> fetchAddress() {
        return Stream.of(fetchAddressLine1(), fetchAddressLine2(), fetchAddressLine3(), fetchZipCode(), fetchCity())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public String fetchFullName() {
        String surname = fetchSurname();

        return fetchFirstname()
                .map(s -> s + " " + surname)
                .orElse(surname);
    }

    private String fetchAddressLine1() {
        return Optional.ofNullable(address)
                .map(AddressDto::getAddressLine1).orElse(null);
    }

    private String fetchAddressLine2() {
        return Optional.ofNullable(address)
                .map(AddressDto::getAddressLine2).orElse(null);
    }

    private String fetchAddressLine3() {
        return Optional.ofNullable(address)
                .map(AddressDto::getAddressLine3).orElse(null);
    }

    private String fetchCity() {
        return Optional.ofNullable(address)
                .map(AddressDto::getCity).orElse(null);
    }

    private String fetchZipCode() {
        return String.valueOf(
                Optional.ofNullable(address)
                        .map(AddressDto::getZipCode)
                        .orElse(null)
        );
    }

    private Optional<String> fetchFirstname() {
        return Optional.ofNullable(person)
                .map(PersonDto::getFirstName);
    }

    private String fetchSurname() {
        return Optional.ofNullable(person)
                .map(PersonDto::getSurname).orElse("");
    }

    public String fetchUsername() {
        return user.getUsername();
    }

    public Optional<String> fetchDescriptionCode() {
        return Optional.ofNullable(person)
                .map(PersonDto::getDescription);
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }
}
