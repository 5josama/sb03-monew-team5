package com.sprint5team.monew.repository.interest;

import com.sprint5team.monew.base.config.QuerydslConfig;
import com.sprint5team.monew.domain.interest.dto.CursorPageRequest;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.interest.repository.InterestRepositoryImpl;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
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
@Import({InterestRepositoryImpl.class, QuerydslConfig.class})
@DisplayName("Interest Repository 슬라이스 테스트")
public class InterestRepositoryCustomTest {
    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    private Interest interestA, interestB, interestC;
    private final Instant baseTime = Instant.parse("2025-07-14T00:00:00Z");

    @BeforeEach
    public void setup() {

        keywordRepository.deleteAll();
        interestRepository.deleteAll();

        interestA = Interest.builder()
            .name("다큐멘터리")
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

    /**
     *  findAllInterestByRequest() test
     */
    @Test
    void 이름으로_정렬한다() throws Exception {
        // given
        Instant createdAt = Instant.now();
        Keyword keywordEntity = new Keyword(createdAt, "스포츠", interestB);
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
            .hasSize(sortedInterest.size())
            .containsExactlyElementsOf(sortedInterest);
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
            .hasSize(sortedInterest.size())
            .containsExactlyElementsOf(sortedInterest);
    }

    @Test
    void 오름차순_정렬한다() throws Exception {
        // given
        String keyword = null;
        String orderBy = "name";
        String direction = "asc";
        String cursor = null;
        Instant after = null;
        Integer limit = 10;
        UUID userId = UUID.randomUUID();

        List<Interest> sortedInterest = List.of(interestB, interestA, interestC);

        CursorPageRequest request = new CursorPageRequest(keyword, orderBy, direction, cursor, after, limit, userId);
        // when
        List<Interest> result = interestRepository.findAllInterestByRequest(request);

        // then
        assertThat(result)
            .isNotNull()
            .hasSize(sortedInterest.size())
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
            .hasSize(sortedInterest.size())
            .containsExactlyElementsOf(sortedInterest);
    }

    //    @Test
//    void 같은_커서값을_가진_객체가_여러개일_경우_보조커서_기준으로_조회한다_오름차순() throws Exception {
//        interestRepository.deleteAll();
//
//        Interest interestA1 = Interest.builder()
//            .name("sport")
//            .subscriberCount(50L)
//            .createdAt(baseTime.minus(Duration.ofMinutes(10)))
//            .build();
//        interestRepository.save(interestA1);
//        Interest interestB1 = Interest.builder()
//            .name("sport")
//            .subscriberCount(200L)
//            .createdAt(baseTime.minus(Duration.ofMinutes(20)))
//            .build();
//        interestRepository.save(interestB1);
//        Interest interestC1 = Interest.builder()
//            .name("sport")
//            .subscriberCount(100L)
//            .createdAt(baseTime.minus(Duration.ofMinutes(5)))
//            .build();
//        interestRepository.save(interestC1);
//
//        String keyword = null;
//        String orderBy = "name";
//        String direction = "asc";
//        String cursor = "sport";
//        Instant after = baseTime.minus(Duration.ofMinutes(15));
//        Integer limit = 10;
//        UUID userId = UUID.randomUUID();
//
//        List<Interest> sortedInterest = List.of(interestA1, interestC1);
//
//        CursorPageRequest request = new CursorPageRequest(keyword, orderBy, direction, cursor, after, limit, userId);
//        // when
//        List<Interest> result = interestRepository.findAllInterestByRequest(request);
//
//        // then
//        assertThat(result)
//            .isNotNull()
//            .hasSize(sortedInterest.size())
//            .containsExactlyElementsOf(sortedInterest);
//    }
    @Test
    void 같은_커서값을_가진_객체가_여러개일_경우_보조커서_기준으로_조회한다_오름차순() throws Exception {

        interestRepository.deleteAll();

        Interest interestB1 = Interest.builder()
            .name("스포츠")
            .subscriberCount(200L)
            .createdAt(baseTime.minus(Duration.ofMinutes(20)))
            .build();
        interestRepository.save(interestB1);

        Interest interestA1 = Interest.builder()
            .name("스포츠")
            .subscriberCount(50L)
            .createdAt(baseTime.minus(Duration.ofMinutes(10)))
            .build();
        interestRepository.save(interestA1);

        Interest interestC1 = Interest.builder()
            .name("스포츠")
            .subscriberCount(100L)
            .createdAt(baseTime.minus(Duration.ofMinutes(5)))
            .build();
        interestRepository.save(interestC1);

        String keyword = null;
        String orderBy = "name";
        String direction = "asc";
        String cursor = "스포츠";

        Instant after = interestB1.getCreatedAt();

        Integer limit = 10;
        UUID userId = UUID.randomUUID();

        List<Interest> sortedInterest = List.of(interestA1, interestC1);

        CursorPageRequest request = new CursorPageRequest(keyword, orderBy, direction, cursor, after, limit, userId);

        // when
        List<Interest> result = interestRepository.findAllInterestByRequest(request);

        // then
        assertThat(result)
            .isNotNull()
            .hasSize(sortedInterest.size())
            .containsExactlyElementsOf(sortedInterest);
    }

    /**
     * countTotalElements() test
     */
    @Test
    void 조건이_없으면_모든_관심사들의_수를_반환한다() throws Exception {
        // given
        String keyword = null;
        String orderBy = "name";
        String direction = "asc";
        String cursor = null;
        Instant after = null;
        Integer limit = 10;
        UUID userId = UUID.randomUUID();

        CursorPageRequest request = new CursorPageRequest(keyword, orderBy, direction, cursor, after, limit, userId);

        // when
        long result = interestRepository.countTotalElements(request);

        // then
        assertThat(result).isEqualTo(3);

    }

    @Test
    void 키워드가_interest_name에_포함될때_정확한_개수를_반환한다() throws Exception {
        // given
        /**
         * Interest: 다큐맨터리,게임,스포츠
         */
        String keyword = "게임";
        String orderBy = "name";
        String direction = "asc";
        String cursor = null;
        Instant after = null;
        Integer limit = 10;
        UUID userId = UUID.randomUUID();

        CursorPageRequest request = new CursorPageRequest(keyword, orderBy, direction, cursor, after, limit, userId);

        // when
        long result = interestRepository.countTotalElements(request);

        // then
        assertThat(result).isEqualTo(1);
    }

    @Test
    void 키워드가_keyword_name에_포함될때_정확한_개수를_반환한다() throws Exception {
        // given
        /**
         * Interest: 다큐맨터리, 게임(keyword:스포츠), 스포츠
         */
        Instant createdAt = Instant.now();
        Keyword keywordEntity = new Keyword(createdAt, "스포츠", interestB);
        keywordRepository.save(keywordEntity);

        String keyword = "스포츠";
        String orderBy = "name";
        String direction = "asc";
        String cursor = null;
        Instant after = null;
        Integer limit = 10;
        UUID userId = UUID.randomUUID();

        CursorPageRequest request = new CursorPageRequest(keyword, orderBy, direction, cursor, after, limit, userId);

        // when
        long result = interestRepository.countTotalElements(request);

        // then
        assertThat(result).isEqualTo(2);
    }

    @Test
    void 조건에_만족하는_관심사가_없을떄_0을_반환한다() throws Exception {
        // given
        /**
         * Interest: 다큐맨터리,게임,스포츠
         */
        String keyword = "피규어 수집";
        String orderBy = "name";
        String direction = "asc";
        String cursor = null;
        Instant after = null;
        Integer limit = 10;
        UUID userId = UUID.randomUUID();

        CursorPageRequest request = new CursorPageRequest(keyword, orderBy, direction, cursor, after, limit, userId);

        // when
        long result = interestRepository.countTotalElements(request);

        // then
        assertThat(result).isEqualTo(0);
    }

    @Test
    void 조건은_대소문자를_구분하지_않고_반환한다() throws Exception {
        interestRepository.deleteAll();

        // given
        Instant createdAt = Instant.now();
        Interest interestA1 = Interest.builder()
            .name("sport1234")
            .subscriberCount(50L)
            .createdAt(createdAt.minus(Duration.ofMinutes(10)))
            .build();
        interestRepository.save(interestA1);
        Interest interestB1 = Interest.builder()
            .name("SPORT4321")
            .subscriberCount(200L)
            .createdAt(createdAt.minus(Duration.ofMinutes(20)))
            .build();
        interestRepository.save(interestB1);
        Interest interestC1 = Interest.builder()
            .name("Sport1152")
            .subscriberCount(100L)
            .createdAt(createdAt.minus(Duration.ofMinutes(5)))
            .build();
        interestRepository.save(interestC1);
        Interest interestD1 = Interest.builder()
            .name("운동")
            .subscriberCount(100L)
            .createdAt(createdAt.minus(Duration.ofMinutes(5)))
            .build();
        interestRepository.save(interestD1);

        Keyword keywordEntity = new Keyword(createdAt, "SPORt", interestD1);
        keywordRepository.save(keywordEntity);

        String keyword = "sport";
        String orderBy = "name";
        String direction = "asc";
        String cursor = null;
        Instant after = null;
        Integer limit = 10;
        UUID userId = UUID.randomUUID();

        CursorPageRequest request = new CursorPageRequest(keyword, orderBy, direction, cursor, after, limit, userId);

        // when
        long result = interestRepository.countTotalElements(request);

        // then
        assertThat(result).isEqualTo(4);
    }

}
