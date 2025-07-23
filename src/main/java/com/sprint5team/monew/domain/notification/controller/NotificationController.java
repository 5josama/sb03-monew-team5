package com.sprint5team.monew.domain.notification.controller;


import com.sprint5team.monew.domain.notification.dto.CursorPageResponseNotificationDto;
import com.sprint5team.monew.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<CursorPageResponseNotificationDto> findAllNotConfirmed(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Instant after,
            @RequestParam Integer limit,
            @RequestHeader("Monew-Request-User-ID") UUID userId
    ) {
        log.info("알림 목록 조회 요청: userId={}, cursor={}, after={}, limit={}", userId, cursor, after, limit);
        CursorPageResponseNotificationDto response =  notificationService.getAllNotifications(userId, cursor, after, limit);

        log.debug("조회된 알림 수: {}", response.content().size());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/{notificationId}")
    public ResponseEntity<Void> confirmNotification(
            @PathVariable UUID notificationId,
            @RequestHeader("Monew-Request-User-ID") UUID userId) {
        log.info("단일 알림 확인 요청: userId={}, notificationId={}", userId, notificationId);

        notificationService.confirmNotification(notificationId, userId);
        log.debug("알림 확인 완료: notificationId={}", notificationId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PatchMapping()
    public ResponseEntity<Void> confirmAllNotifications(
            @RequestHeader("Monew-Request-User-ID") UUID userId) {
        log.info("전체 알림 확인 요청: userId={}", userId);

        notificationService.confirmAllNotifications(userId);
        log.debug("전체 알림 확인 완료: userId={}", userId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}