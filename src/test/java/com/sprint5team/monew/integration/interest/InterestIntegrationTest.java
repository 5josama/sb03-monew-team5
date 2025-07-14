package com.sprint5team.monew.integration.interest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint5team.monew.domain.interest.dto.CursorPageRequest;
import com.sprint5team.monew.domain.interest.dto.CursorPageResponseInterestDto;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.interest.service.InterestService;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import com.sprint5team.monew.domain.user_interest.repository.UserInterestRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PackageName  : com.sprint5team.monew.integration.interest
 * FileName     : InterestIntegrationTest
 * Author       : dounguk
 * Date         : 2025. 7. 13.
 */
@DisplayName("Interest 통합 테스트")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class InterestIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private InterestService interestService;


    Interest interestA, interestB;
    Keyword keywordA, keywordB;
    @BeforeEach
    void setUp() {
        keywordRepository.deleteAll();
        interestRepository.deleteAll();

        interestA = Interest.builder()
            .name("os")
            .createdAt(Instant.now())
            .subscriberCount(0)
            .build();
        interestRepository.save(interestA);

        interestB = Interest.builder()
            .name("브랜드")
            .createdAt(Instant.now())
            .subscriberCount(0)
            .build();
        interestRepository.save(interestB);

        keywordA = Keyword.builder()
            .createdAt(Instant.now())
            .name("mac")
            .interest(interestA)
            .build();
        keywordRepository.save(keywordA);

        keywordB = Keyword.builder()
            .createdAt(Instant.now())
            .name("apple")
            .interest(interestB)
            .build();
        keywordRepository.save(keywordB);
    }


    @Test
    void 관심사_목록을_정상적으로_가져온다() throws Exception {
        // given
        CursorPageRequest validRequest = CursorPageRequest.builder()
            .orderBy("name")
            .direction("asc")
            .limit(10)
            .userId(UUID.randomUUID())
            .build();

        // when
        CursorPageResponseInterestDto result = interestService.generateCursorPage(validRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.content().size()).isEqualTo(2);
        assertThat(result.content().get(0).name()).isEqualTo("os");
        assertThat(result.content().get(0).keywords().get(0)).isEqualTo("mac");
        assertThat(result.content().get(1).name()).isEqualTo("브랜드");
        assertThat(result.content().get(1).keywords().get(0)).isEqualTo("apple");
        assertThat(result.nextCursor()).isNull();
        assertThat(result.nextAfter()).isNull();
        assertThat(result.totalElements()).isEqualTo(2);

    }

    @Test
    void 정렬_기준_오류가_있을경우_ConstraintViolationException_400_에러가_발생한다() throws Exception {
        // given
        MvcResult result = mockMvc.perform(get("/api/interests")
                .param("orderBy", "name")
                .param("direction", "invalid")
                .param("limit", "10")
                .header("monew-request-user-id", UUID.randomUUID().toString()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andReturn();

        Exception exception = result.getResolvedException();

        assertThat(exception).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void 페이지네이션_파라미터에_오류가_있을경우_ConstraintViolationException_400_에러가_발생한다() throws Exception {
        // given
        MvcResult result = mockMvc.perform(get("/api/interests")
                .param("orderBy", "invalid")
                .param("direction", "asc")
                .param("limit", "10")
                .header("monew-request-user-id", UUID.randomUUID().toString()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andReturn();

        Exception exception = result.getResolvedException();

        assertThat(exception).isInstanceOf(ConstraintViolationException.class);
    }

}
