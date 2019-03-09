package com.github.jactor.persistence.entity.blog;

import com.github.jactor.persistence.builder.AbstractBuilder;
import com.github.jactor.persistence.builder.MissingFields;
import com.github.jactor.persistence.entity.user.UserEntity;
import com.github.jactor.persistence.entity.user.UserEntityBuilder;

import java.util.Optional;

public class BlogEntityBuilder extends AbstractBuilder<BlogEntity> {
    private String title;
    private UserEntity userEntity;

    BlogEntityBuilder() {
        super(BlogEntityBuilder::validate);
    }

    public BlogEntityBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public BlogEntityBuilder with(UserEntity userEntity) {
        this.userEntity = userEntity;
        return this;
    }

    public BlogEntityBuilder with(UserEntityBuilder userEntityBuilder) {
        return with(userEntityBuilder.build());
    }

    @Override protected BlogEntity buildBean() {
        BlogEntity blogEntityToBuild = new BlogEntity();
        blogEntityToBuild.setTitle(title);
        blogEntityToBuild.setUserEntity(userEntity);

        return blogEntityToBuild;
    }

    private static Optional<MissingFields> validate(BlogEntity blogEntity, MissingFields missingFields) {
        missingFields.addInvalidFieldWhenNoValue(BlogEntity.class.getSimpleName(), "title", blogEntity.getTitle());
        missingFields.addInvalidFieldWhenNoValue(BlogEntity.class.getSimpleName(), "userEntity", blogEntity.getUser());
        return missingFields.presentWhenFieldsAreMissing();
    }
}
