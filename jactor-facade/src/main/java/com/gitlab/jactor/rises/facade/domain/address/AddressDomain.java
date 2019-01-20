package com.gitlab.jactor.rises.facade.domain.address;

import com.gitlab.jactor.rises.commons.datatype.Country;
import com.gitlab.jactor.rises.commons.dto.AddressDto;
import com.gitlab.jactor.rises.facade.domain.PersistentDomain;

import java.util.Optional;

public class AddressDomain extends PersistentDomain {

    private final AddressDto addressDto;

    public AddressDomain(AddressDto addressDto) {
        this.addressDto = addressDto;
    }

    String getAddressLine1() {
        return addressDto.getAddressLine1();
    }

    String getAddressLine2() {
        return addressDto.getAddressLine2();
    }

    String getAddressLine3() {
        return addressDto.getAddressLine3();
    }

    String getCity() {
        return addressDto.getCity();
    }

    Country getCountry() {
        return Optional.ofNullable(addressDto.getCountry()).map(Country::new).orElse(null);
    }

    Integer getZipCode() {
        return addressDto.getZipCode();
    }

    public AddressDto getDto() {
        return addressDto;
    }

    public static AddressBuilder anAddress() {
        return new AddressBuilder();
    }
}
