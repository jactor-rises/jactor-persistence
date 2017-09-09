package com.github.jactorrises.model.internal.domain.blog;

import com.github.jactorrises.model.internal.persistence.entity.blog.BlogEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.jactorrises.model.internal.domain.blog.BlogEntryDomain.aBlogEntry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@DisplayName("The BlogEntryBuilder")
class BlogEntryDomainBuilderTest {

    @DisplayName("should not build a blog entry without the entry")
    @Test void willNotBuildBlogEntryWithoutTheEntry() {
        assertThatIllegalArgumentException().isThrownBy(() -> aBlogEntry().withEntry(null, "aCreator").with(new BlogEntity()).build())
                .withMessage(BlogEntryDomainBuilder.THE_ENTRY_CANNOT_BE_EMPTY);
    }

    @DisplayName("should not build a blog entry with an empty entry")
    @Test void willNotBuildBlogEntryWithAnEmptyEntry() {
        assertThatIllegalArgumentException().isThrownBy(() -> aBlogEntry().withEntry("", "aCreator").with(new BlogEntity()).build())
                .withMessage(BlogEntryDomainBuilder.THE_ENTRY_CANNOT_BE_EMPTY);
    }

    @DisplayName("should not build a blog entry with a blog")
    @Test void willNotBuildBlogEntryWithoutTheBlog() {
        assertThatIllegalArgumentException().isThrownBy(() -> aBlogEntry().withEntry("some entry", "aCreatorName").build())
                .withMessage(BlogEntryDomainBuilder.THE_ENTRY_MUST_BELONG_TO_A_BLOG);
    }

    @DisplayName("should not build a blog entry with the creator")
    @Test void willNotBuildBlogEntryWithoutTheCreator() {
        assertThatIllegalArgumentException().isThrownBy(() -> aBlogEntry().withEntry("some entry", null).with(new BlogEntity()).build())
                .withMessage(BlogEntryDomainBuilder.THE_ENTRY_MUST_BE_CREATED_BY_SOMEONE);
    }

    @DisplayName("should build a blog entry when all required fields are set")
    @Test void willBuildBlogEntryWhenAllRequiredFieldsAreSet() {
        assertThat(aBlogEntry().with(new BlogEntity()).withEntry("some entry", "aCreator").build()).isNotNull();
    }

}
