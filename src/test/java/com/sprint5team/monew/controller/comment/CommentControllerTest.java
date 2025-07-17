package com.sprint5team.monew.controller.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint5team.monew.domain.comment.controller.CommentController;
import com.sprint5team.monew.domain.comment.dto.CommentDto;
import com.sprint5team.monew.domain.comment.dto.CommentRegisterRequest;
import com.sprint5team.monew.domain.comment.dto.CursorPageResponseCommentDto;
import com.sprint5team.monew.domain.comment.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@DisplayName("댓글 Controller 단위 테스트")
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
                .andExpect(jsonPath("$.userNickname").value("UserNickname"))
                .andExpect(jsonPath("$.content").value("테스트 댓글 입니다."))
                .andExpect(jsonPath("$.likeCount").value(0))
                .andExpect(jsonPath("$.likedByMe").value(false));

    }

    @Test
    public void 댓글등록_실패_내용_길이초과() throws Exception {
        //Given
        UUID articleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String longContent = "a".repeat(501); // 500자 초과

        CommentRegisterRequest commentRegisterRequest = new CommentRegisterRequest(articleId, userId, longContent);

        //When && Then
        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRegisterRequest)))
                .andExpect(status().isBadRequest());
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
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 댓글_조회_성공() throws Exception {
        //given
        UUID articleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant cursor = Instant.now();
        Pageable pageable = PageRequest.of(0, 50, Sort.Direction.DESC, "createdAt");

        CommentDto commentDto1 = new CommentDto(
                UUID.randomUUID(),
                articleId,
                userId,
                "testName1",
                "테스트댓글1",
                1L,
                false,
                cursor.minusSeconds(10)
        );

        CommentDto commentDto2 = new CommentDto(
                UUID.randomUUID(),
                articleId,
                userId,
                "testName2",
                "테스트댓글2",
                2L,
                false,
                cursor.minusSeconds(20)
        );

        CommentDto commentDto3 = new CommentDto(
                UUID.randomUUID(),
                articleId,
                userId,
                "testName3",
                "테스트댓글3",
                3L,
                false,
                cursor.minusSeconds(30)
        );

        List<CommentDto> commentDtos = Arrays.asList(commentDto1, commentDto2, commentDto3);

        CursorPageResponseCommentDto response = new CursorPageResponseCommentDto(
                commentDtos,
                null,
                null,
                10,
                3L,
                false
        );

        given(commentService.find(eq(articleId), any(), any() ,any(Pageable.class))).willReturn(response);

        //when && then
        mockMvc.perform(get("/api/comments")
                        .param("orderBy","createdAt")
                        .param("direction","DESC")
                        .param("limit","10")
                        .param("articleId",articleId.toString())
                        .param("cursor",cursor.toString())
                        .param("userId",userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.nextCursor").isEmpty())
                .andExpect(jsonPath("$.nextAfter").isEmpty())
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.hasNext").value(false));


    }

    @Test
    void 댓글_논리_삭제_성공() throws Exception {
        //given
        UUID commentId = UUID.randomUUID();
        willDoNothing().given(commentService).softDelete(eq(commentId));

        //when && then
        mockMvc.perform(delete("/api/comments/{commentId}")
                .param("commentId", commentId.toString()))
                .andExpect(status().isNoContent());
    }


}
