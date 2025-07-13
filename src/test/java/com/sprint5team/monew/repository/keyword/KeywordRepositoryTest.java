package com.sprint5team.monew.repository.keyword;

import com.sprint5team.monew.base.config.QuerydslConfig;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.interest.repository.InterestRepositoryImpl;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import org.hibernate.validator.internal.metadata.aggregated.FieldCascadable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PackageName  : com.sprint5team.monew.domain.keyword.repository
 * FileName     : KeywordRepositoryTest
 * Author       : dounguk
 * Date         : 2025. 7. 11.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import({QuerydslConfig.class})
@DisplayName("Keyword Repository 슬라이스 테스트")
class KeywordRepositoryTest {

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private InterestRepository interestRepository;


    private Interest globalInterest;

    @BeforeEach
    void setUp() {
        globalInterest = Interest.builder()
            .createdAt(Instant.now())
            .name("주류")
            .subscriberCount(5L)
            .build();
        interestRepository.save(globalInterest);

    }

    @Test
    void 관심사id를_통해_모든_키워드의_이름을_가져온다() throws Exception {
        // given
        Keyword keyword1 = Keyword.builder()
            .name("Bladnoch")
            .interest(globalInterest)
            .createdAt(Instant.now())
            .build();

        keywordRepository.save(keyword1);

        Keyword keyword2 = Keyword.builder()
            .name("Kilkerran")
            .interest(globalInterest)
            .createdAt(Instant.now())
            .build();
        keywordRepository.save(keyword2);


        // when
        Set<Keyword> keywords = keywordRepository.findAllByInterest(globalInterest);
        Set<String> keywordNames = keywords.stream().map(Keyword::getName).collect(Collectors.toSet());

        // then
        assertThat(keywords).hasSize(2);
        assertThat(keywordNames).contains("Bladnoch", "Kilkerran");
    }
}