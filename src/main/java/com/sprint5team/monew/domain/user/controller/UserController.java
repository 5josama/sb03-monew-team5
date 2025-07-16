package com.sprint5team.monew.domain.user.controller;

import com.sprint5team.monew.domain.user.dto.UserDto;
import com.sprint5team.monew.domain.user.dto.UserLoginRequest;
import com.sprint5team.monew.domain.user.dto.UserRegisterRequest;
import com.sprint5team.monew.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<UserDto> register(
      @RequestBody @Valid UserRegisterRequest request) {

    UserDto createdUser = userService.register(request);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdUser);
  }

  @PostMapping("/login")
  public ResponseEntity<UserDto> login(
      @RequestBody @Valid UserLoginRequest request){

    UserDto user = userService.login(request.email(), request.password());

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(user);
  }
}
