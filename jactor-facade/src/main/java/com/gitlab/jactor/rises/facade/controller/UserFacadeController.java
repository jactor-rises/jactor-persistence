package com.gitlab.jactor.rises.facade.controller;

import com.gitlab.jactor.rises.commons.datatype.Username;
import com.gitlab.jactor.rises.commons.dto.UserDto;
import com.gitlab.jactor.rises.facade.service.UserDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserFacadeController {

    private final UserDomainService userDomainService;

    @Autowired public UserFacadeController(UserDomainService userDomainService) {
        this.userDomainService = userDomainService;
    }

    @GetMapping("/all/usernames")
    public ResponseEntity<List<String>> findAllUsernames() {
        return new ResponseEntity<>(userDomainService.findAllUsernames(), HttpStatus.OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<UserDto> find(@PathVariable("username") String username) {
        return userDomainService.find(new Username(username))
                .map(userDto -> new ResponseEntity<>(userDto, HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
