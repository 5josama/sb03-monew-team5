package com.sprint5team.monew.domain.comment.controller;

import com.sprint5team.monew.domain.comment.dto.CommentDto;
import com.sprint5team.monew.domain.comment.dto.CommentRegisterRequest;
import com.sprint5team.monew.domain.comment.dto.CommentUpdateRequest;
import com.sprint5team.monew.domain.comment.dto.CursorPageResponseCommentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.awt.print.Pageable;
import java.time.Instant;
import java.util.UUID;

@Tag(name="Comment", description="댓글 관련 API")
public interface CommentApi {

    @Operation(summary = "댓글 등록")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "201", description = "등록 성공",
                    content = @Content(schema = @Schema(implementation = CommentDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)",
                    content = @Content(schema = @Schema(implementation = CommentDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = CommentDto.class)))
    })
    ResponseEntity<CommentDto> create(
            @Parameter(description = "댓글 정보")CommentRegisterRequest request
    );

    @Operation(summary = "댓글 조회")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200",description = "조회 성공",
                    content = @Content(schema=@Schema(implementation = CursorPageResponseCommentDto.class))),
            @ApiResponse(responseCode = "400",description = "잘못된 요청 (정렬 기준 오류, 페이지네이션 파라미터 오류 등)",
                    content = @Content(schema=@Schema(implementation = CursorPageResponseCommentDto.class))),
            @ApiResponse(responseCode = "500",description = "서버 내부 오류",
                    content = @Content(schema=@Schema(implementation = CursorPageResponseCommentDto.class)))}
    )
    ResponseEntity<CursorPageResponseCommentDto> find(
            @Parameter(description = "기사 ID") UUID articleId,
            @Parameter(required = true, description = "정렬 속성 이름") String orderBy,
            @Parameter(required = true, description = "정렬 방향 (ASC, DESC)") String direction,
            @Parameter(description = "커서 값") String cursor,
            @Parameter(required = true, description = "커서 페이지 크기") Integer limit,
            @Parameter(description = "보조 커서(createdAt) 값")Instant after,
            @Parameter(description = "요청자 ID") UUID userId
            );


    @Operation(summary = "댓글 논리 삭제")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "204",description = "삭제 성공"),
            @ApiResponse(responseCode = "404",description = "댓글 정보 없음"),
            @ApiResponse(responseCode = "500",description = "서버 내부 오류")
    })
    ResponseEntity<Void> softDelete(
            @Parameter(required = true,description = "댓글 ID") UUID commentId
    );

    @Operation(summary = "댓글 물리 삭제")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "204",description = "삭제 성공"),
            @ApiResponse(responseCode = "404",description = "댓글 정보 없음"),
            @ApiResponse(responseCode = "500",description = "서버 내부 오류")
    })
    ResponseEntity<Void> hardDelete(
            @Parameter(required = true,description = "댓글 ID") UUID commentId
    );

    @Operation(summary = "댓글 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "수정 성공",
            content = @Content(schema = @Schema(implementation = CommentDto.class))),
            @ApiResponse(responseCode = "400",description = "잘못된 요청 (입력값 검증 실패)",
                    content = @Content(schema = @Schema(implementation = CommentDto.class))),
            @ApiResponse(responseCode = "404",description = "댓글 정보 없음",
                    content = @Content(schema = @Schema(implementation = CommentDto.class))),
            @ApiResponse(responseCode = "500",description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = CommentDto.class)))
    })
    ResponseEntity<CommentDto> update(
            @Parameter(required = true, description = "댓글 ID") UUID commentId,
            @Parameter(required = true,description = "요청자 ID") UUID userId,
            @RequestBody(required = true,description = "수정할 댓글 정보")CommentUpdateRequest request
            );

}
