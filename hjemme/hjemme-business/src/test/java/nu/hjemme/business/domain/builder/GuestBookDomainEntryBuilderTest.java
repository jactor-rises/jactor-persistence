package nu.hjemme.business.domain.builder;

import nu.hjemme.business.domain.GuestBookEntryDomain;
import nu.hjemme.business.domain.persistence.GuestBookEntity;
import nu.hjemme.business.domain.persistence.PersonEntity;
import nu.hjemme.client.datatype.Name;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/** @author Tor Egil Jacobsen */
public class GuestBookDomainEntryBuilderTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void willNotBuildGuestBookEntryWithoutAnEntry() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(GuestBookEntryDomainBuilder.THE_ENTRY_CANNOT_BE_EMPTY);

        GuestBookEntryDomainBuilder.init().appendCreatorName("some creator").appendGuestBook(new GuestBookEntity()).build();
    }

    @Test
    public void willNotBuildGuestBookEntryWithAnEmptyEntry() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(GuestBookEntryDomainBuilder.THE_ENTRY_CANNOT_BE_EMPTY);

        GuestBookEntryDomainBuilder.init()
                .appendEntry("")
                .appendCreatorName("some creator")
                .appendGuestBook(new GuestBookEntity())
                .build();
    }

    @Test
    public void willNotBuildGuestBookEntryWithoutTheGuestBook() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(GuestBookEntryDomainBuilder.THE_ENTRY_MUST_BELONG_TO_A_GUEST_BOOK);

        GuestBookEntryDomainBuilder.init().appendEntry("some entry").appendCreatorName("some creator").build();
    }

    @Test
    public void willNotBuildGuestBookEntryWithoutTheNameOfTheCreator() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(GuestBookEntryDomainBuilder.THE_ENTRY_MUST_BE_CREATED_BY_SOMEONE);

        GuestBookEntryDomainBuilder.init()
                .appendEntry("some entry")
                .appendGuestBook(new GuestBookEntity())
                .build();
    }

    @Test
    public void willNotBuildGuestBookEntryWithAnEmptyNameOfTheCreator() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(hentFeilmeldingFraName());

        GuestBookEntryDomainBuilder.init()
                .appendEntry("some entry")
                .appendCreatorName("")
                .appendGuestBook(new GuestBookEntity())
                .build();
    }

    public String hentFeilmeldingFraName() {
        String nameErrorMessage = null;

        try {
            new Name("");
        } catch (IllegalArgumentException iae) {
            nameErrorMessage = iae.getMessage();
        }

        return nameErrorMessage;
    }

    @Test
    public void willBuildGuestBookEntryWhenAllRequiredFieldsAreSet() {
        GuestBookEntryDomain guestBookEntryDomain = GuestBookEntryDomainBuilder.init()
                .appendEntry("some entry")
                .appendCreatorName("some creator")
                .appendGuestBook(new GuestBookEntity())
                .build();

        assertThat("GuestBookEntryEntity", guestBookEntryDomain, is(notNullValue()));
    }

    @Test
    public void willSetCreatorNameWhenCreatorIsAppended() {
        PersonEntity creator = new PersonEntity();
        creator.setFirstName(new Name("some creator"));

        GuestBookEntryDomain guestBookEntryDomain = GuestBookEntryDomainBuilder.init()
                .appendEntry("some entry")
                .appendCreator(creator)
                .appendGuestBook(new GuestBookEntity())
                .build();

        assertThat("CreatorName", guestBookEntryDomain.getCreatorName(), is(equalTo(new Name("some creator"))));
    }
}