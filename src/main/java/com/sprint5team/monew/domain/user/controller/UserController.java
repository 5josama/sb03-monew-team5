package com.sprint5team.monew.domain.user.controller;

import com.sprint5team.monew.domain.user.dto.UserDto;
import com.sprint5team.monew.domain.user.dto.UserLoginRequest;
import com.sprint5team.monew.domain.user.dto.UserRegisterRequest;
import com.sprint5team.monew.domain.user.dto.UserUpdateRequest;
import com.sprint5team.monew.domain.user.service.UserService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController implements UserApi  {

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

  @PatchMapping("/{userId}")
  public ResponseEntity<UserDto> update(
      @PathVariable("userId") UUID userId,
      @RequestBody UserUpdateRequest request){

    UserDto user = userService.update(userId, request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(user);
  }

  @DeleteMapping("/{userId}/hard")
  public ResponseEntity<Void> hardDelete(
      @PathVariable UUID userId
    ) {
    userService.hardDelete(userId);
    return ResponseEntity
        .noContent()
        .build();
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> softDelete(
      @PathVariable UUID userId
    ) {
    userService.softDelete(userId);
    return ResponseEntity
        .noContent()
        .build();
    }
}
