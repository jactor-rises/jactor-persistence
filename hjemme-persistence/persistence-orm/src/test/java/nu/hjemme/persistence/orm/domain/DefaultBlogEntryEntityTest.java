package nu.hjemme.persistence.orm.domain;

import nu.hjemme.persistence.client.BlogEntryEntity;
import nu.hjemme.persistence.orm.time.NowAsPureDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static nu.hjemme.test.matcher.EqualMatcher.implementsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class DefaultBlogEntryEntityTest {
    private DefaultBlogEntryEntity blogEntryEntityToTest;

    @Before public void initBlogEntryEntity() {
        NowAsPureDate.set();
        blogEntryEntityToTest = new DefaultBlogEntryEntity();
    }

    @Test public void willHaveCorrectImplementedHashCode() {
        blogEntryEntityToTest.setBlog(new DefaultBlogEntity());
        blogEntryEntityToTest.setEntry("some entry");
        blogEntryEntityToTest.setCreatorName("some creator");

        BlogEntryEntity equal = new DefaultBlogEntryEntity(blogEntryEntityToTest);

        BlogEntryEntity notEqual = new DefaultBlogEntryEntity();
        notEqual.setEntry("some other entry");
        notEqual.setCreatorName("some other creator");
        notEqual.setBlog(new DefaultBlogEntity());
        assertThat(blogEntryEntityToTest.hashCode(), implementsWith(equal.hashCode(), notEqual.hashCode()));
    }

    @Test public void willHaveCorrectImplementedEquals() {
        blogEntryEntityToTest.setBlog(new DefaultBlogEntity());
        blogEntryEntityToTest.setEntry("some entry");
        blogEntryEntityToTest.setCreatorName("some creator");

        BlogEntryEntity equal = new DefaultBlogEntryEntity(blogEntryEntityToTest);

        BlogEntryEntity notEqual = new DefaultBlogEntryEntity();
        notEqual.setEntry("some other entry");
        notEqual.setCreatorName("some other creator");
        notEqual.setBlog(new DefaultBlogEntity());

        assertThat(blogEntryEntityToTest, implementsWith(equal, notEqual));
    }

    @Test public void willBeEqualAnIdenticalEntity() {
        blogEntryEntityToTest.setBlog(new DefaultBlogEntity());
        blogEntryEntityToTest.setEntry("some entry");
        blogEntryEntityToTest.setCreatorName("some creator");

        BlogEntryEntity equal = new DefaultBlogEntryEntity();
        equal.setBlog(new DefaultBlogEntity());
        equal.setEntry("some entry");
        equal.setCreatorName("some creator");

        assertThat(blogEntryEntityToTest, equalTo(equal));
    }

    @Test public void willBeEqualAnIdenticalEntityUsingConstructor() {
        blogEntryEntityToTest.setBlog(new DefaultBlogEntity());
        blogEntryEntityToTest.setEntry("some entry");
        blogEntryEntityToTest.setCreatorName("some creator");

        BlogEntryEntity equal = new DefaultBlogEntryEntity(blogEntryEntityToTest);

        assertThat(blogEntryEntityToTest, equalTo(equal));
    }

    @After
    public void removeNowAsPureDate() {
        NowAsPureDate.remove();
    }
}
