package com.sprint5team.monew.domain.user.mapper;

import com.sprint5team.monew.domain.user.dto.UserDto;
import com.sprint5team.monew.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "userId", source = "id")
  UserDto toDto(User user);
}
