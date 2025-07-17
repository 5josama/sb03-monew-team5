package com.sprint5team.monew.repository.interest;

import com.sprint5team.monew.base.config.QuerydslConfig;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.interest.repository.InterestRepositoryImpl;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.Instant;

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
    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    private Interest interestA, interestB, interestC;
    private final Instant baseTime = Instant.parse("2025-07-14T00:00:00Z");
    private double threshold = 0.8;

    @BeforeEach
    public void setup() {
        keywordRepository.deleteAll();
        interestRepository.deleteAll();

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
}
