package com.sprint5team.monew.domain.user.service;

import com.sprint5team.monew.domain.user.dto.UserDto;
import com.sprint5team.monew.domain.user.dto.UserRegisterRequest;

public interface UserService {

  UserDto register(UserRegisterRequest request);
}
