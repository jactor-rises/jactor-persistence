package com.github.jactor.persistence.entity;

import static java.util.Objects.hash;
import static java.util.stream.Collectors.toSet;

import com.github.jactor.persistence.dto.BlogDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "T_BLOG")
public class BlogEntity implements PersistentEntity<BlogEntity> {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "blogSeq")
  @SequenceGenerator(name = "blogSeq", sequenceName = "T_BLOG_SEQ", allocationSize = 1)
  private Long id;

  @Embedded
  @AttributeOverride(name = "createdBy", column = @Column(name = "CREATED_BY"))
  @AttributeOverride(name = "timeOfCreation", column = @Column(name = "CREATION_TIME"))
  @AttributeOverride(name = "modifiedBy", column = @Column(name = "UPDATED_BY"))
  @AttributeOverride(name = "timeOfModification", column = @Column(name = "UPDATED_TIME"))
  private PersistentDataEmbeddable persistentDataEmbeddable;

  @Column(name = "CREATED")
  private LocalDate created;
  @Column(name = "TITLE")
  private String title;
  @JoinColumn(name = "USER_ID")
  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
  private UserEntity userEntity;
  @OneToMany(mappedBy = "blog", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private Set<BlogEntryEntity> entries = new HashSet<>();

  @SuppressWarnings("unused")
  BlogEntity() {
    // used by entity manager
  }

  private BlogEntity(BlogEntity blogEntity) {
    created = blogEntity.created;
    entries = blogEntity.entries.stream().map(BlogEntryEntity::copyWithoutId).collect(toSet());
    id = blogEntity.id;
    persistentDataEmbeddable = new PersistentDataEmbeddable();
    title = blogEntity.getTitle();
    userEntity = Optional.ofNullable(blogEntity.getUser()).map(UserEntity::copyWithoutId).orElse(null);
  }

  public BlogEntity(BlogDto blogDto) {
    created = blogDto.getCreated();
    id = blogDto.getId();
    persistentDataEmbeddable = new PersistentDataEmbeddable(blogDto.fetchPersistentDto());
    title = blogDto.getTitle();
    userEntity = Optional.ofNullable(blogDto.getUserInternal()).map(UserEntity::new).orElse(null);
  }

  public BlogDto asDto() {
    return new BlogDto(
        persistentDataEmbeddable.asPersistentDto(id),
        created, title, Optional.ofNullable(userEntity).map(UserEntity::asDto).orElse(null)
    );
  }

  public void add(BlogEntryEntity blogEntryEntity) {
    blogEntryEntity.setBlog(this);
    entries.add(blogEntryEntity);
  }

  @Override
  public BlogEntity copyWithoutId() {
    BlogEntity blogEntity = new BlogEntity(this);
    blogEntity.setId(null);

    return blogEntity;
  }

  @Override
  public void modify() {
    persistentDataEmbeddable.modify();
  }

  @Override
  public boolean equals(Object o) {
    return this == o || o != null && getClass() == o.getClass() &&
        Objects.equals(getTitle(), ((BlogEntity) o).getTitle()) &&
        Objects.equals(getUser(), ((BlogEntity) o).getUser());
  }

  @Override
  public int hashCode() {
    return hash(getTitle(), getUser());
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        .appendSuper(super.toString())
        .append(getCreated())
        .append(getTitle())
        .append(getUser())
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

  public LocalDate getCreated() {
    return created;
  }

  public Set<BlogEntryEntity> getEntries() {
    return entries;
  }

  public String getTitle() {
    return title;
  }

  public UserEntity getUser() {
    return userEntity;
  }

  public void setUser(UserEntity userEntity) {
    this.userEntity = userEntity;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public static BlogEntity aBlog(BlogDto blogDto) {
    return new BlogEntity(blogDto);
  }
}
