package com.github.jactor.persistence.entity;

import com.github.jactor.persistence.dto.PersistentDto;
import com.github.jactor.persistence.time.Now;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@MappedSuperclass
public abstract class PersistentEntity {

    private @Column(name = "CREATION_TIME") LocalDateTime creationTime;
    private @Column(name = "CREATED_BY") String createdBy;
    private @Column(name = "UPDATED_TIME") LocalDateTime updatedTime;
    private @Column(name = "UPDATED_BY") String updatedBy;

    protected PersistentEntity() {
        createdBy = "todo #3";
        creationTime = Now.asDateTime();
        updatedBy = "todo #3";
        updatedTime = Now.asDateTime();
    }

    protected PersistentEntity(PersistentEntity persistentEntity) {
        createdBy = persistentEntity.createdBy;
        creationTime = persistentEntity.creationTime;
        updatedBy = persistentEntity.updatedBy;
        updatedTime = persistentEntity.updatedTime;
    }

    protected PersistentEntity(PersistentDto persistentDto) {
        setId(persistentDto.getId());
        createdBy = persistentDto.getCreatedBy();
        creationTime = persistentDto.getCreationTime();
        updatedBy = persistentDto.getUpdatedBy();
        updatedTime = persistentDto.getUpdatedTime();
    }

    protected PersistentDto initPersistentDto() {
        return new PersistentDto(getId(), createdBy, creationTime, updatedBy, updatedTime);
    }

    public PersistentEntity addSequencedId(Sequencer sequencer) {
        if (getId() == null) {
            addSequencedId(this, sequencer);
        }

        fetchAllSequencedDependencies().stream()
                .filter(dependency -> dependency.getId() == null)
                .forEach(depencency -> addSequencedId(depencency, sequencer));

        return this;
    }

    private void addSequencedId(PersistentEntity entity, Sequencer sequencer) {
        Long id = sequencer.nextVal(entity.getClass());
        entity.setId(id);
    }

    protected Stream<Optional<PersistentEntity>> streamSequencedDependencies(PersistentEntity... persistentEntities) {
        if (persistentEntities == null) {
            return Stream.empty();
        }

        return Arrays.stream(persistentEntities)
                .map(Optional::ofNullable);
    }

    List<PersistentEntity> fetchAllSequencedDependencies() {
        List<PersistentEntity> sequencedDependencies = fetchSequencedDependencies(this);
        List<PersistentEntity> allSequencedDependencies = new ArrayList<>();

        for (PersistentEntity persistentEntity : sequencedDependencies) {
            addAllSequencedDependencis(persistentEntity, allSequencedDependencies);
        }

        return allSequencedDependencies;
    }

    private void addAllSequencedDependencis(PersistentEntity persistentEntity, List<PersistentEntity> allSequencedDependencies) {
        allSequencedDependencies.add(persistentEntity);
        List<PersistentEntity> otherSequencedDependencies = fetchSequencedDependencies(persistentEntity);

        otherSequencedDependencies.forEach(dependency -> {
            if (!allSequencedDependencies.contains(dependency)) {
                addAllSequencedDependencis(dependency, allSequencedDependencies);
            }
        });
    }

    private List<PersistentEntity> fetchSequencedDependencies(PersistentEntity persistentEntity) {
        return persistentEntity.streamSequencedDependencies()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    public @Override String toString() {
        return String.format("%s.id<%s>", fetchEntityShortName(), getId());
    }

    private String fetchEntityShortName() {
        String simpleName = getClass().getSimpleName();

        return simpleName.substring(0, simpleName.indexOf("Entity"));
    }

    public abstract PersistentEntity copy();

    protected abstract Stream<Optional<PersistentEntity>> streamSequencedDependencies();

    public abstract Long getId();

    protected abstract void setId(Long id);

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    protected void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    protected void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public interface Sequencer {
        Long nextVal(Class<?> entityClass);
    }
}