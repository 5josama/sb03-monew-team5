package com.sprint5team.monew.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sprint5team.monew.domain.article.repository.ArticleCountRepository;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.comment.repository.LikeRepository;
import com.sprint5team.monew.domain.notification.repository.NotificationRepository;
import com.sprint5team.monew.domain.user.dto.UserDto;
import com.sprint5team.monew.domain.user.dto.UserRegisterRequest;
import com.sprint5team.monew.domain.user.dto.UserUpdateRequest;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.exception.InvalidInputValueException;
import com.sprint5team.monew.domain.user.exception.InvalidLoginException;
import com.sprint5team.monew.domain.user.exception.UserAlreadyExistsException;
import com.sprint5team.monew.domain.user.exception.UserNotFoundException;
import com.sprint5team.monew.domain.user.mapper.UserMapper;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import com.sprint5team.monew.domain.user.service.UserServiceImpl;
import com.sprint5team.monew.domain.user_interest.repository.UserInterestRepository;
import java.time.Instant;
import java.util.Optional;
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

  @Mock private UserRepository userRepository;
  @Mock private ArticleCountRepository articleCountRepository;
  @Mock private CommentRepository commentRepository;
  @Mock private LikeRepository likeRepository;
  @Mock private UserInterestRepository userInterestRepository;
  @Mock private NotificationRepository notificationRepository;
  @Mock private UserMapper userMapper;

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
    userDto = new UserDto(id, email, nickname, Instant.now());
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

  @Test
  void 사용자_로그인_성공() {
    // given
    given(userRepository.findByEmailAndPassword(eq(email), eq(password))).willReturn(Optional.of(user));
    given(userMapper.toDto(any(User.class))).willReturn(userDto);

    // when
    UserDto result = userService.login(email, password);

    // then
    assertThat(result).isEqualTo(userDto);
    verify(userRepository).findByEmailAndPassword(eq(email), eq(password));
  }

  @Test
  void 사용자_로그인_실패() {
    // given
    String wrongPassword = "wrongPassword";
    given(userRepository.findByEmailAndPassword(eq(email), any(String.class))).willThrow(InvalidLoginException.class);

    // when and then
    assertThatThrownBy(() -> userService.login(email, wrongPassword))
        .isInstanceOf(InvalidLoginException.class);
  }

  @Test
  void 사용자_정보_수정_성공() {
    // given
    String newNickname = "newNickname";
    UserUpdateRequest request = new UserUpdateRequest(newNickname);
    given(userRepository.findById(id)).willReturn(Optional.of(user));
    given(userRepository.save(any(User.class))).willReturn(user);

    // when
    userService.update(id, request);

    // then
    verify(userRepository).save(any(User.class));
    assertThat(user.getNickname()).isEqualTo(newNickname);
    then(userRepository).should(times(1)).save(any(User.class));
  }

  @Test
  void 사용자_정보_수정_실패_닉네임_길이_초과() {
    // given
    String newNickname = "nnnnnnnnnnnnnnnnnnnnNewname"; // 닉네임 길이 초과
    UserUpdateRequest request = new UserUpdateRequest(newNickname);
    given(userRepository.findById(id)).willReturn(Optional.of(user));
    given(userService.update(id, request)).willThrow(InvalidInputValueException.class);

    // when and then
    assertThatThrownBy(() -> userService.update(id, request))
        .isInstanceOf(InvalidInputValueException.class);
  }

  @Test
  void 사용자_논리삭제_성공() {
    // given
    given(userRepository.findById(id)).willReturn(Optional.of(user));
    given(userRepository.save(any(User.class))).willReturn(user);

    // when
    userService.softDelete(id);

    // then
    verify(userRepository).save(any(User.class));
    assertThat(user.getIsDeleted()).isTrue();
    then(userRepository).should(times(1)).save(any(User.class));
  }

  @Test
  void 사용자_논리삭제_실패_존재하지_않는_사용자() {
    // given
    given(userRepository.findById(id)).willReturn(Optional.empty());

    // when and then
    assertThatThrownBy(() -> userService.softDelete(id))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  void 사용자_물리삭제_성공() {
    // given
    willDoNothing().given(userRepository).deleteById(id);
    willDoNothing().given(articleCountRepository).deleteAllByUserId(id);
    willDoNothing().given(commentRepository).deleteAllByUserId(id);
    willDoNothing().given(likeRepository).deleteAllByUserId(id);
    willDoNothing().given(userInterestRepository).deleteAllByUserId(id);
    willDoNothing().given(notificationRepository).deleteAllByUserId(id);

    // when
    userService.hardDelete(id);

    // then
    then(userRepository).should(times(1)).deleteById(id);
    then(articleCountRepository).should(times(1)).deleteAllByUserId(id);
    then(commentRepository).should(times(1)).deleteAllByUserId(id);
    then(likeRepository).should(times(1)).deleteAllByUserId(id);
    then(userInterestRepository).should(times(1)).deleteAllByUserId(id);
    then(notificationRepository).should(times(1)).deleteAllByUserId(id);
  }

  @Test
  void 사용자_물리삭제_실패_존재하지_않는_사용자() {
    // given
    UUID notExistUserId = UUID.randomUUID();
    given(userRepository.findById(notExistUserId)).willReturn(Optional.empty());

    // when and then
    assertThatThrownBy(() -> userService.hardDelete(notExistUserId))
        .isInstanceOf(UserNotFoundException.class);
  }
}
