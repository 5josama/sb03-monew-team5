package com.sprint5team.monew.domain.user.service;

import com.sprint5team.monew.domain.user.dto.UserActivityDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserActivityServiceImpl {

  public UserActivityDto getUserActivity(UUID userId) {
    return null;
  }
}
