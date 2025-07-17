package com.sprint5team.monew.controller.interest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint5team.monew.domain.interest.controller.InterestController;
import com.sprint5team.monew.domain.interest.dto.CursorPageRequest;
import com.sprint5team.monew.domain.interest.dto.CursorPageResponseInterestDto;
import com.sprint5team.monew.domain.interest.dto.InterestDto;
import com.sprint5team.monew.domain.interest.dto.InterestRegisterRequest;
import com.sprint5team.monew.domain.interest.service.InterestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.*;

/**
 * PackageName  : com.sprint5team.monew.interest.service
 * FileName     : InterestControllerTest
 * Author       : dounguk
 * Date         : 2025. 7. 11.
 */

@WebMvcTest(controllers = InterestController.class)
@DisplayName("Interest Controller 슬라이스 테스트")
public class InterestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InterestService interestService;


    @Test
    void 모든_파라미터를_포함한_커서_패이지_응답이_정상적으로_동작한다() throws Exception {
        // given
        UUID id = UUID.randomUUID();

        Instant createdAt = Instant.now();

        InterestDto interestDto1 = InterestDto.builder()
            .id(id)
            .name("스포츠")
            .keywords(List.of("야구", "축구","스포츠"))
            .subscriberCount(0L)
            .subscribedByMe(false)
            .build();

        InterestDto interestDto2 = InterestDto.builder()
            .id(id)
            .name("취미")
            .keywords(List.of("악기", "축구", "수집","스포츠"))
            .subscriberCount(0L)
            .subscribedByMe(false)
            .build();

        InterestDto interestDto3 = InterestDto.builder()
            .id(id)
            .name("공부")
            .keywords(List.of("수학", "영어","스포츠"))
            .subscriberCount(0L)
            .subscribedByMe(false)
            .build();
        List<InterestDto> interestDtos = Arrays.asList(interestDto1, interestDto2, interestDto3);

        CursorPageResponseInterestDto response = CursorPageResponseInterestDto.builder()
            .content(interestDtos)
            .nextCursor("취미")
            .nextAfter(createdAt)
            .size(3)
            .totalElements(3L)
            .hasNext(false)
            .build();

        given(interestService.generateCursorPage(any(CursorPageRequest.class))).willReturn(response);

        // when
        mockMvc.perform(get("/api/interests")
                .param("keyword", "축구")
                .param("orderBy", "name")
                .param("direction", "asc")
                .param("cursor", "강의")
                .param("after", Instant.now().toString())
                .param("limit", "3")
                .header("monew-request-user-id", id.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.nextCursor").value("취미"))
            .andExpect(jsonPath("$.nextAfter").value(createdAt.toString()))
            .andExpect(jsonPath("$.size").value(3))
            .andExpect(jsonPath("$.totalElements").value(3L))
            .andExpect(jsonPath("$.hasNext").value(false));

    }

    @Test
    void 필수_파라미터를_포함한_커서_패이지_응답이_정상적으로_동작한다() throws Exception {
        /// given
        UUID id = UUID.randomUUID();

        Instant createdAt = Instant.now();

        InterestDto interestDto1 = InterestDto.builder()
            .id(id)
            .name("스포츠")
            .keywords(List.of("야구", "축구","스포츠"))
            .subscriberCount(0L)
            .subscribedByMe(false)
            .build();

        InterestDto interestDto2 = InterestDto.builder()
            .id(id)
            .name("취미")
            .keywords(List.of("악기", "축구", "수집","스포츠"))
            .subscriberCount(0L)
            .subscribedByMe(false)
            .build();

        InterestDto interestDto3 = InterestDto.builder()
            .id(id)
            .name("공부")
            .keywords(List.of("수학", "영어","스포츠"))
            .subscriberCount(0L)
            .subscribedByMe(false)
            .build();
        List<InterestDto> interestDtos = Arrays.asList(interestDto1, interestDto2, interestDto3);

        CursorPageResponseInterestDto response = CursorPageResponseInterestDto.builder()
            .content(interestDtos)
            .nextCursor("취미")
            .nextAfter(createdAt)
            .size(3)
            .totalElements(3L)
            .hasNext(false)
            .build();

        given(interestService.generateCursorPage(any(CursorPageRequest.class))).willReturn(response);

        // when
        mockMvc.perform(get("/api/interests")
                .param("orderBy", "name")
                .param("direction", "asc")
                .param("limit", "3")
                .header("monew-request-user-id", id.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.nextCursor").value("취미"))
            .andExpect(jsonPath("$.nextAfter").value(createdAt.toString()))
            .andExpect(jsonPath("$.size").value(3))
            .andExpect(jsonPath("$.totalElements").value(3L))
            .andExpect(jsonPath("$.hasNext").value(false));
    }


    @Test
    void 파라미터가_정상입력_되어있을경우_새로운_관심사를_등록한다() throws Exception {
        // given
        InterestRegisterRequest validRequest = InterestRegisterRequest.builder()
            .name("재즈 아티스트")
            .keywords(List.of("존콜 트레인","아트 블래키", "찰리 파커"))
            .build();

        InterestDto response = InterestDto.builder()
            .id(UUID.randomUUID())
            .name("재즈 아티스트")
            .keywords(List.of("존콜 트레인", "아트 블래키", "찰리 파커"))
            .subscriberCount(0L)
            .subscribedByMe(false)
            .build();


        given(interestService.registerInterest(validRequest))
            .willReturn(response);

        // when n then
        mockMvc.perform(post("/api/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("재즈 아티스트"))
            .andExpect(jsonPath("$.subscriberCount").value(0L))
            .andExpect(jsonPath("$.subscribedByMe").value(false))
            .andExpect(jsonPath("$.keywords").isArray())
            .andExpect(jsonPath("$.keywords.size()").value(3));
    }

    @Test
    void 키워드_이름이_없을경우_관심사를_등록할_수_없다() throws Exception {
        // given
        InterestRegisterRequest invalidRequest = InterestRegisterRequest.builder()
            .name(null)
            .keywords(List.of("존콜 트레인","아트 블래키", "찰리 파커"))
            .build();

        // when n then
        mockMvc.perform(post("/api/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Bad Request"))
            .andExpect(jsonPath("$.details").value("name: must not be null"));
    }

    @Test
    void 관심사_단어_길이가_50을_넘을수_없다() throws Exception {
        // given
        InterestRegisterRequest invalidRequest = InterestRegisterRequest.builder()
            .name("Invalid-Keyword-pneumonoultramicroscopicsilicovolcanoconiosis")
            .keywords(List.of("존콜 트레인","아트 블래키", "찰리 파커"))
            .build();

        // when n then
        mockMvc.perform(post("/api/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Bad Request"))
            .andExpect(jsonPath("$.details").value("name: size must be between 1 and 50"));
    }

    @Test
    void 키워드_단어_길이가_20을_넘을수_없다() throws Exception {
        // given
        InterestRegisterRequest invalidRequest = InterestRegisterRequest.builder()
            .name("재즈 아티스트")
            .keywords(List.of("존콜 트레인","아트 블래키", "찰리 파커","pneumonoultramicroscopicsilicovolcanoconiosis"))
            .build();

        // when n then
        mockMvc.perform(post("/api/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Bad Request"))
            .andExpect(jsonPath("$.details", containsString("keywords")))
            .andExpect(jsonPath("$.details", containsString("size must be between 1 and 20")));
    }

    @Test
    void 키워드_수가_10개를_초과할수_없다() throws Exception {
        // given
        InterestRegisterRequest invalidRequest = InterestRegisterRequest.builder()
            .name("재즈 아티스트")
            .keywords(List.of("존콜 트레인","아트 블래키", "찰리 파커","조 빔"," 테네리오 주니오르", "스탄 게츠","리사 심슨", "오스카 피터슨", "챗 베이커","빌 에반스","마일스 데이비스"))
            .build();
        // when n then
        mockMvc.perform(post("/api/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Bad Request"))
            .andExpect(jsonPath("$.details").value("keywords: size must be between 1 and 10"));
    }



}
