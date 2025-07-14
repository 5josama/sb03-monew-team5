package com.sprint5team.monew.domain.interest.controller;

import com.sprint5team.monew.domain.interest.dto.CursorPageRequest;
import com.sprint5team.monew.domain.interest.dto.CursorPageResponseInterestDto;
import com.sprint5team.monew.domain.interest.service.InterestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.UUID;

/**
 * PackageName  : com.sprint5team.monew.domain.interest.controller
 * FileName     : InterestController
 * Author       : dounguk
 * Date         : 2025. 7. 11.
 */
@RequiredArgsConstructor
@Controller
@Validated
@RequestMapping("api/interests")
public class InterestController {

    private final InterestService interestService;

    @GetMapping
    public ResponseEntity<CursorPageResponseInterestDto> InterestPaginationController(
        @RequestParam(required = false) String keyword,
        @RequestParam String orderBy,
        @RequestParam String direction,
        @RequestParam(required = false) String cursor,
        @RequestParam(required = false) Instant after,
        @RequestParam Integer limit,
        @RequestHeader(name = "Monew-Request-User-ID") UUID userId
    ) {
        CursorPageRequest request = new CursorPageRequest(keyword, orderBy, direction, cursor, after, limit, userId);
        return ResponseEntity.ok(interestService.generateCursorPage(request));
    }
}
