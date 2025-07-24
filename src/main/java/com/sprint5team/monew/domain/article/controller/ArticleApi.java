package com.sprint5team.monew.domain.article.controller;

import com.sprint5team.monew.domain.article.dto.ArticleDto;
import com.sprint5team.monew.domain.article.dto.ArticleRestoreResultDto;
import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.article.dto.CursorPageResponseArticleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ArticleApi {
    
    @Operation(summary = "뉴스 기사 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ArticleDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (정렬 기준 오류, 페이지네이션 파라미터 오류 등)",
                    content = @Content(schema = @Schema(implementation = ArticleDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ArticleDto.class)))
    })
    ResponseEntity<CursorPageResponseArticleDto> getArticles(
            @Parameter(description = "검색어(제목, 요약)") String keyword,
            @Parameter(description = "관심사 ID") UUID interestId,
            @Parameter(description = "출처(포함)") List<String> sourceIn,
            @Parameter(description = "날짜 시작(범위)") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime publishDateFrom,
            @Parameter(description = "날짜 끝(범위)") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime publishDateTo,
            @Parameter(description = "정렬 속성") String orderBy,
            @Parameter(description = "정렬 방향") String direction,
            @Parameter(description = "커서 값") String cursor,
            @Parameter(description = "보조 커서") Instant after,
            @Parameter(description = "커서 페이지 크기") int limit,
            @Parameter(description = "요청자 ID") UUID userId
    );

    @Operation(summary = "기사 뷰 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "기사 뷰 등록 성공",
                    content = @Content(schema = @Schema(implementation = ArticleViewDto.class))),
            @ApiResponse(responseCode = "400", description = "댓글 정보 없음",
                    content = @Content(schema = @Schema(implementation = ArticleViewDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ArticleViewDto.class)))
    })
    public ResponseEntity<ArticleViewDto> createArticleView(
            @Parameter(description = "기사 ID") UUID articleId,
            @Parameter(description = "요청자 ID") UUID userId
    );

    @Operation(summary = "출처 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(type = "string", example = "NAVER"))
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<String>> getSources();

    @Operation(summary = "뉴스 복구")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "복구 성공",
                    content = @Content(schema = @Schema(implementation = ArticleRestoreResultDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ArticleRestoreResultDto.class)))
    })
    public ResponseEntity<ArticleRestoreResultDto> restoreArticle(
            @Parameter(description = "날짜 시작(범위)") Instant publishDateFrom,
            @Parameter(description = "날짜 끝(범위)") Instant publishDateTo
    );

    @Operation(summary = "뉴스 논리 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "논리 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "뉴스 기사 정보 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Void> softDeleteArticle(
            @Parameter(description = "뉴스 기사 ID") UUID articleId
    );

    @Operation(summary = "뉴스 물리 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "논리 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "뉴스 기사 정보 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Void> hardDeleteArticle(
            @Parameter(description = "뉴스 기사 ID") UUID articleId
    );
}
