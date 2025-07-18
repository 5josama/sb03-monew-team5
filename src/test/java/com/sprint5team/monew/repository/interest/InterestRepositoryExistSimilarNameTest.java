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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PackageName  : com.sprint5team.monew.repository.interest
 * FileName     : InterestRepositoryExistSimilarNameTest
 * Author       : dounguk
 * Date         : 2025. 7. 15.
 */
@Testcontainers
@DataJpaTest
@DisplayName("InterestRepository existSimilarName() 테스트")
@Import({InterestRepositoryImpl.class, QuerydslConfig.class})
public class InterestRepositoryExistSimilarNameTest {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:17")
            .withInitScript("sql/init_pg_trgm.sql");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry reg) {
        reg.add("spring.datasource.url",      postgres::getJdbcUrl);
        reg.add("spring.datasource.username", postgres::getUsername);
        reg.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    private Interest interestA, interestB, interestC;
    private final Instant baseTime = Instant.parse("2025-07-14T00:00:00Z");
    private double threshold = 0.75;


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
            .name("custom keyword1")
            .subscriberCount(200L)
            .createdAt(baseTime.minus(Duration.ofMinutes(20)))
            .build();
        interestRepository.save(interestB);
        interestC = Interest.builder()
            .name("compleexity")
            .subscriberCount(100L)
            .createdAt(baseTime.minus(Duration.ofMinutes(5)))
            .build();
        interestRepository.save(interestC);
        interestRepository.flush();
    }

    @Test
    void 관심사_이름이_약80퍼센트_이상_일치하면_true_를_반환한다() throws Exception {
        // when
        boolean result = interestRepository.existsSimilarName("custom keyword2", threshold);
        // then
        assertThat(result).isTrue();
    }


    @Test
    void 관심사_이름이_약80퍼센트_미만_일치하면_false를_반환한다() throws Exception {
        // when
        boolean result = interestRepository.existsSimilarName("complex", threshold);
        // then
        assertThat(result).isFalse();

    }

    @Test
    void 대소문자는_확인하지_않고_일치율을_확인한다_true() throws Exception {
        // when
        boolean result = interestRepository.existsSimilarName("deep learning", threshold);
        // then
        assertThat(result).isTrue();

    }

}
