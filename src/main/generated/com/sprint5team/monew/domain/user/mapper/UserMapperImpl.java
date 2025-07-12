package com.sprint5team.monew.domain.user.mapper;

import com.sprint5team.monew.domain.user.dto.UserDto;
import com.sprint5team.monew.domain.user.entity.User;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-13T02:00:15+0900",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.2.jar, environment: Java 17.0.15 (Homebrew)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UUID userId = null;
        String email = null;
        String nickname = null;
        Instant createdAt = null;

        userId = user.getId();
        email = user.getEmail();
        nickname = user.getNickname();
        createdAt = user.getCreatedAt();

        UserDto userDto = new UserDto( userId, email, nickname, createdAt );

        return userDto;
    }
}
