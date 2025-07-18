package com.sprint5team.monew.repository.user_interest;




import com.sprint5team.monew.base.config.QuerydslConfig;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.interest.repository.InterestRepositoryImpl;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
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
import java.util.Set;
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
        ReflectionTestUtils.setField(globalUserInterest1, "updatedAt", Instant.now());
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
        ReflectionTestUtils.setField(globalUserInterest2, "updatedAt", Instant.now());
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
        ReflectionTestUtils.setField(globalUserInterest3, "updatedAt", Instant.now());
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
}
