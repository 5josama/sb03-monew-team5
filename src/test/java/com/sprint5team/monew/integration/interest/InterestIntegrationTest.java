package com.sprint5team.monew.integration.interest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint5team.monew.domain.interest.dto.CursorPageRequest;
import com.sprint5team.monew.domain.interest.dto.CursorPageResponseInterestDto;
import com.sprint5team.monew.domain.interest.dto.InterestDto;
import com.sprint5team.monew.domain.interest.dto.InterestRegisterRequest;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.exception.InterestNotExistException;
import com.sprint5team.monew.domain.interest.exception.SimilarInterestException;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.interest.service.InterestService;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
//    "spring.sql.init.mode=never",
    "spring.datasource.driver-class-name=org.postgresql.Driver"
})
public class InterestIntegrationTest {

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
    private MockMvc mockMvc;

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private InterestService interestService;

    @Autowired
    ObjectMapper objectMapper;

    Interest interestA, interestB;
    Keyword keywordA, keywordB;

    @BeforeEach
    void setUp() {
        keywordRepository.deleteAll();
        interestRepository.deleteAll();

        interestA = Interest.builder()
            .name("collect old whiskey bottle")
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
            .name("old bottle 1")
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
        assertThat(result.content().get(0).name()).isEqualTo("브랜드");
        assertThat(result.content().get(0).keywords().get(0)).isEqualTo("apple");
        assertThat(result.content().get(1).name()).isEqualTo("collect old whiskey bottle");
        assertThat(result.content().get(1).keywords().get(0)).isEqualTo("old bottle 1");
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

    @Test
    void 관심사를_정상적으로_등록한다() throws Exception {
        // given
        InterestRegisterRequest request = new InterestRegisterRequest("백엔드 공부",List.of("JPA","Spring boot"));

        // when
        InterestDto result = interestService.registerInterest(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result).getClass().isAssignableFrom(InterestDto.class);
        assertThat(result.name()).isEqualTo(request.name());
        assertThat(result.keywords()).isEqualTo(request.keywords());
    }

    @Test
    void 관심사_이름의_유사도가_높을경우_SimilarInterestException_409_를_반환한다() throws Exception {
        // given
        InterestRegisterRequest request = new InterestRegisterRequest("collect old whisky bottle",List.of("keyword"));

        // when n then
        MvcResult result = mockMvc.perform(post("/api/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.message").value("Conflict"))
            .andExpect(jsonPath("$.details").value("이미 유사한 이름의 관심사가 있습니다."))
            .andReturn();

        Exception exception = result.getResolvedException();

        assertThat(exception).isInstanceOf(SimilarInterestException.class);

    }

    @Test
    void 잘못된_요청이_들어왔을때_MethodArgumentNotValidException_400_을_반환한다() throws Exception {
        // given
        InterestRegisterRequest request = new InterestRegisterRequest("invalid interest pneumonoultramicroscopicsilicovolcanoconiosis",List.of("keyword"));

        // when n then
        MvcResult result = mockMvc.perform(post("/api/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Bad Request"))
            .andExpect(jsonPath("$.details").value("name: size must be between 1 and 50"))
            .andReturn();

        Exception exception = result.getResolvedException();
        assertThat(exception).isInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    void 관심사ID로_관심사를_찾아_삭제할_수_있다() throws Exception {
        // given

        // when
        interestService.deleteInterest(interestA.getId());

        // then
        assertThat(interestRepository.count()).isEqualTo(1);
        assertThat(interestRepository.findById(interestA.getId())).isNotPresent();
    }

    @Test
    void 관심사를_찾지_못하면_InterestNotExistException_404_를_반환한다() throws Exception {
        // given
        UUID interestId = UUID.randomUUID();

        // when n then
        MvcResult result = mockMvc.perform(delete("/api/interests/{interestId}", interestId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").value("Not Found"))
            .andExpect(jsonPath("$.details").value("입력된 관심사 아이디와 일치하는 관심사가 없습니다."))
            .andReturn();

        Exception exception = result.getResolvedException();
        assertThat(exception).isInstanceOf(InterestNotExistException.class);
    }
}
