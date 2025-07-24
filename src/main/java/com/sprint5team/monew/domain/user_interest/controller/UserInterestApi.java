package com.sprint5team.monew.domain.user_interest.controller;

import com.sprint5team.monew.base.exception.ErrorResponse;
import com.sprint5team.monew.domain.interest.dto.CursorPageResponseInterestDto;
import com.sprint5team.monew.domain.user_interest.dto.SubscriptionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

/**
 * PackageName  : com.sprint5team.monew.domain.user_interest.controller
 * FileName     : UserInterestApi
 * Author       : dounguk
 * Date         : 2025. 7. 24.
 */
@Tag(name = "관심사 관리", description = "관심사 관련 API")
public interface UserInterestApi {

    @Operation(summary = "관심사 구독", description = "관심사를 구독합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "구독 성공",
            content = @Content(
                mediaType = "*/*",
                array = @ArraySchema(schema = @Schema(implementation = CursorPageResponseInterestDto.class))
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
    @PostMapping
    ResponseEntity<SubscriptionDto> registerSubscription(
        @Parameter(description = "검색어(관심사 이름, 키워드)") UUID interestId,
        @Parameter(description = "요청자 ID") @RequestHeader(name = "Monew-Request-User-ID") UUID userId
    );



    @Operation(summary = "관심사 구독 취소", description = "관심사를 구독을 취소합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "구독 취소 성공"
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
    ResponseEntity<Void> unfollowInterest(
        @Parameter(description = "검색어(관심사 이름, 키워드)") UUID interestId,
        @Parameter(description = "요청자 ID") @RequestHeader(name = "Monew-Request-User-ID") UUID userId
    );



}
