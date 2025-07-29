package com.sprint5team.monew.domain.user.controller;

import com.sprint5team.monew.domain.user.dto.UserActivityDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

@Tag(name = "사용자 활동 내역 관리", description = "사용자 활동 내역 관련 API")
public interface UserActivityApi {

  @Operation(summary = "사용자 활동 내역 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "사용자 활동 내역 조회 성공",
      content = @Content(schema = @Schema(implementation = UserActivityDto.class))),
      @ApiResponse(responseCode = "404", description = "사용자 정보 없음",
      content = @Content(schema = @Schema(implementation = UserActivityDto.class))),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류",
      content = @Content(schema = @Schema(implementation = UserActivityDto.class)))
  })
  ResponseEntity<UserActivityDto> getUserActivity(
      @Parameter(description = "사용자 ID") UUID userId);
}
