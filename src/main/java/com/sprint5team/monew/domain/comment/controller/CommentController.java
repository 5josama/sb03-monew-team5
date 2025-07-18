package com.sprint5team.monew.domain.comment.controller;

import com.sprint5team.monew.domain.comment.dto.CommentDto;
import com.sprint5team.monew.domain.comment.dto.CommentRegisterRequest;
import com.sprint5team.monew.domain.comment.dto.CommentUpdateRequest;
import com.sprint5team.monew.domain.comment.dto.CursorPageResponseCommentDto;
import com.sprint5team.monew.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController implements CommentApi{

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> create(@RequestBody @Valid CommentRegisterRequest request) {
        log.info("댓글 생성 요청: {}",request);
        CommentDto createdComment = commentService.create(request);
        log.debug("댓글 생성 응답: {}",createdComment);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdComment);
    }

    @GetMapping
    public ResponseEntity<CursorPageResponseCommentDto> find(
            @RequestParam(required = false) UUID articleId,
            @RequestParam String orderBy,
            @RequestParam String direction,
            @RequestParam(required = false) String cursor,
            @RequestParam Integer limit,
            @RequestParam(required = false) Instant after,
            @RequestHeader("MoNew-Request-User-ID") UUID userId
    ) {
        log.info("댓글 조회 요청: 기사 ID={}, 요청자 ID={}, 커서={}",articleId, userId, cursor);
        Pageable pageable = PageRequest.of(0, limit+1, Sort.Direction.valueOf(direction), orderBy, "createdAt");
        CursorPageResponseCommentDto response = commentService.find(articleId,cursor,after,pageable);
        log.debug("댓글 조회 응답: {}",response);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Override
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> softDelete(
            @PathVariable UUID commentId) {
        log.info("댓글 논리 삭제 요청: 댓글 ID = {}", commentId);
        commentService.softDelete(commentId);
        log.debug("댓글 논리 삭제 완료");

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build() ;
    }

    @Override
    @DeleteMapping("/{commentId}/hard")
    public ResponseEntity<Void> hardDelete(
            @PathVariable UUID commentId) {
        log.info("댓글 물리 삭제 요청: 댓글 ID = {}", commentId);
        commentService.hardDelete(commentId);
        log.debug("댓글 물리 삭제 완료");

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build() ;
    }

    @Override
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> update(
            @PathVariable UUID commentId
            ,@RequestHeader("Monew-Request-User-ID") UUID userId
            , @Valid @RequestBody CommentUpdateRequest request) {
        log.info("댓글 수정 요청: 댓글ID = {}, 내용 = {}", commentId, request.content());
        CommentDto response = commentService.update(commentId, request);
        log.debug("댓글 수정 완료: 결과 = {}",response);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
