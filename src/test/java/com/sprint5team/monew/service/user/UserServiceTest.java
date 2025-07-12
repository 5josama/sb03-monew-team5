package com.sprint5team.monew.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint5team.monew.domain.user.dto.UserDto;
import com.sprint5team.monew.domain.user.dto.UserRegisterRequest;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.exception.UserAlreadyExistsException;
import com.sprint5team.monew.domain.user.mapper.UserMapper;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import com.sprint5team.monew.domain.user.service.UserServiceImpl;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserServiceImpl userService;

  private UUID id;
  private String email;
  private String nickname;
  private String password;

  private User user;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    email = "test@test.kr";
    nickname = "test";
    password = "test1234";

    user = new User(email, nickname, password);
    ReflectionTestUtils.setField(user, "id", id);
    userDto = new UserDto(id, email, nickname, null);
  }

  @Test
  void 사용자_등록_성공() {
    // given
    UserRegisterRequest request = new UserRegisterRequest(email, nickname, password);
    given(userRepository.save(any(User.class))).willReturn(user);
    given(userMapper.toDto(any(User.class))).willReturn(userDto);

    // when
    UserDto result = userService.register(request);

    // then
    assertThat(result).isEqualTo(userDto);
    verify(userRepository).save(any(User.class));
  }

  @Test
  void 이미_존재하는_이메일로_사용자_생성_시도_시_실패() {
    // given
    UserRegisterRequest request = new UserRegisterRequest(email, nickname, password);
    given(userRepository.existsByEmail(eq(email))).willReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.register(request))
        .isInstanceOf(UserAlreadyExistsException.class);
  }

}
