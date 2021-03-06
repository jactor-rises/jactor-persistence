package com.github.jactor.persistence.repository;

import com.github.jactor.persistence.entity.UserEntity;
import com.github.jactor.persistence.entity.UserEntity.UserType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

  Optional<UserEntity> findByUsername(String username);

  List<UserEntity> findByUserTypeIn(Collection<UserType> userType);
}
