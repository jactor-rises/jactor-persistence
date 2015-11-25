package nu.hjemme.facade.db;

import nu.hjemme.client.datatype.Name;
import nu.hjemme.facade.MenuFacadeIntegrationTest;
import nu.hjemme.facade.config.HjemmeBeanContext;
import nu.hjemme.facade.config.HjemmeDbContext;
import nu.hjemme.persistence.BlogEntity;
import nu.hjemme.persistence.BlogEntryEntity;
import nu.hjemme.persistence.UserEntity;
import nu.hjemme.persistence.domain.DefaultBlogEntryEntity;
import nu.hjemme.test.matcher.MatchBuilder;
import nu.hjemme.test.matcher.TypeSafeBuildMatcher;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;

import static nu.hjemme.business.domain.AddressDomain.anAddress;
import static nu.hjemme.business.domain.BlogDomain.aBlog;
import static nu.hjemme.business.domain.BlogEntryDomain.aBlogEntry;
import static nu.hjemme.business.domain.PersonDomain.aPerson;
import static nu.hjemme.business.domain.UserDomain.aUser;
import static nu.hjemme.test.matcher.DescriptionMatcher.is;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HjemmeBeanContext.class, MenuFacadeIntegrationTest.HjemmeTestMenus.class, HjemmeDbContext.class})
@Transactional
public class BlogEntryDbIntegrationTest {

    @Resource(name = "sessionFactory") @SuppressWarnings("unused") // initialized by spring
    private SessionFactory sessionFactory;

    @Test public void willSaveBlogEntryEntityToThePersistentLayer() {
        Serializable id = session().save(aBlogEntry().with(aPersistedBlogTitled("my blog")).withEntryAs("svada", "lada").build().getEntity());

        session().flush();
        session().clear();

        assertThat((BlogEntryEntity) session().get(DefaultBlogEntryEntity.class, id), new TypeSafeBuildMatcher<BlogEntryEntity>("blog entry persisted") {
            @Override public MatchBuilder matches(BlogEntryEntity typeToTest, MatchBuilder matchBuilder) {
                return matchBuilder
                        .matches(typeToTest.getBlog().getTitle(), is(equalTo("my blog"), "blog.title"))
                        .matches(typeToTest.getCreatedTime(), is(notNullValue(), "entry.createdTime"))
                        .matches(typeToTest.getCreatorName(), is(equalTo(new Name("lada")), "entry.creator"))
                        .matches(typeToTest.getEntry(), is(equalTo("svada"), "entry.entry"));
            }
        });
    }

    private BlogEntity aPersistedBlogTitled(String blogTitled) {
        BlogEntity blogEntity = aBlog().with(aPersistedUser()).withTitleAs(blogTitled).build().getEntity();
        session().save(blogEntity);
        return blogEntity;
    }

    private UserEntity aPersistedUser() {
        UserEntity userEntity = aUser().withUserNameAs("titten")
                .withPasswordAs("demo")
                .withEmailAddressAs("helt@hjemme")
                .with(aPerson().withDescriptionAs("description")
                                .with(anAddress().withAddressLine1As("Hjemme")
                                                .withCityAs("Dirdal")
                                                .withCountryAs("NO", "no")
                                                .withZipCodeAs(1234)
                                )
                )
                .build().getEntity();

        session().save(userEntity);

        return userEntity;
    }

    private Session session() {
        return sessionFactory.getCurrentSession();
    }
}