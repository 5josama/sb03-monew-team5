package com.sprint5team.monew.domain.user.service;

import com.sprint5team.monew.domain.user.dto.UserDto;
import com.sprint5team.monew.domain.user.dto.UserRegisterRequest;
import com.sprint5team.monew.domain.user.dto.UserUpdateRequest;
import java.util.UUID;

public interface UserService {

  UserDto register(UserRegisterRequest request);

  UserDto login(String email, String password);

  UserDto update(UUID userId, UserUpdateRequest request);

  void hardDelete(UUID userId);

  void softDelete(UUID id);
}
