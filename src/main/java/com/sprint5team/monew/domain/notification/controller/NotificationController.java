package com.sprint5team.monew.domain.notification.controller;


import com.sprint5team.monew.domain.notification.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    @GetMapping
    public NotificationDto getAllNotification() {
        return null;
    }
}
