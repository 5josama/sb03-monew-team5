package com.sprint5team.monew.integration.userinterest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.exception.InterestNotExistsException;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.exception.UserNotFoundException;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import com.sprint5team.monew.domain.user_interest.exception.UserInterestAlreadyExistsException;
import com.sprint5team.monew.domain.user_interest.dto.SubscriptionDto;
import com.sprint5team.monew.domain.user_interest.repository.UserInterestRepository;
import com.sprint5team.monew.domain.user_interest.service.UserInterestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * PackageName  : com.sprint5team.monew.integration.userinterest
 * FileName     : UserInterestIntegrationTest
 * Author       : dounguk
 * Date         : 2025. 7. 18.
 */

@DisplayName("User Interest 통합 테스트")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
public class UserInterestIntegrationTest {


    @Autowired
    private UserInterestRepository userInterestRepository;

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private UserInterestService userInterestService;

    private Instant baseTime = Instant.now();

    User globalUser;
    Interest globalInterest;
    Keyword globalKeyword;



    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        userInterestRepository.deleteAll();
        interestRepository.deleteAll();
        keywordRepository.deleteAll();

        globalUser = new User("email@email", "userA", "12341234");
        userRepository.save(globalUser);

        globalInterest = Interest.builder()
            .createdAt(baseTime)
            .name("Global Interest")
            .build();
        interestRepository.save(globalInterest);

        globalKeyword = Keyword.builder()
            .createdAt(baseTime)
            .name("Global Keyword")
            .interest(globalInterest)
            .build();
        keywordRepository.save(globalKeyword);
    }


    @Test
    void 사용자가_관심사를_정상적으로_구독한다() throws Exception {
        // when
        SubscriptionDto result = userInterestService.registerSubscription(globalInterest.getId(), globalUser.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(SubscriptionDto.class);
        assertThat(result.interestId()).isEqualTo(globalInterest.getId());
        assertThat(result.interestName()).isEqualTo(globalInterest.getName());
        assertThat(result.interestKeywords().get(0)).isEqualTo(globalKeyword.getName());
        assertThat(result.interestSubscriberCount()).isEqualTo(1);
    }

    @Test
    void 사용자가_유효하지_않을경우_UserNotFoundException_404_를_반환한다() throws Exception {
        // given
        UUID invalidUserId = UUID.randomUUID();

        // when n then
        assertThatThrownBy(()->userInterestService.registerSubscription(globalInterest.getId(), invalidUserId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("존재하지 않는 사용자입니다.");
    }

    @Test
    void 관심사가_유효하지_않을경우_InterestNotExistsException_404_를_반환한다() throws Exception {
        // given
        UUID invalidUserId = UUID.randomUUID();

        // when n then
        assertThatThrownBy(() -> userInterestService.registerSubscription(invalidUserId, globalUser.getId()))
            .isInstanceOf(InterestNotExistsException.class)
            .hasMessageContaining("일치하는 관심사 없음");
    }

    @Test
    void 구독중인_관심사를_구독하려_시도할_경우_UserInterestAlreadyExistsException_404_를_반환한다() throws Exception {
        // given
        userInterestService.registerSubscription(globalInterest.getId(), globalUser.getId());

        // when n then
        assertThatThrownBy(() -> userInterestService.registerSubscription(globalInterest.getId(), globalUser.getId()))
            .isInstanceOf(UserInterestAlreadyExistsException.class)
            .hasMessageContaining("이미 구독중");
    }

}
