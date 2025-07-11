package com.sprint5team.monew.interest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint5team.monew.domain.interest.controller.InterestController;
import com.sprint5team.monew.domain.interest.dto.CursorPageResponseInterestDto;
import com.sprint5team.monew.domain.interest.dto.InterestDto;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.service.InterestService;
import com.sprint5team.monew.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();
        UUID id4 = UUID.randomUUID();

        Instant createdAt = Instant.now();
//        User user = new User("test@test.com", "user1", "password1");
//        ReflectionTestUtils.setField(interestService, "id", id);

        Interest interest1 = Interest.builder()
            .name("스포츠")
            .subscriberCount(0L)
            .build();
        ReflectionTestUtils.setField(interest1, "createdAt", createdAt);

        Interest interest2 = Interest.builder()
            .name("취미")
            .subscriberCount(0L)
            .build();
        ReflectionTestUtils.setField(interest2, "createdAt", createdAt);
        List<Interest> interests = Arrays.asList(interest1, interest2);

        CursorPageRequest cursorPageRequest = new CursorPageRequest("축구", "name", "asc", "공부", "createdAt", "3", id4);


        InterestDto interestDto1 = InterestDto.builder()
            .id(id1)
            .name("스포츠")
            .keywords(List.of("야구","축구"))
            .subscriberCount(0L)
            .subscribedByMe(false)
            .build();

        InterestDto interestDto2 = InterestDto.builder()
            .id(id2)
            .name("취미")
            .keywords(List.of("악기","축구","수집"))
            .subscriberCount(0L)
            .subscribedByMe(false)
            .build();

        InterestDto interestDto3 = InterestDto.builder()
            .id(id3)
            .name("공부")
            .keywords(List.of("수학","영어"))
            .subscriberCount(0L)
            .subscribedByMe(false)
            .build();
        List<InterestDto> interestDtos = Arrays.asList(interestDto1, interestDto2);


        CursorPageResponseInterestDto response = CursorPageResponseInterestDto.builder()
            .content(interestDtos)
            .nextCursor("공부")
            .nextAfter(createdAt)
            .size(3)
            .totalElements(3)
            .hasNext(false)
            .build();

        given(interestService.generateCursorPage(any())).willReturn(response);

        // when
        mockMvc.perform(post("/api/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.type").value(ChannelType.PRIVATE.name()))
            .andExpect(jsonPath("$.name").value(""))
            .andExpect(jsonPath("$.description").value(""))
            .andExpect(jsonPath("$.participants.size()").value(2))
            .andExpect(jsonPath("$.participants").isArray())
            .andExpect(jsonPath("$.lastMessageAt").value(lastMessageAt.toString()));

        // then

    }

    @Test
    void 필수_파라미터를_포함한_커서_패이지_응답이_정상적으로_동작한다() throws Exception {
        // given

        // when

        // then

    }

}
