package com.github.jactor.persistence.service;

import static com.github.jactor.persistence.entity.address.AddressEntity.anAddress;
import static com.github.jactor.persistence.entity.guestbook.GuestBookEntity.aGuestBook;
import static com.github.jactor.persistence.entity.guestbook.GuestBookEntryEntity.aGuestBookEntry;
import static com.github.jactor.persistence.entity.person.PersonEntity.aPerson;
import static com.github.jactor.persistence.entity.user.UserEntity.aUser;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.jactor.persistence.dto.GuestBookDto;
import com.github.jactor.persistence.dto.GuestBookEntryDto;
import com.github.jactor.persistence.dto.UserDto;
import com.github.jactor.persistence.entity.address.AddressEntity;
import com.github.jactor.persistence.entity.guestbook.GuestBookEntity;
import com.github.jactor.persistence.entity.guestbook.GuestBookEntryEntity;
import com.github.jactor.persistence.entity.person.PersonEntity;
import com.github.jactor.persistence.entity.user.UserEntity;
import com.github.jactor.persistence.fields.FieldValue;
import com.github.jactor.persistence.fields.RequiredFieldsExtension;
import com.github.jactor.persistence.repository.GuestBookEntryRepository;
import com.github.jactor.persistence.repository.GuestBookRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("A GuestBookService")
class GuestBookServiceTest {

  @RegisterExtension
  RequiredFieldsExtension requiredFieldsExtension = new RequiredFieldsExtension(Map.of(
      GuestBookEntryEntity.class, singletonList(
          new FieldValue("guestBook", () -> aGuestBook().build())
      ), GuestBookEntity.class, List.of(
          new FieldValue("title", "my book"),
          new FieldValue("user", () -> aUser().build())
      ), UserEntity.class, List.of(
          new FieldValue("username", () -> "unique@" + LocalDateTime.now()),
          new FieldValue("personEntity", () -> aPerson().build())
      ), PersonEntity.class, List.of(
          new FieldValue("addressEntity", () -> anAddress().build()),
          new FieldValue("surname", "sure, man")
      ), AddressEntity.class, List.of(
          new FieldValue("addressLine1", "Test Boulevard 1"),
          new FieldValue("zipCode", 1001),
          new FieldValue("city", "Testing")
      )
  ));

  @InjectMocks
  private GuestBookService guestBookServiceToTest;
  @Mock
  private GuestBookRepository guestBookRepositoryMock;
  @Mock
  private GuestBookEntryRepository guestBookEntryRepositoryMock;

  @BeforeEach
  void initMocking() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  @DisplayName("should map guest book to a dto")
  void shouldMapBlogToDto() {
    Optional<GuestBookEntity> guestBookEntity = Optional.of(aGuestBook().withTitle("@home").build());
    when(guestBookRepositoryMock.findById(1001L)).thenReturn(guestBookEntity);

    GuestBookDto guestBookDto = guestBookServiceToTest.find(1001L).orElseThrow(mockError());

    assertThat(guestBookDto.getTitle()).as("title").isEqualTo("@home");
  }

  @Test
  @DisplayName("should map guest book entry to a dto")
  void shouldMapFoundBlogToDto() {
    Optional<GuestBookEntryEntity> anEntry = Optional.of(aGuestBookEntry().withCreatorName("me").withEntry("too").build());
    when(guestBookEntryRepositoryMock.findById(1001L)).thenReturn(anEntry);

    GuestBookEntryDto guestBookEntryDto = guestBookServiceToTest.findEntry(1001L).orElseThrow(mockError());

    assertAll(
        () -> assertThat(guestBookEntryDto.getCreatorName()).as("creator name").isEqualTo("me"),
        () -> assertThat(guestBookEntryDto.getEntry()).as("entry").isEqualTo("too")
    );
  }

  private Supplier<AssertionError> mockError() {
    return () -> new AssertionError("missed mocking?");
  }

  @Test
  @DisplayName("should save GuestBookDto as GuestBookEntity")
  void shouldSaveGuestBookDtoAsGuestBookEntity() {
    GuestBookEntryDto guestBookEntryDto = new GuestBookEntryDto();
    guestBookEntryDto.setGuestBook(new GuestBookDto());
    GuestBookDto guestBookDto = new GuestBookDto();
    guestBookDto.setEntries(Set.of(guestBookEntryDto));
    guestBookDto.setTitle("home sweet home");
    guestBookDto.setUser(new UserDto());

    guestBookServiceToTest.saveOrUpdate(guestBookDto);

    ArgumentCaptor<GuestBookEntity> argCaptor = ArgumentCaptor.forClass(GuestBookEntity.class);
    verify(guestBookRepositoryMock).save(argCaptor.capture());
    GuestBookEntity guestBookEntity = argCaptor.getValue();

    assertAll(
        () -> assertThat(guestBookEntity.getEntries()).as("entries").hasSize(1),
        () -> assertThat(guestBookEntity.getTitle()).as("title").isEqualTo("home sweet home"),
        () -> assertThat(guestBookEntity.getUser()).as("user").isNotNull()
    );
  }

  @Test
  @DisplayName("should save GuestBookEntryDto as GuestBookEntryEntity")
  void shouldSaveBlogEntryDtoAsBlogEntryEntity() {
    GuestBookEntryDto guestBookEntryDto = new GuestBookEntryDto();
    guestBookEntryDto.setGuestBook(new GuestBookDto());
    guestBookEntryDto.setCreatorName("me");
    guestBookEntryDto.setEntry("if i where a rich man...");

    guestBookServiceToTest.saveOrUpdate(guestBookEntryDto);

    ArgumentCaptor<GuestBookEntryEntity> argCaptor = ArgumentCaptor.forClass(GuestBookEntryEntity.class);
    verify(guestBookEntryRepositoryMock).save(argCaptor.capture());
    GuestBookEntryEntity guestBookEntryEntity = argCaptor.getValue();

    assertAll(
        () -> assertThat(guestBookEntryEntity.getGuestBook()).as("guest book").isNotNull(),
        () -> assertThat(guestBookEntryEntity.getCreatorName()).as("creator name").isEqualTo("me"),
        () -> assertThat(guestBookEntryEntity.getEntry()).as("entry").isEqualTo("if i where a rich man...")
    );
  }
}
