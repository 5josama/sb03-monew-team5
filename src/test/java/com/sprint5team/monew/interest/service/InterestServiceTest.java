package com.sprint5team.monew.interest.service;

import com.sprint5team.monew.domain.interest.dto.CursorPageResponseInterestDto;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.interest.service.InterestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.BDDMockito.*;

/**
 * PackageName  : com.sprint5team.monew.interest.service
 * FileName     : InterestServiceTest
 * Author       : dounguk
 * Date         : 2025. 7. 11.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Interest service unit 테스트")
public class InterestServiceTest {
    @Mock
    private InterestRepository interestRepository; // Mock the repository layer

    @InjectMocks
    private InterestService interestService;



    /**
     *0 required
     * orderBy(name,subscriberCount), direction, limit, Monew-Request-User-Id
     *
     *0 not required
     * keyword, cursor, after
     */

    @Test
    void 입력된_keyword를_포함한_관심사를_조회한다() throws Exception {
        // given
        Instant createdAt = Instant.now();

//        String keyword = "여행";
//        String orderBy = "createdAt";
//        String direction = "DESC";
//        Integer limit = 5;
//        UUID newRequestUserId = UUID.randomUUID();



        Interest interest1 = Interest.builder()
            .name("스포츠")
            .subscriberCount(0L)
            .build();
        ReflectionTestUtils.setField(interest1, "createdAt", createdAt);

        Interest interest2 = Interest.builder()
            .name("취미")
            .subscriberCount(0L)
            .build();
        ReflectionTestUtils.setField(interest2, "createdAt", createdAt);

        List<Interest> interests = Arrays.asList(interest1, interest2);


        given(interestRepository.findInterestByCondition(any()))
            .willReturn(interests);

        // when
        CursorPageResponseInterestDto result = interestService.generateCursorPage();
    }

    @Test
    void 이름으로_정렬한다() throws Exception {
        // given

        // when

        // then

    }

    @Test
    void 구독자수로_정렬한다() throws Exception {
        // given

        // when

        // then

    }


    @Test
    void 오름차순_정렬한다() throws Exception {
        // given

        // when

        // then

    }

    @Test
    void 내림차순_정렬한다() throws Exception {
        // given

        // when

        // then

    }

    @Test
    void 커서값을_기준으로_조회한다() throws Exception {
        // given

        // when

        // then

    }

    @Test
    void 보조커서_기준으로_조회한다() throws Exception {
        // given

        // when

        // then

    }

    @Test
    void 커서_페이지_크기로_조회한다() throws Exception {
        // given

        // when

        // then

    }

    @Test
    @DisplayName("")
    void 유저_기반_구독여부를_판단한다() throws Exception {
        // given

        // when

        // then

    }


}
