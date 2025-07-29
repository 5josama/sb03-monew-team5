package com.sprint5team.monew.domain.user.controller;

import com.sprint5team.monew.domain.user.dto.UserActivityDto;
import com.sprint5team.monew.domain.user.service.UserActivityService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-activities")
public class UserActivityController implements UserActivityApi {

  private final UserActivityService userActivityService;

  @GetMapping("/{userId}")
  public ResponseEntity<UserActivityDto> getUserActivity(
      @PathVariable("userId") UUID userId
  ) {
    UserActivityDto userActivity = userActivityService.getUserActivity(userId);
    return ResponseEntity
        .ok()
        .body(userActivity);
  }
}
