package com.sprint5team.monew.repository.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint5team.monew.base.config.QuerydslConfig;
import com.sprint5team.monew.domain.interest.repository.InterestRepositoryImpl;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import java.time.Instant;
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

}
