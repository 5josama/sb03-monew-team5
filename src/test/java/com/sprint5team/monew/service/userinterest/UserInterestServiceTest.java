package com.sprint5team.monew.service.userinterest;

import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.exception.InterestNotExistsException;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.exception.UserNotFoundException;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import com.sprint5team.monew.domain.user_interest.exception.SubscriberNotMatchesException;
import com.sprint5team.monew.domain.user_interest.exception.UserInterestAlreadyExistsException;
import com.sprint5team.monew.domain.user_interest.dto.SubscriptionDto;
import com.sprint5team.monew.domain.user_interest.entity.UserInterest;
import com.sprint5team.monew.domain.user_interest.mapper.UserInterestMapper;
import com.sprint5team.monew.domain.user_interest.repository.UserInterestRepository;
import com.sprint5team.monew.domain.user_interest.service.UserInterestServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    @Mock
    private UserInterestMapper userInterestMapper;

    @Test
    void 구독중이_아닐경우_구독이_가능하다() throws Exception {
        // given
        User user = new User("test@test.com", "dk", "dkdkdk");
        Interest interest = new Interest("유리 공예");

        SubscriptionDto subscriptionDto = SubscriptionDto.builder()
            .interestId(interest.getId())
            .build();

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(interestRepository.findById(interest.getId())).willReturn(Optional.of(interest));
        given(userInterestRepository.existsByUserIdAndInterestId(user.getId(), interest.getId())).willReturn(false);
        given(userInterestMapper.toDto(any(UserInterest.class))).willReturn(subscriptionDto);

        // when
        SubscriptionDto result = userInterestService.registerSubscription(interest.getId(), user.getId());

        // then
        assertThat(result.interestId()).isEqualTo(interest.getId());
        then(userInterestRepository).should(times(1)).save(any());
    }

    @Test
    void 구독중인_경우_UserInterestAlreadyExistsException_400_를_반환한다() throws Exception { //
        // given
        User user = new User("test@test.com", "dk", "dkdkdk");
        Interest interest = new Interest("유리 공예");

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(interestRepository.findById(interest.getId())).willReturn(Optional.of(interest));
        given(userInterestRepository.existsByUserIdAndInterestId(user.getId(), interest.getId()))
            .willThrow(UserInterestAlreadyExistsException.class);

        // when
        assertThatThrownBy(() -> userInterestService.registerSubscription(interest.getId(), user.getId()))
            .isInstanceOf(UserInterestAlreadyExistsException.class);

        // then
        then(userInterestRepository).should(times(0)).save(any());
    }

    @Test
    void 사용자_정보가_없을경우_UserNotFoundException_404_를_반환한다() throws Exception {
        // given
        User user = new User("test@test.com", "dk", "dkdkdk");
        Interest interest = new Interest("유리 공예");

        given(userRepository.findById(user.getId())).willReturn(Optional.empty());
        // when
        assertThatThrownBy(() -> userInterestService.registerSubscription(interest.getId(), user.getId()))
            .isInstanceOf(UserNotFoundException.class);

        // then
        then(interestRepository).shouldHaveNoInteractions();
        then(userInterestRepository).should(times(0)).save(any());
        then(userInterestRepository).shouldHaveNoInteractions();
    }

    @Test
    void 관심사_정보가_없을경우_InterestNotExistException_404_를_반환한다() throws Exception {
        // given
        User user = new User("test@test.com", "dk", "dkdkdk");
        Interest interest = new Interest("유리 공예");

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(interestRepository.findById(interest.getId())).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> userInterestService.registerSubscription(interest.getId(), user.getId()))
            .isInstanceOf(InterestNotExistsException.class);

        // then
        then(userRepository).should(times(1)).findById(any());
        then(userInterestRepository).should(times(0)).save(any());
        then(userInterestRepository).shouldHaveNoInteractions();
    }

    @Test
    void 구독을_성공하면_관심사의_subscriberCount_1_증가한다() throws Exception {
        // given
        User user = new User("test@test.com", "dk", "dkdkdk");
        Interest interest = Interest.builder()
            .name("유리 공예")
            .subscriberCount(0L)
            .build();

        SubscriptionDto subscriptionDto = SubscriptionDto.builder()
            .interestId(interest.getId())
            .interestSubscriberCount(1L)
            .build();

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(interestRepository.findById(interest.getId())).willReturn(Optional.of(interest));
        given(userInterestRepository.existsByUserIdAndInterestId(user.getId(), interest.getId())).willReturn(false);
        given(userInterestMapper.toDto(any())).willReturn(subscriptionDto);

        // when
        SubscriptionDto result = userInterestService.registerSubscription(interest.getId(), user.getId());

        // then
        assertThat(result.interestId()).isEqualTo(interest.getId());
        assertThat(result.interestSubscriberCount()).isEqualTo(1);
        then(userInterestRepository).should(times(1)).save(any());
    }

    // TODO 구독 취소
    @Test
    void 구독_취소가_가능하다() throws Exception {
        // given
        UserInterest userInterest = UserInterest.builder().build();
        Interest interest = Interest.builder().subscriberCount(1L).build();

        given(userInterestRepository.findByUserIdAndInterestId(any(), any()))
            .willReturn(Optional.of(userInterest));
        given(userRepository.existsById(any())).willReturn(true);
        given(interestRepository.findById(any())).willReturn(Optional.of(interest));

        // when
        userInterestService.unsubscribeInterest(any(), any());

        // then
        then(userInterestRepository).should(times(1)).delete(any());
    }

    @Test
    void 구독_취소시_유저정보가_없을경우_UserNotFoundException_404_를_반환한다() throws Exception {
        // given
        UserInterest userInterest = UserInterest.builder().build();

        given(userInterestRepository.findByUserIdAndInterestId(any(), any()))
            .willReturn(Optional.of(userInterest));
        given(userRepository.existsById(any())).willReturn(false);

        // when n then
        assertThatThrownBy(()->userInterestService.unsubscribeInterest(any(), any()))
            .isInstanceOf(UserNotFoundException.class);

        then(userInterestRepository).should(times(0)).delete(any());
    }

    @Test
    void 구독_취소시_관심사_정보가_없을경우_InterestNotExistsException_404_를_반환한다() throws Exception {
        // given
        UserInterest userInterest = UserInterest.builder().build();

        given(userInterestRepository.findByUserIdAndInterestId(any(), any()))
            .willReturn(Optional.of(userInterest));
        given(userRepository.existsById(any())).willReturn(true);
        given(interestRepository.findById(any())).willReturn(Optional.empty());

        // when
        assertThatThrownBy(()->userInterestService.unsubscribeInterest(any(), any()))
            .isInstanceOf(InterestNotExistsException.class);

        // then
        then(userInterestRepository).should(times(0)).delete(any());
    }

    @Test
    void 요청자가_관심사를_이미_구독중일경우_UserInterestAlreadyExistsException_404_를_반환한다() throws Exception {
        // given

        given(userInterestRepository.findByUserIdAndInterestId(any(), any()))
            .willReturn(Optional.empty());

        // when
        assertThatThrownBy(()->userInterestService.unsubscribeInterest(any(), any()))
            .isInstanceOf(UserInterestAlreadyExistsException.class);

        // then
        then(userInterestRepository).should(times(0)).delete(any());
        then(interestRepository).shouldHaveNoInteractions();
        then(userRepository).shouldHaveNoInteractions();
    }
    @Test
    void 구독자_수가_1미만일때_구독을_취소하면_SubscriberNotMatchesException_409_를_반환한다() throws Exception {
        // given
        UserInterest userInterest = UserInterest.builder().build();
        Interest interest = Interest.builder().subscriberCount(0L).build();

        given(userInterestRepository.findByUserIdAndInterestId(any(), any()))
            .willReturn(Optional.of(userInterest));
        given(userRepository.existsById(any())).willReturn(true);
        given(interestRepository.findById(any())).willReturn(Optional.of(interest));

        // when
        assertThatThrownBy(()->userInterestService.unsubscribeInterest(any(), any()))
            .isInstanceOf(SubscriberNotMatchesException.class);

        // then
        then(userInterestRepository).should(times(0)).delete(any());
    }

}
