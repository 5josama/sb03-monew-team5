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
    public ResponseEntity<?> InterestPaginationController(
        @RequestParam(required = false) String keyword,
        @RequestParam String orderBy,
        @RequestParam String direction,
        @RequestParam(required = false) String cursor,
        @RequestParam(required = false) Instant after,
        @RequestParam Integer limit,
        @RequestHeader(name = "monew-request-user-id") UUID userId
    ) {
        CursorPageRequest request = new CursorPageRequest(keyword, orderBy, direction, cursor, after, limit, userId);
        @Valid CursorPageResponseInterestDto response = interestService.generateCursorPage(request);
        return ResponseEntity.ok(response);
    }
}
