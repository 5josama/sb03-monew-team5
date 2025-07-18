package com.sprint5team.monew.controller.userinterest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint5team.monew.domain.interest.exception.InterestNotExistException;
import com.sprint5team.monew.domain.userinterest.controller.UserInterestController;
import com.sprint5team.monew.domain.userinterest.dto.SubscriptionDto;
import com.sprint5team.monew.domain.userinterest.service.UserInterestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PackageName  : com.sprint5team.monew.controller.userinterest
 * FileName     : UserInterestControllerTest
 * Author       : dounguk
 * Date         : 2025. 7. 18.
 */
@WebMvcTest(controllers = UserInterestController.class)
@DisplayName("User Interest Controller 슬라이스 테스트")
public class UserInterestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserInterestService userInterestService;

    private Instant baseTime = Instant.now();

    @Test
    void 모든_파라미터를_포함한_관심사_구독_응답이_정상적으로_동작한다() throws Exception {
        // given
        UUID interestId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID subscriptionId = UUID.randomUUID();

        SubscriptionDto response = SubscriptionDto.builder()
            .id(subscriptionId)
            .interestId(interestId)
            .interestName("내가 좋아하는 아이돌 리스트")
            .interestKeywords(List.of("헌트릭스","사자보이즈","아일릿","NJZ"))
            .interestSubscriberCount(100L)
            .createdAt(baseTime)
            .build();

        given(userInterestService.registerSubscription(interestId, userId))
            .willReturn(response);

        // when
        mockMvc.perform(post("/api/interest/{interestId}/subscriptions", interestId)
                .header("Monew-Request-User-ID", userId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(interestId.toString()))
            .andExpect(jsonPath("$.interestId").value(response.interestId().toString()))
            .andExpect(jsonPath("$.interestName").value(response.interestName()))
            .andExpect(jsonPath("$.interestKeywords.size()").value(4))
            .andExpect(jsonPath("$.interestSubscriberCount").value(100L));
    }
    @Test
    void 관심사_정보가_없으면_InterestNotExistException_404_를_응답한다() throws Exception {
        // given
        UUID interestId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        given(userInterestService.registerSubscription(interestId, userId))
            .willThrow(InterestNotExistException.class);

        // when
        MvcResult result = mockMvc.perform(post("/api/interest/{interestId}/subscriptions", interestId)
                .header("Monew-Request-User-ID", userId.toString()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.details").value("입력된 관심사 아이디와 일치하는 관심사가 없습니다."))
            .andReturn();

        Exception exception = result.getResolvedException();

        assertThat(exception).isInstanceOf(InterestNotExistException.class);
    }


}
