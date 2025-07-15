package com.sprint5team.monew.controller.notification;

import com.sprint5team.monew.domain.notification.controller.NotificationController;
import com.sprint5team.monew.domain.notification.dto.CursorPageResponseNotificationDto;
import com.sprint5team.monew.domain.notification.dto.NotificationDto;
import com.sprint5team.monew.domain.notification.entity.ResourceType;
import com.sprint5team.monew.domain.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void 알림_목록_커서_기반_조회() throws Exception {
        // given
        Instant createdAt = Instant.now();
        String cursor = createdAt.toString();
        Instant after = createdAt;
        int limit = 10;

        NotificationDto dto = new NotificationDto(
                UUID.randomUUID(),
                createdAt,
                createdAt,
                false,
                userId,
                "테스트 알림 내용",
                ResourceType.COMMENT,
                UUID.randomUUID()
        );

        CursorPageResponseNotificationDto response = new CursorPageResponseNotificationDto(
                List.of(dto),
                cursor,
                createdAt,
                limit,
                100L,
                true
        );

        given(notificationService.getAllNotifications(any(), any(), any(), anyInt())).willReturn(response);

        // when, then
        mockMvc.perform(get("/api/notifications")
                        .header("Monew-Request-User-ID", userId.toString())
                        .param("cursor", cursor)
                        .param("after", after.toString())
                        .param("limit", String.valueOf(limit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").isNotEmpty())
                .andExpect(jsonPath("$.content[0].content").value("테스트 알림 내용"))
                .andExpect(jsonPath("$.size").value(limit))
                .andExpect(jsonPath("$.totalElements").value(100))
                .andExpect(jsonPath("$.hasNext").value(true));
    }
}