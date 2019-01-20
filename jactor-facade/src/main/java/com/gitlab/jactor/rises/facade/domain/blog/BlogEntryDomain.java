package com.gitlab.jactor.rises.facade.domain.blog;

import com.gitlab.jactor.rises.commons.datatype.Name;
import com.gitlab.jactor.rises.commons.dto.BlogEntryDto;
import com.gitlab.jactor.rises.facade.domain.PersistentDomain;

import java.util.Optional;

public class BlogEntryDomain extends PersistentDomain {

    private final BlogEntryDto blogEntryDto;

    BlogEntryDomain(BlogEntryDto blogEntryDto) {
        this.blogEntryDto = blogEntryDto;
    }

    BlogDomain getBlog() {
        return Optional.ofNullable(blogEntryDto.getBlog()).map(BlogDomain::new).orElse(null);
    }

    String getEntry() {
        return blogEntryDto.getEntry();
    }

    Name getCreatorName() {
        return Optional.ofNullable(blogEntryDto.getCreatorName()).map(Name::new).orElse(null);
    }

    @Override public BlogEntryDto getDto() {
        return blogEntryDto;
    }

    static BlogEntryBuilder aBlogEntry() {
        return new BlogEntryBuilder();
    }
}
