package com.sprint5team.monew.domain.notification.dto;

import java.time.Instant;
import java.util.List;

/**
 * 커서 기반 알림 페이지네이션 응답 DTO
 * 알림 목록 조회 시 페이징 처리된 결과를 담고 있으며, 커서 방식으로 다음 페이지를 조회한다
 *
 * @param content        현재 페이지의 알림 목록
 * @param nextCursor     다음 페이지 요청을 위한 커서 (마지막 요소의 시간 정보)
 * @param nextAfter      다음 보조 커서 (마지막 요소의 생성 시간)
 * @param size           요청한 페이지 크기
 * @param totalElements  전체 미확인 알림 수
 * @param hasNext        다음 페이지가 존재하는지 여부
 */
public record CursorPageResponseNotificationDto(
        List<NotificationDto> content,
        String nextCursor,
        Instant nextAfter,
        int size,
        long totalElements,
        boolean hasNext
) {
}
