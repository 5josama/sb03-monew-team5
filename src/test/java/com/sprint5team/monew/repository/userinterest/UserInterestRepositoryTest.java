package com.sprint5team.monew.repository.userinterest;




import com.sprint5team.monew.base.config.QuerydslConfig;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import com.sprint5team.monew.domain.user_interest.entity.UserInterest;
import com.sprint5team.monew.domain.user_interest.repository.UserInterestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PackageName  : com.sprint5team.monew.repository.user_interest
 * FileName     : UserInterestTest
 * Author       : dounguk
 * Date         : 2025. 7. 13.
 */

@DataJpaTest
@ActiveProfiles("test")
@Import({QuerydslConfig.class})
@DisplayName("User Interest Repository 슬라이스 테스트")
class UserInterestRepositoryTest {
    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private UserInterestRepository userInterestRepository;

    @Autowired
    private UserRepository userRepository;


    private User globalUser;
    private UserInterest globalUserInterest1, globalUserInterest2, globalUserInterest3;
    private Interest interest3;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        interestRepository.deleteAll();
        userInterestRepository.deleteAll();

        globalUser = new User("user@user.com", "user1", "password");
        ReflectionTestUtils.setField(globalUser, "createdAt", Instant.now());
        userRepository.saveAndFlush(globalUser);

        Interest interest1 = Interest.builder()
            .createdAt(Instant.now())
            .name("향수")
            .subscriberCount(5L)
            .build();
        interestRepository.saveAndFlush(interest1);

        globalUserInterest1 = UserInterest.builder()
            .user(globalUser)
            .interest(interest1)
            .createdAt(Instant.now())
            .build();
        userInterestRepository.saveAndFlush(globalUserInterest1);

        Interest interest2 = Interest.builder()
            .createdAt(Instant.now())
            .name("전자제품")
            .subscriberCount(5L)
            .build();
        ReflectionTestUtils.setField(interest2, "updatedAt", Instant.now());
        interestRepository.saveAndFlush(interest2);

        globalUserInterest2 = UserInterest.builder()
            .user(globalUser)
            .interest(interest2)
            .createdAt(Instant.now())
            .build();
        userInterestRepository.saveAndFlush(globalUserInterest2);

        interest3 = Interest.builder()
            .createdAt(Instant.now())
            .name("주류")
            .subscriberCount(5L)
            .build();
        ReflectionTestUtils.setField(interest3, "updatedAt", Instant.now());
        interestRepository.saveAndFlush(interest3);

        globalUserInterest3 = UserInterest.builder()
            .user(globalUser)
            .interest(interest3)
            .createdAt(Instant.now())
            .build();
        userInterestRepository.saveAndFlush(globalUserInterest3);
    }

    @Test
    void 사용자id를_통해_사용자가_구독한_관심사를_가져온다() throws Exception {
        // when
        Set<UserInterest> userInterests = userInterestRepository.findByUserId(globalUser.getId());

        Set<String> interestnames = userInterests.stream()
            .map(userInterest -> userInterest.getInterest().getName())
            .collect(Collectors.toSet());

        // then
        assertThat(userInterests).hasSize(3);
        assertThat(interestnames).contains("향수", "전자제품", "주류");
    }

    @Test
    void 사용자ID와_관심사ID를_이용해_관심사_구독중일_경우_true를_반환한다() throws Exception {
        // when
        boolean result = userInterestRepository.existsByUserIdAndInterestId(globalUserInterest1.getUser().getId(), globalUserInterest1.getInterest().getId());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 사용자ID와_관심사ID를_이용해_관심사_구독중이_아닐경우_false_반환한다() throws Exception {
        // given
        UUID invalidUserId = UUID.randomUUID();
        UUID invalidInterestId = UUID.randomUUID();

        // when
        boolean result = userInterestRepository.existsByUserIdAndInterestId(invalidUserId, invalidInterestId);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 관심사ID를_이용해_관심사가_있는지_확인한다() throws Exception {
        // given
        Interest interest = Interest.builder()
            .createdAt(Instant.now())
            .name("여름에 사용할 화장품 목록")
            .subscriberCount(5L)
            .build();
        interestRepository.save(interest);

        // when
        boolean result = interestRepository.existsById(interest.getId());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 요청자ID와_관심사ID를_이용해_구독을_가져온다() throws Exception {
        // when
        Optional<UserInterest> userInterest = userInterestRepository.findByUserIdAndInterestId(globalUser.getId(), globalUserInterest1.getInterest().getId());

        // then
        assertThat(userInterest).isNotNull();
        assertThat(userInterest.get().getCreatedAt()).isEqualTo(globalUserInterest1.getCreatedAt());
        assertThat(userInterest.get().getInterest()).isEqualTo(globalUserInterest1.getInterest());
        assertThat(userInterest.get().getUser()).isEqualTo(globalUserInterest1.getUser());
    }
}
