package com.sprint5team.monew.repository.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertThrows;

import com.sprint5team.monew.base.config.QuerydslConfig;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.exception.InvalidLoginException;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
@Import({QuerydslConfig.class})

class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EntityManager entityManager;

  private User createTestUser(String email, String nickname, String password) {
    User user = new User(email, nickname, password);
    return userRepository.save(user);
  }

  @Test
  void 신규_사용자_저장_성공() {
    // given
    String email = "save@user.com";
    String nickname = "newbie";
    String password = "new1234";

    // when
    User savedUser = userRepository.save(createTestUser(email, nickname, password));

    // then
    assertThat(savedUser).isNotNull();
    assertThat(savedUser.getId()).isNotNull();
    assertThat(savedUser.getEmail()).isEqualTo(email);
    assertThat(savedUser.getNickname()).isEqualTo(nickname);
    assertThat(savedUser.getPassword()).isEqualTo(password);
    assertThat(savedUser.getCreatedAt()).isNotNull();
    assertThat(savedUser.getCreatedAt()).isBeforeOrEqualTo(Instant.now());
  }

  @Test
  void 중복_이메일로_저장_실패() {
    // given
    String email = "exists@test.kr";
    userRepository.save(User.builder()
        .email(email)
        .nickname("exists")
        .password("test1234")
        .build());

    // when and then
    assertThatThrownBy(() -> {
      userRepository.save(User.builder()
          .email(email)
          .nickname("new")
          .password("test1234")
          .build()
      );
      entityManager.flush();
    }).isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  void 이메일_비밀번호로_사용자_조회_성공() {
    // given
    String email = "test@test.kr";
    String password = "test1234";
    createTestUser(email, "test", password);

    // when
    User user = userRepository.findByEmailAndPassword(email, password)
        .orElseThrow(InvalidLoginException::new);

    // then
    assertThat(user).isNotNull();
    assertThat(user.getEmail()).isEqualTo(email);
    assertThat(user.getPassword()).isEqualTo(password);
  }

  @Test
  void 이메일_비밀번호로_사용자_조회_실패() {
    // given
    String email = "test@test.kr";
    String password = "test1234";
    createTestUser(email, "test", password);

    // when
    User user = userRepository
        .findByEmailAndPassword(email, "wrongpassword")
        .orElseThrow(InvalidLoginException::new);

    // then
    assertThat(user).isNull();
  }

  @Test
  void 사용자_닉네임_수정_성공() {
    // given
    String email = "test@test.kr";
    String nickname = "test";
    String password = "test1234";
    User user = userRepository.save(createTestUser(email, nickname, password));

    String newNickname = "newNickname";

    // when
    user.updateNickname(newNickname);

    // then
    assertThat(userRepository.findById(user.getId()).get().getNickname()).isEqualTo(newNickname);
  }

}
