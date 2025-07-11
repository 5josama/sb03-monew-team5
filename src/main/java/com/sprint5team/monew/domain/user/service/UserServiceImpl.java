package com.sprint5team.monew.domain.user.service;

import com.sprint5team.monew.domain.user.dto.UserDto;
import com.sprint5team.monew.domain.user.dto.UserRegisterRequest;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.mapper.UserMapper;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService{

  private final UserMapper userMapper;
  private final UserRepository userRepository;

  @Override
  public UserDto register(UserRegisterRequest request) {
    String email = request.email();
    String nickname = request.nickname();
    String password = request.password();

    User user = User.builder()
        .email(email)
        .nickname(nickname)
        .password(password)
        .build();
    userRepository.save(user);

    return userMapper.toDto(user);
  }
}
