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

/**
 * 알림 관련 API를 제공하는 컨트롤러
 * 알림 목록 조회
 * 알림 확인
 * 전체 알림 확인
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@Slf4j
public class NotificationController implements NotificationApi{

    private final NotificationService notificationService;

    /**
     * 미확인 알림 목록을 시간순정렬(ASC) 커서 기반으로 조회한다
     *
     * @param cursor 마지막으로 받은 알림의 커서 (nullable)
     * @param after 특정 시간 이후의 알림만 조회 (nullable)
     * @param limit 한 번에 조회할 최대 알림 수
     * @param userId 요청 유저의 ID (헤더)
     * @return 페이징된 알림 목록
     */
    @Override
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

    /**
     * 특정 알림을 확인한다
     * 알림 ID와 사용자 ID를 기반으로 해당 알림을 읽음 처리(confirmed = true)
     *
     * @param notificationId 확인할 알림의 ID (PathVariable)
     * @param userId 요청 사용자 ID (헤더: Monew-Request-User-ID)
     * @return HTTP 204 No Content (성공 시 본문 없음)
     */
    @Override
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

    /**
     * 해당 사용자의 모든 미확인 알림을 일괄 확인한다
     * confirmed = false 상태인 알림들을 모두 true로 업데이트
     *
     * @param userId 요청 사용자 ID (헤더: Monew-Request-User-ID)
     * @return HTTP 204 No Content (성공 시 본문 없음)
     */
    @Override
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