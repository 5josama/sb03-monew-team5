package com.sprint5team.monew.domain.comment.controller;

import com.sprint5team.monew.domain.comment.dto.CommentDto;
import com.sprint5team.monew.domain.comment.dto.CommentRegisterRequest;
import com.sprint5team.monew.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
