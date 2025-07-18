package com.sprint5team.monew.domain.interest.controller;

import com.sprint5team.monew.domain.interest.dto.CursorPageRequest;
import com.sprint5team.monew.domain.interest.dto.CursorPageResponseInterestDto;
import com.sprint5team.monew.domain.interest.dto.InterestDto;
import com.sprint5team.monew.domain.interest.dto.InterestRegisterRequest;
import com.sprint5team.monew.domain.interest.service.InterestService;
import com.sprint5team.monew.domain.keyword.dto.InterestUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

/**
 * PackageName  : com.sprint5team.monew.domain.interest.controller
 * FileName     : InterestController
 * Author       : dounguk
 * Date         : 2025. 7. 11.
 */
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("api/interests")
public class InterestController {

    private final InterestService interestService;

    @GetMapping
    public ResponseEntity<CursorPageResponseInterestDto> interestPaginationController(
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

    @PostMapping
    public ResponseEntity<InterestDto> insertInterest(@RequestBody @Valid InterestRegisterRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(interestService.registerInterest(request));
    }

    @DeleteMapping("/{interestId}")
    public ResponseEntity<Void> deleteInterest(@PathVariable UUID interestId) {
        interestService.deleteInterest(interestId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{interestId}")
    public ResponseEntity<InterestDto> updateInterest(
        @PathVariable UUID interestId,
        @RequestBody InterestUpdateRequest request,
        @RequestHeader(name = "Monew-Request-User-ID") UUID userId
    ){
        return ResponseEntity.ok(interestService.updateInterest(interestId, request, userId));
    }
}
