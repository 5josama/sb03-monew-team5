package com.sprint5team.monew.repository.keyword;

import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;

/**
 * PackageName  : com.sprint5team.monew.domain.keyword.repository
 * FileName     : KeywordRepositoryTest
 * Author       : dounguk
 * Date         : 2025. 7. 11.
 */
@DataJpaTest
@TestPropertySource(properties = {"spring.sql.init.mode=never"})
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

        // when
//        keywordRepository.findAllNameByInterest(globalInterest);

        // then

    }

}