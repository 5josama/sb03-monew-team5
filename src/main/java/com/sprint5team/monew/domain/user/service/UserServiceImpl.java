package com.sprint5team.monew.domain.user.service;

import com.sprint5team.monew.domain.article.repository.ArticleCountRepository;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.comment.repository.LikeRepository;
import com.sprint5team.monew.domain.notification.repository.NotificationRepository;
import com.sprint5team.monew.domain.user.dto.UserDto;
import com.sprint5team.monew.domain.user.dto.UserRegisterRequest;
import com.sprint5team.monew.domain.user.dto.UserUpdateRequest;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.exception.InvalidLoginException;
import com.sprint5team.monew.domain.user.exception.UserAlreadyExistsException;
import com.sprint5team.monew.domain.user.exception.UserNotFoundException;
import com.sprint5team.monew.domain.user.mapper.UserMapper;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import com.sprint5team.monew.domain.user_interest.repository.UserInterestRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService{

  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private final ArticleCountRepository articleCountRepository;
  private final CommentRepository commentRepository;
  private final LikeRepository likeRepository;
  private final UserInterestRepository userInterestRepository;
  private final NotificationRepository notificationRepository;

  @Override
  public UserDto register(UserRegisterRequest request) {
    String email = request.email();
    String nickname = request.nickname();
    String password = request.password();

    if (userRepository.existsByEmail(email)) {
      throw new UserAlreadyExistsException();
    }

    User user = User.builder()
        .email(email)
        .nickname(nickname)
        .password(password)
        .isDeleted(false)
        .build();
    userRepository.save(user);

    return userMapper.toDto(user);
  }

  @Override
  public UserDto login(String email, String password) {

    User user = userRepository
        .findByEmailAndPassword(email, password)
        .orElseThrow(InvalidLoginException::new);

    if (user == null) {
      throw new InvalidLoginException();
    }

    return userMapper.toDto(user);
  }

  @Override
  public UserDto update(UUID userId, UserUpdateRequest request) {
    User user = userRepository
        .findById(userId)
        .orElseThrow(UserNotFoundException::new);

    if (request.nickname() != null && !request.nickname().equals(user.getNickname())) {
      user.updateNickname(request.nickname());
      userRepository.save(user);
    }

    return userMapper.toDto(user);
  }

  @Override
  public void hardDelete(UUID userId) {
    userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

    // 사용자 정보 삭제
    userRepository.deleteById(userId);

    // 사용자 관련 데이터 삭제
    articleCountRepository.deleteAllByUserId(userId);
    commentRepository.deleteAllByUserId(userId);
    likeRepository.deleteAllByUserId(userId);
    userInterestRepository.deleteAllByUserId(userId);
    notificationRepository.deleteAllByUserId(userId);
  }

  @Override
  public void softDelete(UUID id) {
    User user = userRepository
        .findById(id)
        .orElseThrow(UserNotFoundException::new);

    user.softDelete();
    userRepository.save(user);
  }
}
