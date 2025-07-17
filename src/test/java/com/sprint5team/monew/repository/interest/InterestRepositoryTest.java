package com.sprint5team.monew.repository.interest;

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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PackageName  : com.sprint5team.monew.repository.interest
 * FileName     : InterestRepositoryTest
 * Author       : dounguk
 * Date         : 2025. 7. 15.
 */

@DataJpaTest
@ActiveProfiles("test")
@Import({InterestRepositoryImpl.class, QuerydslConfig.class})
@DisplayName("Interest Repository 슬라이스 테스트")
public class InterestRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    private Interest interestA, interestB, interestC;
    private final Instant baseTime = Instant.parse("2025-07-14T00:00:00Z");
    private double threshold = 0.8;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserInterestRepository userInterestRepository;

    @BeforeEach
    public void setup() {
        keywordRepository.deleteAll();
        interestRepository.deleteAll();
        userRepository.deleteAll();
        userInterestRepository.deleteAll();

        interestA = Interest.builder()
            .name("DEEP LEARNING")
            .subscriberCount(50L)
            .createdAt(baseTime.minus(Duration.ofMinutes(10)))
            .build();
        interestRepository.save(interestA);
        interestB = Interest.builder()
            .name("게임")
            .subscriberCount(200L)
            .createdAt(baseTime.minus(Duration.ofMinutes(20)))
            .build();
        interestRepository.save(interestB);
        interestC = Interest.builder()
            .name("스포츠")
            .subscriberCount(100L)
            .createdAt(baseTime.minus(Duration.ofMinutes(5)))
            .build();
        interestRepository.save(interestC);
        interestRepository.flush();
    }

    // existsByNameEqualsIgnoreCase
    @Test
    void 동일한_관심사_이름일_경우_true를_반환한다() throws Exception {
        // when
        boolean result = interestRepository.existsByNameEqualsIgnoreCase("게임");
        // then
        assertThat(result).isTrue();

    }

    @Test
    void 대소문자를_구분하지_않고_관심사가_동일하면_true를_반환한다() throws Exception {
        // when
        boolean result = interestRepository.existsByNameEqualsIgnoreCase("deep learning");

        // then
        assertThat(result).isTrue();

    }

    @Test
    void 관심사_이름이_다를경우_false_를_반환한다() throws Exception {
        // when
        boolean result = interestRepository.existsByNameEqualsIgnoreCase("스포츠카");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 관심사_객체가_정상_저장된다() throws Exception {
        // given
        interestRepository.deleteAll();
        Interest interest = Interest.builder()
            .name("관심사")
            .createdAt(baseTime)
            .build();

        // when
        interestRepository.save(interest);

        // then
        assertThat(interestRepository.count()).isEqualTo(1);
    }

    @Test
    void 관심사가_정상_삭제된다() throws Exception {
        // when
        interestRepository.delete(interestA);

        // then
        assertThat(interestRepository.count()).isEqualTo(2L);
    }

    @Test
    void 관심사가_삭제되면_관련_키워드들도_삭제된다() throws Exception {
        // given
        interestRepository.deleteAll();
        keywordRepository.deleteAll();

        Interest interest = Interest.builder()
            .createdAt(baseTime)
            .name("AI")
            .subscriberCount(0L)
            .userInterests(new ArrayList<>())
            .build();
        ReflectionTestUtils.setField(interest, "updatedAt", baseTime);
        interest = interestRepository.save(interest);
        entityManager.flush();
        entityManager.clear();

        Keyword keyword = Keyword.builder()
            .createdAt(baseTime)
            .name("키워드1")
            .interest(interest)
            .build();
        keywordRepository.save(keyword);

        // when
        interestRepository.delete(interest);

        // then
        assertThat(interestRepository.count()).isEqualTo(0L);
        assertThat(keywordRepository.count()).isEqualTo(0L);
    }

    @Test
    void 관심사가_삭제되면_관련_구독도_삭제된다() throws Exception {
        // given
        Instant baseTime = Instant.now();

        Interest interest = Interest.builder()
            .createdAt(baseTime)
            .name("AI")
            .subscriberCount(0L)
            .userInterests(new ArrayList<>())
            .build();
        ReflectionTestUtils.setField(interest, "updatedAt", baseTime);
        interest = interestRepository.save(interest);

        User user = User.builder()
            .createdAt(baseTime)
            .email("test@test.com")
            .nickname("dk")
            .password("testpassword")
            .isDeleted(false)
            .build();
        user = userRepository.save(user);

        UserInterest userInterest = UserInterest.builder()
            .user(user)
            .interest(interest)
            .createdAt(baseTime)
            .build();
        ReflectionTestUtils.setField(userInterest, "updatedAt", baseTime);
        interest.getUserInterests().add(userInterest);
        userInterestRepository.save(userInterest);

        // when
        interestRepository.delete(interest);

        // then
        assertThat(userInterestRepository.count()).isEqualTo(0L);
    }

    @Test
    void 동일한_관심사_이름_있는지_확인한다() throws Exception {
        // when
        boolean result = interestRepository.existsById(interestA.getId());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 관심사_아이디로_관심사를_삭제한다() throws Exception {
        // when
        interestRepository.deleteById(interestA.getId());

        // then
        assertThat(interestRepository.count()).isEqualTo(2L);
    }

    // TODO 관심사 수정 기능
}