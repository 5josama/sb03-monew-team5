package com.sprint5team.monew.domain.user.service;

import com.sprint5team.monew.domain.user.dto.UserActivityDto;
import java.util.UUID;

public interface UserActivityService {

  UserActivityDto getUserActivity(UUID userId);
}
