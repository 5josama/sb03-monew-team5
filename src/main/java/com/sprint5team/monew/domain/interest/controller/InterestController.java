package com.sprint5team.monew.domain.interest.controller;

import com.sprint5team.monew.domain.interest.dto.CursorPageRequest;
import com.sprint5team.monew.domain.interest.dto.CursorPageResponseInterestDto;
import com.sprint5team.monew.domain.interest.service.InterestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
@RequestMapping("api/interests")
public class InterestController {

    private final InterestService interestService;


    @GetMapping
    public ResponseEntity<?> InterestPaginationController(
        @RequestParam String keyword,
        @RequestParam(required = false) String orderBy,
        @RequestParam(required = false) String direction,
        @RequestParam String cursor,
        @RequestParam(defaultValue = "createdAt") Instant after,
        @RequestParam(required = false) Integer limit,
        @RequestHeader(name = "monew-request-user-id", required = false) UUID userId
    ) {
        CursorPageRequest request = new CursorPageRequest(keyword, orderBy, direction, cursor, after, limit, userId);
        CursorPageResponseInterestDto response = interestService.generateCursorPage(request);
        return ResponseEntity.ok(response);
    }
}
