package com.sprint5team.monew.domain.interest.controller;

import com.sprint5team.monew.base.exception.ErrorResponse;
import com.sprint5team.monew.domain.interest.dto.CursorPageResponseInterestDto;
import com.sprint5team.monew.domain.interest.dto.InterestDto;
import com.sprint5team.monew.domain.interest.dto.InterestRegisterRequest;
import com.sprint5team.monew.domain.keyword.dto.InterestUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

/**
 * PackageName  : com.sprint5team.monew.domain.interest.controller
 * FileName     : InterestApi
 * Author       : dounguk
 * Date         : 2025. 7. 24.
 */

@Tag(name = "관심사 관리", description = "관심사 관련 API")
public interface InterestApi {

    @Operation(summary = "관심사 목록 조회", description = "조건에 맞는 관심사 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "*/*",
                array = @ArraySchema(schema = @Schema(implementation = CursorPageResponseInterestDto.class))
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청(정렬 기준 오류, 페이지네이션 파라미터 오류 등)",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @GetMapping
    ResponseEntity<CursorPageResponseInterestDto> interestPaginationController(
        @Parameter(description = "검색어(관심사 이름, 키워드)") String keyword,
        @Parameter(description = "정렬 속성 이름") String orderBy,
        @Parameter(description = "정렬 방향(ASC,DESC)") String direction,
        @Parameter(description = "커서 값") String cursor,
        @Parameter(description = "보조 커서(createdAt) 값") Instant after,
        @Parameter(description = "커서 페이지 크기") Integer limit,
        @Parameter(description = "요청자 ID") @RequestHeader(name = "Monew-Request-User-ID") UUID userId
    );


    @Operation(summary = "관심사 등록", description = "새로운 관심사를 등록합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "등록 성공",
            content = @Content(
                mediaType = "*/*",
                array = @ArraySchema(schema = @Schema(implementation = InterestDto.class))
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (입력값 검증 실패)",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "유사 관심사 중복",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @PostMapping
    ResponseEntity<InterestDto> insertInterest(@Valid InterestRegisterRequest request);


    @Operation(summary = "관심사 물리 삭제", description = "관심사를 물리적으로 삭제합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "삭제 성공"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "관심사 정보 없음"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        ),
    })
    @DeleteMapping
    ResponseEntity<Void> deleteInterest(
        @Parameter(description = "관심사 ID") UUID interestId
    );

    @Operation(summary = "관심사 정보 수정", description = "관심사의 키워드를 수정합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = @Content(
                mediaType = "*/*",
                array = @ArraySchema(schema = @Schema(implementation = InterestDto.class))
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (입력값 검증 실패)",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "관심사 정보 없음",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @PatchMapping
    ResponseEntity<InterestDto> updateInterest(
        @PathVariable UUID interestId,
        InterestUpdateRequest request
    );
}
