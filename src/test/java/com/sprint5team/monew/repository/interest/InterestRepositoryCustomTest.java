package com.sprint5team.monew.repository.interest;

import com.sprint5team.monew.domain.interest.dto.CursorPageRequest;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.interest.repository.InterestRepositoryCustom;
import com.sprint5team.monew.domain.interest.service.InterestService;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PackageName  : com.sprint5team.monew.repository.interest
 * FileName     : InterestRepositoryCustomTest
 * Author       : dounguk
 * Date         : 2025. 7. 13.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Interest Repository 슬라이스 테스트")
public class InterestRepositoryCustomTest {
    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    Interest interestA;
    Interest interestB;
    Interest interestC;
    Instant createdAt = Instant.now();
    
    @BeforeEach
    public void setup() {
        LocalDateTime now = LocalDateTime.ofInstant(createdAt, ZoneId.systemDefault());

        interestA = Interest.builder()
            .name("다큐멘터리")
            .subscriberCount(50L)
            .createdAt(createdAt.minus(Duration.ofMinutes(10)))
            .build();
        interestRepository.save(interestA);
        interestB = Interest.builder()
            .name("게임")
            .subscriberCount(200L)
            .createdAt(createdAt.minus(Duration.ofMinutes(20)))
            .build();
        interestRepository.save(interestB);
        interestC = Interest.builder()
            .name("스포츠")
            .subscriberCount(100L)
            .createdAt(createdAt.minus(Duration.ofMinutes(5)))
            .build();
        interestRepository.save(interestC);
    }

    @Test
    void 이름으로_정렬한다() throws Exception {
        // given
        Keyword keywordEntity = new Keyword(createdAt,"스포츠",interestB);
        keywordRepository.save(keywordEntity);


        String keyword = "스포츠";
        String orderBy = "name";
        String direction = "asc";
        String cursor = null;
        Instant after = null;
        Integer limit = 10;
        UUID userId = UUID.randomUUID();

        List<Interest> sortedInterest = List.of(interestB, interestC);

        CursorPageRequest request = new CursorPageRequest(keyword, orderBy, direction, cursor, after, limit, userId);
        // when
        List<Interest> result = interestRepository.findAllInterestByRequest(request);

        // then
        assertThat(result)
            .isNotNull()
            .hasSize(1)
            .containsExactlyElementsOf(sortedInterest);

        keywordRepository.delete(keywordEntity);
    }

    @Test
    void 구독자수로_정렬한다() throws Exception {
        // given
        String keyword = null;
        String orderBy = "subscriberCount";
        String direction = "asc";
        String cursor = null;
        Instant after = null;
        Integer limit = 10;
        UUID userId = UUID.randomUUID();

        List<Interest> sortedInterest = List.of(interestA, interestC, interestB);

        CursorPageRequest request = new CursorPageRequest(keyword, orderBy, direction, cursor, after, limit, userId);
        // when
        List<Interest> result = interestRepository.findAllInterestByRequest(request);

        // then
        assertThat(result)
            .isNotNull()
            .hasSize(3)
            .containsExactlyElementsOf(sortedInterest);
    }

    @Test
    void 내림차순_정렬한다() throws Exception {
        // given
        String keyword = null;
        String orderBy = "name";
        String direction = "desc";
        String cursor = null;
        Instant after = null;
        Integer limit = 10;
        UUID userId = UUID.randomUUID();

        List<Interest> sortedInterest = List.of(interestC, interestA, interestB);

        CursorPageRequest request = new CursorPageRequest(keyword, orderBy, direction, cursor, after, limit, userId);
        // when
        List<Interest> result = interestRepository.findAllInterestByRequest(request);

        // then
        assertThat(result)
            .isNotNull()
            .hasSize(3)
            .containsExactlyElementsOf(sortedInterest);
    }

    @Test
    void 커서값을_기준으로_조회한다() throws Exception {
        // given
        Interest interestD = Interest.builder()
            .name("낚시")
            .subscriberCount(200L)
            .createdAt(createdAt.minus(Duration.ofMinutes(20)))
            .build();
        interestRepository.save(interestD);
        
        String keyword = null;
        String orderBy = "name";
        String direction = "asc";
        String cursor = null;
        Instant after = null;
        Integer limit = 10;
        UUID userId = UUID.randomUUID();

        List<Interest> sortedInterest = List.of(interestD, interestA, interestB);

        CursorPageRequest request = new CursorPageRequest(keyword, orderBy, direction, cursor, after, limit, userId);
        // when
        List<Interest> result = interestRepository.findAllInterestByRequest(request);

        // then
        assertThat(result)
            .isNotNull()
            .hasSize(3)
            .containsExactlyElementsOf(sortedInterest);
    }

    @Test
    void 같은_커서값을_가진_객체가_여러개일_경우_보조커서_기준으로_조회한다() throws Exception {
        Interest interestA = Interest.builder()
            .name("스포츠")
            .subscriberCount(50L)
            .createdAt(createdAt.minus(Duration.ofMinutes(10)))
            .build();
        interestRepository.save(interestA);
        Interest interestB = Interest.builder()
            .name("스포츠")
            .subscriberCount(200L)
            .createdAt(createdAt.minus(Duration.ofMinutes(20)))
            .build();
        interestRepository.save(interestB);
        Interest interestC = Interest.builder()
            .name("스포츠")
            .subscriberCount(100L)
            .createdAt(createdAt.minus(Duration.ofMinutes(5)))
            .build();
        interestRepository.save(interestC);
        
        String keyword = null;
        String orderBy = "name";
        String direction = "asc";
        String cursor = "스포츠";
        Instant after = createdAt.minus(Duration.ofMinutes(5));
        Integer limit = 10;
        UUID userId = UUID.randomUUID();

        List<Interest> sortedInterest = List.of(interestA, interestC);

        CursorPageRequest request = new CursorPageRequest(keyword, orderBy, direction, cursor, after, limit, userId);
        // when
        List<Interest> result = interestRepository.findAllInterestByRequest(request);

        // then
        assertThat(result)
            .isNotNull()
            .hasSize(2)
            .containsExactlyElementsOf(sortedInterest);
    }

    @Test
    void 커서_페이지_크기로_조회한다() throws Exception {
        // given
        String keyword = null;
        String orderBy = "subscriberCount";
        String direction = "asc";
        String cursor = null;
        Instant after = null;
        Integer limit = 2;
        UUID userId = UUID.randomUUID();

        List<Interest> sortedInterest = List.of(interestA, interestC);

        CursorPageRequest request = new CursorPageRequest(keyword, orderBy, direction, cursor, after, limit, userId);
        // when
        List<Interest> result = interestRepository.findAllInterestByRequest(request);

        // then
        assertThat(result)
            .isNotNull()
            .hasSize(2)
            .containsExactlyElementsOf(sortedInterest);
    }
}
