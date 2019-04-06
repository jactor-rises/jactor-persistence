package com.github.jactor.persistence.service;

import com.github.jactor.persistence.dto.UserDto;
import com.github.jactor.persistence.entity.UserEntity;
import com.github.jactor.persistence.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public Optional<UserDto> find(String username) {
    return userRepository.findByUsername(username).map(UserEntity::asDto);
  }

  public Optional<UserDto> find(Long id) {
    return userRepository.findById(id).map(UserEntity::asDto);
  }

  public UserDto saveOrUpdate(UserDto userDto) {
    UserEntity userEntity = new UserEntity(userDto);
    userRepository.save(userEntity);

    return userEntity.asDto();
  }

  public List<String> findUsernamesOnActiveUsers() {
    return userRepository.findByUserType(UserEntity.UserType.ACTIVE).stream()
        .map(UserEntity::getUsername)
        .collect(Collectors.toList());
  }
}
