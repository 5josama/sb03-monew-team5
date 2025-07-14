package com.sprint5team.monew.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint5team.monew.domain.comment.dto.CommentDto;
import com.sprint5team.monew.domain.comment.dto.CommentRegisterRequest;
import com.sprint5team.monew.domain.comment.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @Test
    public void 댓글등록_성공_테스트() throws Exception {
        //Given
        UUID commentId = UUID.randomUUID();
        UUID articleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        CommentRegisterRequest commentRegisterRequest = new CommentRegisterRequest(articleId, userId, "테스트 댓글 입니다.");
        CommentDto createdComment = new CommentDto(
                commentId,
                articleId,
                userId,
                "UserNickname",
                commentRegisterRequest.content(),
                (long) 0,
                false,
                Instant.now()
        );

        given(commentService.create(any(CommentRegisterRequest.class))).willReturn(createdComment);

        //When && Then
        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRegisterRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.articleId").value(articleId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.userNickName").value("UserNickname"))
                .andExpect(jsonPath("$.content").value("테스트 댓글 입니다."))
                .andExpect(jsonPath("$.likeCount").value(0))
                .andExpect(jsonPath("$.likedByMe").value(false));

    }

    @Test
    public void 댓글등록_실패_내용_길이초과() throws Exception {
        //Given
        UUID articleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String longContent = "a".repeat(1001); // 1000자 초과

        CommentRegisterRequest commentRegisterRequest = new CommentRegisterRequest(articleId, userId, longContent);

        //When && Then
        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRegisterRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void 댓글등록_실패_빈_내용() throws Exception {
        //Given
        UUID articleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        CommentRegisterRequest commentRegisterRequest = new CommentRegisterRequest(articleId, userId, "");

        //When && Then
        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRegisterRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }


}
