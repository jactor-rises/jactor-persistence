package com.github.jactorrises.model.internal.domain.blog;

import com.github.jactorrises.model.internal.domain.DomainBuilder;
import com.github.jactorrises.model.internal.persistence.entity.blog.BlogEntity;
import com.github.jactorrises.model.internal.persistence.entity.blog.BlogEntryEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static java.util.Arrays.asList;

public final class BlogEntryDomainBuilder extends DomainBuilder<BlogEntryDomain> {
    static final String THE_ENTRY_MUST_BELONG_TO_A_BLOG = "The blog entry must belong to a blog";
    static final String THE_ENTRY_CANNOT_BE_EMPTY = "The entry field cannot be empty";
    static final String THE_ENTRY_MUST_BE_CREATED_BY_SOMEONE = "The entry must be created by someone";

    private final BlogEntryEntity blogEntryEntity = new BlogEntryEntity();

    BlogEntryDomainBuilder() {
        super(asList(
                domain -> StringUtils.isNotBlank(domain.getEntry()) ? Optional.empty() : Optional.of(THE_ENTRY_CANNOT_BE_EMPTY),
                domain -> domain.getCreatorName() != null ? Optional.empty() : Optional.of(THE_ENTRY_MUST_BE_CREATED_BY_SOMEONE),
                domain -> domain.getBlog() != null ? Optional.empty() : Optional.of(THE_ENTRY_MUST_BELONG_TO_A_BLOG)
        ));
    }

    public BlogEntryDomainBuilder withEntry(String entry, String creator) {
        blogEntryEntity.setEntry(entry);
        blogEntryEntity.setCreatorName(creator);
        return this;
    }

    public BlogEntryDomainBuilder with(BlogEntity blogEntity) {
        blogEntryEntity.setBlog(blogEntity);
        return this;
    }

    @Override protected BlogEntryDomain buildBeforeValidation() {
        return new BlogEntryDomain(blogEntryEntity);
    }
}
