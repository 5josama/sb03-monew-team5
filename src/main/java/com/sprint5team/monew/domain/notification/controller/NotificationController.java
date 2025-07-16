package com.sprint5team.monew.domain.notification.controller;


import com.sprint5team.monew.domain.notification.dto.CursorPageResponseNotificationDto;
import com.sprint5team.monew.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<CursorPageResponseNotificationDto> findAllNotConfirmed(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Instant after,
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader("Monew-Request-User-ID") UUID userId
    ) {
        CursorPageResponseNotificationDto response =  notificationService.getAllNotifications(userId, cursor, after, limit);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/{notificationId}")
    public ResponseEntity<Void> confirmNotification(
            @PathVariable UUID notificationId,
            @RequestHeader("Monew-Request-User-ID") UUID userId) {

       return null;
    }

    @PatchMapping()
    public ResponseEntity<Void> confirmAllNotifications(
            @RequestHeader("Monew-Request-User-ID") UUID userId) {

        return null;
    }
}