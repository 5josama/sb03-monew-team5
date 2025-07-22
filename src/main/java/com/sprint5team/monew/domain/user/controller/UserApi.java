package com.sprint5team.monew.domain.user.controller;

import com.sprint5team.monew.domain.user.dto.UserDto;
import com.sprint5team.monew.domain.user.dto.UserLoginRequest;
import com.sprint5team.monew.domain.user.dto.UserRegisterRequest;
import com.sprint5team.monew.domain.user.dto.UserUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

@Tag(name = "사용자 관리", description = "사용자 관련 API")
public interface UserApi {

  @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "회원가입 성공",
      content = @Content(schema = @Schema(implementation = UserDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)",
      content = @Content(schema = @Schema(implementation = UserDto.class))),
      @ApiResponse(responseCode = "409", description = "이메일 중복",
      content = @Content(schema = @Schema(implementation = UserDto.class))),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류",
      content = @Content(schema = @Schema(implementation = UserDto.class)))
  })
  ResponseEntity<UserDto> register(UserRegisterRequest request);

  @Operation(summary = "로그인", description = "사용자 로그인을 처리합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "로그인 성공",
      content = @Content(schema = @Schema(implementation = UserDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)",
      content = @Content(schema = @Schema(implementation = UserDto.class))),
      @ApiResponse(responseCode = "401", description = "로그인 실패 (이메일 또는 비밀번호 불일치)",
      content = @Content(schema = @Schema(implementation = UserDto.class))),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류",
      content = @Content(schema = @Schema(implementation = UserDto.class)))
  })
  ResponseEntity<UserDto> login(UserLoginRequest request);

  @Operation(summary = "사용자 정보 수정", description = "사용자의 닉네임을 수정합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "사용자 정보 수정 성공",
      content = @Content(schema = @Schema(implementation = UserDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)",
      content = @Content(schema = @Schema(implementation = UserDto.class))),
      @ApiResponse(responseCode = "403", description = "사용자 정보 수정 권한 없음",
      content = @Content(schema = @Schema(implementation = UserDto.class))),
      @ApiResponse(responseCode = "404", description = "사용자 정보 없음",
      content = @Content(schema = @Schema(implementation = UserDto.class))),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류",
      content = @Content(schema = @Schema(implementation = UserDto.class)))
  })
  ResponseEntity<UserDto> update(
      @Parameter(description = "사용자 ID") UUID userId,
      UserUpdateRequest request);

  @Operation(summary = "사용자 논리 삭제", description = "사용자를 논리적으로 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "사용자 삭제 성공"),
      @ApiResponse(responseCode = "403", description = "사용자 삭제 권한 없음"),
      @ApiResponse(responseCode = "404", description = "사용자 정보 없음"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<Void> softDelete(
      @Parameter(description = "사용자 ID") UUID id);

  @Operation(summary = "사용자 물리 삭제", description = "사용자를 물리적으로 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "사용자 삭제 성공"),
      @ApiResponse(responseCode = "403", description = "사용자 삭제 권한 없음"),
      @ApiResponse(responseCode = "404", description = "사용자 정보 없음"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<Void> hardDelete(
      @Parameter(description = "사용자 ID") UUID userId);
}
