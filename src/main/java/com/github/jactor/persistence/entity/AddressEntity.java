package com.github.jactor.persistence.entity;

import static java.util.Objects.hash;

import com.github.jactor.persistence.dto.AddressInternalDto;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "T_ADDRESS")
public class AddressEntity implements PersistentEntity<AddressEntity> {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "addressSeq")
  @SequenceGenerator(name = "addressSeq", sequenceName = "T_ADDRESS_SEQ", allocationSize = 1)
  private Long id;

  @Embedded
  @AttributeOverride(name = "createdBy", column = @Column(name = "CREATED_BY"))
  @AttributeOverride(name = "timeOfCreation", column = @Column(name = "CREATION_TIME"))
  @AttributeOverride(name = "modifiedBy", column = @Column(name = "UPDATED_BY"))
  @AttributeOverride(name = "timeOfModification", column = @Column(name = "UPDATED_TIME"))
  private PersistentDataEmbeddable persistentDataEmbeddable;

  @Column(name = "ADDRESS_LINE_1", nullable = false)
  private String addressLine1;
  @Column(name = "ADDRESS_LINE_2")
  private String addressLine2;
  @Column(name = "ADDRESS_LINE_3")
  private String addressLine3;
  @Column(name = "CITY", nullable = false)
  private String city;
  @Column(name = "COUNTRY")
  private String country;
  @Column(name = "ZIP_CODE", nullable = false)
  private String zipCode;

  @SuppressWarnings("unused")
  AddressEntity() {
    // used by entity manager
  }

  /**
   * @param address to copyWithoutId
   */
  private AddressEntity(AddressEntity address) {
    persistentDataEmbeddable = new PersistentDataEmbeddable();
    addressLine1 = address.getAddressLine1();
    addressLine2 = address.getAddressLine2();
    addressLine3 = address.getAddressLine3();
    city = address.getCity();
    country = address.getCountry();
    id = address.getId();
    zipCode = address.getZipCode();
  }

  AddressEntity(AddressInternalDto addressInternalDto) {
    persistentDataEmbeddable = new PersistentDataEmbeddable(addressInternalDto.fetchPersistentDto());
    addressLine1 = addressInternalDto.getAddressLine1();
    addressLine2 = addressInternalDto.getAddressLine2();
    addressLine3 = addressInternalDto.getAddressLine3();
    city = addressInternalDto.getCity();
    country = addressInternalDto.getCountry();
    id = addressInternalDto.getId();
    zipCode = addressInternalDto.getZipCode();
  }

  public AddressInternalDto asDto() {
    return new AddressInternalDto(persistentDataEmbeddable.asPersistentDto(id), zipCode, addressLine1, addressLine2, addressLine3, city, country);
  }

  @Override
  public AddressEntity copyWithoutId() {
    AddressEntity addressEntity = new AddressEntity(this);
    addressEntity.setId(null);

    return addressEntity;
  }

  @Override
  public void modify() {
    persistentDataEmbeddable.modify();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AddressEntity addressEntity = (AddressEntity) o;

    return this == o || Objects.equals(addressLine1, addressEntity.addressLine1) &&
        Objects.equals(addressLine2, addressEntity.addressLine2) &&
        Objects.equals(addressLine3, addressEntity.addressLine3) &&
        Objects.equals(city, addressEntity.city) &&
        Objects.equals(country, addressEntity.country) &&
        Objects.equals(zipCode, addressEntity.zipCode);
  }

  @Override
  public int hashCode() {
    return hash(addressLine1, addressLine2, addressLine3, city, country, zipCode);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
        .appendSuper(super.toString())
        .append(getAddressLine1())
        .append(getAddressLine2())
        .append(getAddressLine3())
        .append(getZipCode())
        .append(getCity())
        .append(getCountry())
        .toString();
  }

  @Override
  public String getCreatedBy() {
    return persistentDataEmbeddable.getCreatedBy();
  }

  @Override
  public LocalDateTime getTimeOfCreation() {
    return persistentDataEmbeddable.getTimeOfCreation();
  }

  @Override
  public String getModifiedBy() {
    return persistentDataEmbeddable.getModifiedBy();
  }

  @Override
  public LocalDateTime getTimeOfModification() {
    return persistentDataEmbeddable.getTimeOfModification();
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public String getZipCode() {
    return zipCode;
  }

  public String getAddressLine1() {
    return addressLine1;
  }

  public String getAddressLine2() {
    return addressLine2;
  }

  public String getAddressLine3() {
    return addressLine3;
  }

  public String getCity() {
    return city;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  public void setAddressLine1(String addressLine1) {
    this.addressLine1 = addressLine1;
  }

  public void setAddressLine2(String addressLine2) {
    this.addressLine2 = addressLine2;
  }

  public void setAddressLine3(String addressLine3) {
    this.addressLine3 = addressLine3;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public static AddressEntity anAddress(AddressInternalDto addressInternalDto) {
    return new AddressEntity(addressInternalDto);
  }
}
