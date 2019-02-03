package com.gitlab.jactor.persistence.entity;

import com.gitlab.jactor.persistence.entity.address.AddressEntity;
import com.gitlab.jactor.persistence.entity.blog.BlogEntity;
import com.gitlab.jactor.persistence.entity.guestbook.GuestBookEntity;
import com.gitlab.jactor.persistence.entity.person.PersonEntity;
import com.gitlab.jactor.persistence.entity.user.UserEntity;
import com.gitlab.jactor.persistence.fields.FieldValue;
import com.gitlab.jactor.persistence.fields.RequiredFieldsExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.gitlab.jactor.persistence.entity.address.AddressEntity.anAddress;
import static com.gitlab.jactor.persistence.entity.blog.BlogEntity.aBlog;
import static com.gitlab.jactor.persistence.entity.blog.BlogEntryEntity.aBlogEntry;
import static com.gitlab.jactor.persistence.entity.guestbook.GuestBookEntity.aGuestBook;
import static com.gitlab.jactor.persistence.entity.guestbook.GuestBookEntryEntity.aGuestBookEntry;
import static com.gitlab.jactor.persistence.entity.person.PersonEntity.aPerson;
import static com.gitlab.jactor.persistence.entity.user.UserEntity.aUser;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("A PersistentEntity")
class PersistentEntityTest {

    private PersistentEntity persistentEntityToTest;

    @RegisterExtension RequiredFieldsExtension requiredFieldsExtension = new RequiredFieldsExtension(Map.of(
            BlogEntity.class, List.of(
                    new FieldValue("title", "my blog"),
                    new FieldValue("userEntity", () -> aUser().build())
            ), UserEntity.class, List.of(
                    new FieldValue("username", "me"),
                    new FieldValue("personEntity", () -> aPerson().build())
            ), PersonEntity.class, List.of(
                    new FieldValue("addressEntity", () -> anAddress().build()),
                    new FieldValue("surname", "black")
            ), AddressEntity.class, List.of(
                    new FieldValue("addressLine1", "some street #1"),
                    new FieldValue("city", "some city"),
                    new FieldValue("zipCode", 1234)
            ), GuestBookEntity.class, List.of(
                    new FieldValue("title", "my guest book"),
                    new FieldValue("user", () -> aUser().build())
            )
    ));

    @DisplayName("should be able to copy an address without the id")
    @Test void shouldCopyAddress() {
        persistentEntityToTest = anAddress()
                .withAddressLine1("somewhere")
                .withAddressLine2("out")
                .withAddressLine3("there")
                .withCity("svg")
                .withCountryCode("NO")
                .withZipCode(1001)
                .build().addSequencedId(aClass -> 1L);

        PersistentEntity copy = persistentEntityToTest.copy();

        assertAll(
                () -> assertThat(persistentEntityToTest).as("persistent entity").isNotNull(),
                () -> assertThat(persistentEntityToTest.getId()).as("id of persistent entity").isEqualTo(1L),
                () -> assertThat(copy).as("copy").isNotNull(),
                () -> assertThat(copy.getId()).as("id of copy").isNull(),
                () -> assertThat(persistentEntityToTest).as("persistent entity equals copy").isEqualTo(copy),
                () -> assertThat(persistentEntityToTest).as("persistent entity is not same instance as copy").isNotSameAs(copy)
        );
    }

    @DisplayName("should be able to copy a person without the id")
    @Test void shouldCopyPerson() {
        persistentEntityToTest = aPerson()
                .with(anAddress())
                .with(aUser())
                .withDescription("here i am")
                .withFirstName("Bill")
                .withSurname("Smith")
                .withLocale("us_US")
                .build().addSequencedId(aClass -> 1L);

        PersistentEntity copy = persistentEntityToTest.copy();

        assertAll(
                () -> assertThat(persistentEntityToTest).as("persistent entity").isNotNull(),
                () -> assertThat(persistentEntityToTest.getId()).as("id of persistent entity").isEqualTo(1L),
                () -> assertThat(copy).as("copy").isNotNull(),
                () -> assertThat(copy.getId()).as("id of copy").isNull(),
                () -> assertThat(persistentEntityToTest).as("persistent entity equals copy").isEqualTo(copy),
                () -> assertThat(persistentEntityToTest).as("persistent entity is not same instance as copy").isNotSameAs(copy)
        );
    }

    @DisplayName("should be able to copy a user without the id")
    @Test void shouldCopyUser() {
        persistentEntityToTest = aUser()
                .with(aPerson())
                .withEmailAddress("i.am@home")
                .withUsername("jactor")
                .build().addSequencedId(aClass -> 1L);

        PersistentEntity copy = persistentEntityToTest.copy();

        assertAll(
                () -> assertThat(persistentEntityToTest).as("persistent entity").isNotNull(),
                () -> assertThat(persistentEntityToTest.getId()).as("id of persistent entity").isEqualTo(1L),
                () -> assertThat(copy).as("copy").isNotNull(),
                () -> assertThat(copy.getId()).as("id of copy").isNull(),
                () -> assertThat(persistentEntityToTest).as("persistent entity equals copy").isEqualTo(copy),
                () -> assertThat(persistentEntityToTest).as("persistent entity is not same instance as copy").isNotSameAs(copy)
        );
    }

    @DisplayName("should be able to copy a blog without the id")
    @Test void shouldCopyBlog() {
        persistentEntityToTest = aBlog()
                .with(aUser())
                .withTitle("general ignorance")
                .build().addSequencedId(aClass -> 1L);

        PersistentEntity copy = persistentEntityToTest.copy();

        assertAll(
                () -> assertThat(persistentEntityToTest).as("persistent entity").isNotNull(),
                () -> assertThat(persistentEntityToTest.getId()).as("id of persistent entity").isEqualTo(1L),
                () -> assertThat(copy).as("copy").isNotNull(),
                () -> assertThat(copy.getId()).as("id of copy").isNull(),
                () -> assertThat(persistentEntityToTest).as("persistent entity equals copy").isEqualTo(copy),
                () -> assertThat(persistentEntityToTest).as("persistent entity is not same instance as copy").isNotSameAs(copy)
        );
    }

    @DisplayName("should be able to copy a blog entry without the id")
    @Test void shouldCopyBlogEntry() {
        persistentEntityToTest = aBlogEntry()
                .with(aBlog())
                .withCreatorName("jactor")
                .withEntry("the one")
                .build().addSequencedId(aClass -> 1L);

        PersistentEntity copy = persistentEntityToTest.copy();

        assertAll(
                () -> assertThat(persistentEntityToTest).as("persistent entity").isNotNull(),
                () -> assertThat(persistentEntityToTest.getId()).as("id of persistent entity").isEqualTo(1L),
                () -> assertThat(copy).as("copy").isNotNull(),
                () -> assertThat(copy.getId()).as("id of copy").isNull(),
                () -> assertThat(persistentEntityToTest).as("persistent entity equals copy").isEqualTo(copy),
                () -> assertThat(persistentEntityToTest).as("persistent entity is not same instance as copy").isNotSameAs(copy)
        );
    }

    @DisplayName("should be able to copy a guest book without the id")
    @Test void shouldCopyGuestBook() {
        persistentEntityToTest = aGuestBook()
                .with(aUser())
                .withTitle("enter when applied")
                .build().addSequencedId(aClass -> 1L);

        PersistentEntity copy = persistentEntityToTest.copy();

        assertAll(
                () -> assertThat(persistentEntityToTest).as("persistent entity").isNotNull(),
                () -> assertThat(persistentEntityToTest.getId()).as("id of persistent entity").isEqualTo(1L),
                () -> assertThat(copy).as("copy").isNotNull(),
                () -> assertThat(copy.getId()).as("id of copy").isNull(),
                () -> assertThat(persistentEntityToTest).as("persistent entity equals copy").isEqualTo(copy),
                () -> assertThat(persistentEntityToTest).as("persistent entity is not same instance as copy").isNotSameAs(copy)
        );
    }

    @DisplayName("should be able to copy a guest book entry without the id")
    @Test void shouldCopyGuestBookEntry() {
        persistentEntityToTest = aGuestBookEntry()
                .with(aGuestBook())
                .withCreatorName("jactor")
                .withEntry("the one")
                .build().addSequencedId(aClass -> 1L);

        PersistentEntity copy = persistentEntityToTest.copy();

        assertAll(
                () -> assertThat(persistentEntityToTest).as("persistent entity").isNotNull(),
                () -> assertThat(persistentEntityToTest.getId()).as("id of persistent entity").isEqualTo(1L),
                () -> assertThat(copy).as("copy").isNotNull(),
                () -> assertThat(copy.getId()).as("id of copy").isNull(),
                () -> assertThat(persistentEntityToTest).as("persistent entity equals copy").isEqualTo(copy),
                () -> assertThat(persistentEntityToTest).as("persistent entity is not same instance as copy").isNotSameAs(copy)
        );
    }

    @DisplayName("should return an empty stream when no dependencies")
    @Test void shouldReturnEmptyStreamWithoutDependenciesGiven() {
        persistentEntityToTest = aPerson().build();
        Stream none = persistentEntityToTest.streamSequencedDependencies();

        assertThat(none).isEmpty();
    }

    @DisplayName("should stream optional dependencies of an persistent entity")
    @Test void shouldStreamOptionalDependencies() {
        BlogEntity blogEntity = aBlog().build();
        PersonEntity personEntity = aPerson().build();

        persistentEntityToTest = personEntity;
        List<PersistentEntity> dependencies = persistentEntityToTest.streamSequencedDependencies(blogEntity, personEntity, null, null)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());

        assertThat(dependencies)
                .hasSize(2)
                .contains(blogEntity, personEntity);
    }

    @DisplayName("should fetch all dependencies of a persistent entity")
    @Test void shouldFetchAllDependencies() {
        AddressEntity addressEntity = anAddress().build();
        PersonEntity personEntity = aPerson().with(addressEntity).build();
        persistentEntityToTest = aUser().with(personEntity).build();

        List<PersistentEntity> allSequencedDependencies = persistentEntityToTest.fetchAllSequencedDependencies();

        assertThat(allSequencedDependencies)
                .hasSize(2)
                .contains(addressEntity, personEntity);
    }

    @DisplayName("should add sequenced id, also on dependencies of a persistent entity")
    @Test void shouldAddSequencedIdAlsOnDependencies() {
        AddressEntity addressEntity = anAddress().build();
        PersonEntity personEntity = aPerson().with(addressEntity).build();
        persistentEntityToTest = aUser().with(personEntity).build();

        PersistentEntity.Sequencer sequencerMock = mock(PersistentEntity.Sequencer.class);
        when(sequencerMock.nextVal(any(Class.class))).thenReturn(123L);

        persistentEntityToTest.addSequencedId(sequencerMock);

        assertAll(
                () -> assertThat(persistentEntityToTest.getId()).as("persistentEntityToTest.id").isEqualTo(123L),
                () -> assertThat(addressEntity.getId()).as("addressEntity.id").isEqualTo(123L),
                () -> assertThat(personEntity.getId()).as("personEntity.id").isEqualTo(123L)
        );
    }
}
