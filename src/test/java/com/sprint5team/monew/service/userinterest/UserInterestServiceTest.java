package com.sprint5team.monew.service.userinterest;

import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.interest.service.InterestService;
import com.sprint5team.monew.domain.user.exception.UserNotFoundException;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import com.sprint5team.monew.domain.userinterest.dto.SubscriptionDto;
import com.sprint5team.monew.domain.userinterest.repository.UserInterestRepository;
import com.sprint5team.monew.domain.userinterest.service.UserInterestService;
import com.sprint5team.monew.domain.userinterest.service.UserInterestServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

/**
 * PackageName  : com.sprint5team.monew.service.userinterest
 * FileName     : UserInterestServiceTest
 * Author       : dounguk
 * Date         : 2025. 7. 18.
 */

@ExtendWith(MockitoExtension.class)
@DisplayName("Interest service unit 테스트")
public class UserInterestServiceTest {

    @InjectMocks
    private UserInterestServiceImpl userInterestService;

    @Mock
    private InterestRepository interestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserInterestRepository userInterestRepository;

    @Test
    void 구독중이_아닐경우_구독이_가능하다() throws Exception {
        // given
        UUID interestId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        given(userRepository.existsById(userId)).willReturn(true);
        given(interestRepository.existsById(interestId)).willReturn(true);
        given(userInterestRepository.existsByUserIdAndInterestId(userId, interestId)).willReturn(false);

        // when
        SubscriptionDto result = userInterestService.registerSubscription(interestId, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.interestId()).isEqualTo(interestId);
        then(userInterestRepository).should(times(1)).save(any());
    }

    @Test
    void 구독중일경우_저장하지_않는다() throws Exception { //
        // given
        UUID interestId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        given(userRepository.existsById(userId)).willReturn(true);
        given(interestRepository.existsById(interestId)).willReturn(true);
        given(userInterestRepository.existsByUserIdAndInterestId(userId, interestId)).willReturn(true);

        // when
        userInterestService.registerSubscription(interestId, userId);

        // then
        then(userInterestRepository).should(times(0)).save(any());
    }

    @Test
    void 구독중일경우_subscriberCount는_증가하지_않는다() throws Exception { //
        // given
        UUID interestId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        given(userRepository.existsById(userId)).willReturn(true);
        given(interestRepository.existsById(interestId)).willReturn(true);
        given(userInterestRepository.existsByUserIdAndInterestId(userId, interestId)).willReturn(true);

        // when
        SubscriptionDto result = userInterestService.registerSubscription(interestId, userId);

        // then
        then(userInterestRepository).should(times(0)).save(any());

    }

    @Test
    void 사용자_정보가_없을경우_UserNotFoundException_404_를_반환한다() throws Exception {
        // given
        UUID interestId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        given(userRepository.existsById(userId)).willReturn(false);

        // when
        assertThatThrownBy(() -> userInterestService.registerSubscription(interestId, userId))
            .isInstanceOf(UserNotFoundException.class);

        // then
        then(interestRepository).shouldHaveNoInteractions();
        then(userInterestRepository).should(times(0)).save(any());
        then(userInterestRepository).shouldHaveNoInteractions();
    }

    @Test
    void 관심사_정보가_없을경우_InterestNotFoundException_404_를_반환한다() throws Exception {
        // given
        UUID interestId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        given(userRepository.existsById(userId)).willReturn(true);
        given(interestRepository.existsById(interestId)).willReturn(false);

        // when
        assertThatThrownBy(() -> userInterestService.registerSubscription(interestId, userId))
            .isInstanceOf(UserNotFoundException.class);

        // then
        then(userRepository).should(times(1)).existsById(any());
        then(userInterestRepository).should(times(0)).save(any());
        then(userInterestRepository).shouldHaveNoInteractions();
    }

    @Test
    void 구독을_성공하면_관심사의_subscriberCount가_1_증가한다() throws Exception {
        // given
        UUID interestId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        given(userRepository.existsById(userId)).willReturn(true);
        given(interestRepository.existsById(interestId)).willReturn(true);
        given(userInterestRepository.existsByUserIdAndInterestId(userId, interestId)).willReturn(false);

        // when
        SubscriptionDto result = userInterestService.registerSubscription(interestId, userId);

        // then
        assertThat(result.interestId()).isEqualTo(interestId);
        assertThat(result.interestSubscriberCount()).isEqualTo(1);
        then(userInterestRepository).should(times(1)).save(any());
    }
}
