package com.sprint5team.monew.service.interest;

import com.sprint5team.monew.domain.interest.dto.CursorPageRequest;
import com.sprint5team.monew.domain.interest.dto.CursorPageResponseInterestDto;
import com.sprint5team.monew.domain.interest.dto.InterestDto;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.interest.repository.InterestRepositoryImpl;
import com.sprint5team.monew.domain.interest.service.InterestService;
import com.sprint5team.monew.domain.interest.service.InterestServiceImpl;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import com.sprint5team.monew.domain.user_interest.entity.UserInterest;
import com.sprint5team.monew.domain.user_interest.mapper.InterestMapper;
import com.sprint5team.monew.domain.user_interest.repository.UserInterestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatReflectiveOperationException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    @InjectMocks
    private InterestServiceImpl interestService;

    @Mock
    private InterestRepository interestRepository;

    @Mock
    private KeywordRepository keywordRepository;

    @Mock
    private UserInterestRepository userInterestRepository;


    @Mock
    private InterestMapper interestMapper;

    Interest interestA, interestB, interestC;

    @BeforeEach
    public void setup() {
        Instant createdAt = Instant.now();

        interestA = Interest.builder()
            .name("다큐멘터리")
            .subscriberCount(50L)
            .createdAt(createdAt.minus(Duration.ofMinutes(10)))
            .build();
        ReflectionTestUtils.setField(interestA, "id", UUID.randomUUID());

        interestB = Interest.builder()
            .name("게임")
            .subscriberCount(200L)
            .createdAt(createdAt.minus(Duration.ofMinutes(20)))
            .build();
        ReflectionTestUtils.setField(interestB, "id", UUID.randomUUID());

        interestC = Interest.builder()
            .name("스포츠")
            .subscriberCount(100L)
            .createdAt(createdAt.minus(Duration.ofMinutes(5)))
            .build();
        ReflectionTestUtils.setField(interestC, "id", UUID.randomUUID());
    }


    @Test
    void 입력된_keyword를_포함한_관심사를_조회한다() throws Exception {
        // given

        CursorPageRequest validRequest = CursorPageRequest.builder()
            .keyword("게임")
            .orderBy("name")
            .direction("asc")
            .limit(10)
            .userId(UUID.randomUUID())
            .build();

        UserInterest userInterest = UserInterest.builder()
            .interest(interestB)
            .build();
        ReflectionTestUtils.setField(userInterest, "id", validRequest.getUserId());

        Keyword validKeyword = Keyword.builder().build();

        given(interestRepository.findAllInterestByRequest(validRequest))
            .willReturn(List.of(interestB));

        given(userInterestRepository.findByUserId(validRequest.getUserId()))
            .willReturn(Set.of(userInterest));

        given(interestRepository.countTotalElements(validRequest))
            .willReturn(1L);

        given(keywordRepository.findAllByInterest(any(Interest.class)))
            .willReturn(Set.of(validKeyword));

        given(interestMapper.toDto(any(),any(),eq(true)))
            .willReturn(mock(InterestDto.class));

        // when
        CursorPageResponseInterestDto result = interestService.generateCursorPage(validRequest);

        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).getClass()).isEqualTo(InterestDto.class);
        assertThat(result.hasNext()).isFalse();

        verify(interestRepository).findAllInterestByRequest(validRequest);
        then(interestRepository).should(times(1)).findAllInterestByRequest(validRequest);
        then(interestRepository).should(times(1)).countTotalElements(validRequest);
        then(interestMapper).should(times(1)).toDto(any(),any(),eq(true));
    }

    @Test
    void 조건이_없을경우_모든_관심사를_조회한다() throws Exception {
        // given
        CursorPageRequest request = CursorPageRequest.builder()
            .orderBy("name")
            .direction("asc")
            .limit(10)
            .userId(UUID.randomUUID())
            .build();

        given(interestRepository.findAllInterestByRequest(request))
            .willReturn(List.of(interestA, interestB, interestC));

        given(interestRepository.countTotalElements(request))
            .willReturn(3L);

        given(keywordRepository.findAllByInterest(any(Interest.class)))
            .willReturn(Set.of(Keyword.builder().name("키워드").build()));

        given(userInterestRepository.findByUserId(request.getUserId()))
            .willReturn(Set.of(
                UserInterest.builder().interest(interestB).build()
            ));

        given(interestMapper.toDto(eq(interestA), any(), anyBoolean())).willReturn(mock(InterestDto.class));
        given(interestMapper.toDto(eq(interestB), any(), anyBoolean())).willReturn(mock(InterestDto.class));
        given(interestMapper.toDto(eq(interestC), any(), anyBoolean())).willReturn(mock(InterestDto.class));

        // when
        CursorPageResponseInterestDto result = interestService.generateCursorPage(request);

        // then
        assertThat(result.totalElements()).isEqualTo(3);
        assertThat(result.content()).hasSize(3);
        assertThat(result.hasNext()).isFalse();

        verify(interestRepository).findAllInterestByRequest(request);
        verify(interestRepository).countTotalElements(request);
        verify(interestMapper, times(3)).toDto(any(), any(), anyBoolean());

    }

    @Test
    void 커서값을_기준으로_조회한다() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        CursorPageRequest request = CursorPageRequest.builder()
            .orderBy("name")
            .direction("desc")
            .cursor("게임")
            .limit(10)
            .userId(userId)
            .build();

        InterestDto dto = InterestDto.builder()
            .id(interestA.getId())
            .name(interestA.getName())
            .subscriberCount(interestA.getSubscriberCount())
            .keywords(List.of("키워드"))
            .subscribedByMe(false)
            .build();

        given(interestRepository.findAllInterestByRequest(any())).willReturn(List.of(interestA));
        given(interestRepository.countTotalElements(any())).willReturn(1L);
        given(keywordRepository.findAllByInterest(any())).willReturn(Set.of());
        given(userInterestRepository.findByUserId(any())).willReturn(Set.of());
        given(interestMapper.toDto(any(), any(), eq(false))).willReturn(dto);

        CursorPageResponseInterestDto result = interestService.generateCursorPage(request);

        assertThat(result.content()).containsExactly(dto);
        assertThat(result.totalElements()).isEqualTo(1L);
    }


    @Test
    void 구독여부를_판단할_수_있다() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        CursorPageRequest request = CursorPageRequest.builder()
            .orderBy("name")
            .direction("asc")
            .limit(10)
            .userId(userId)
            .build();

        UserInterest userInterest = UserInterest.builder()
            .interest(interestA)
            .build();
        ReflectionTestUtils.setField(userInterest, "id", userId);

        InterestDto dto = InterestDto.builder()
            .id(interestA.getId())
            .name(interestA.getName())
            .subscriberCount(interestA.getSubscriberCount())
            .keywords(List.of("키워드"))
            .subscribedByMe(true)
            .build();

        given(interestRepository.findAllInterestByRequest(any()))
            .willReturn(List.of(interestA));
        given(interestRepository.countTotalElements(any()))
            .willReturn(1L);
        given(keywordRepository.findAllByInterest(any()))
            .willReturn(Set.of(Keyword.builder().name("키워드").build()));
        given(userInterestRepository.findByUserId(userId))
            .willReturn(Set.of(userInterest));
        given(interestMapper.toDto(eq(interestA), any(), eq(true)))
            .willReturn(dto);

        // when
        CursorPageResponseInterestDto result = interestService.generateCursorPage(request);

        // then
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).subscribedByMe()).isTrue();
    }


    @Test
    void 관심사_수가_원하는_관심사수보다_적을경우_nextCursor_nextAfter은_null을_반환한다() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        CursorPageRequest request = CursorPageRequest.builder()
            .orderBy("name")
            .direction("desc")
            .limit(10)
            .userId(userId)
            .build();

        InterestDto dto = InterestDto.builder()
            .id(interestA.getId())
            .name(interestA.getName())
            .subscriberCount(interestA.getSubscriberCount())
            .keywords(List.of("키워드"))
            .subscribedByMe(false)
            .build();

        given(interestRepository.findAllInterestByRequest(any())).willReturn(List.of(interestC,interestA,interestB));
        given(interestRepository.countTotalElements(any())).willReturn(1L);
        given(keywordRepository.findAllByInterest(any())).willReturn(Set.of());
        given(userInterestRepository.findByUserId(any())).willReturn(Set.of());
        given(interestMapper.toDto(any(), any(), eq(false))).willReturn(dto);

        // when
        CursorPageResponseInterestDto result = interestService.generateCursorPage(request);

        // then
        assertThat(result.nextCursor()).isNull();
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    void 요청한_관심사_이후_관심사가_없을경우_nextCursor_nextAfter은_null을_반환한다() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        CursorPageRequest request = CursorPageRequest.builder()
            .orderBy("name")
            .direction("asc")
            .cursor("게임")
            .limit(2)
            .userId(userId)
            .build();

        InterestDto dto = InterestDto.builder()
            .id(interestA.getId())
            .name(interestA.getName())
            .subscriberCount(interestA.getSubscriberCount())
            .keywords(List.of("키워드"))
            .subscribedByMe(false)
            .build();
        InterestDto dto2 = InterestDto.builder()
            .id(interestC.getId())
            .name(interestC.getName())
            .subscriberCount(interestC.getSubscriberCount())
            .keywords(List.of("키워드"))
            .subscribedByMe(false)
            .build();

        given(interestRepository.findAllInterestByRequest(any())).willReturn(List.of(interestA,interestC));
        given(interestRepository.countTotalElements(any())).willReturn(2L);
        given(keywordRepository.findAllByInterest(any())).willReturn(Set.of());
        given(userInterestRepository.findByUserId(any())).willReturn(Set.of());
        given(interestMapper.toDto(eq(interestA), any(), eq(false))).willReturn(dto);
        given(interestMapper.toDto(eq(interestC), any(), eq(false))).willReturn(dto2);

        // when
        CursorPageResponseInterestDto result = interestService.generateCursorPage(request);

        // then
        assertThat(result.nextCursor()).isNull();
        assertThat(result.hasNext()).isFalse();
        assertThat(result.content().get(0).name()).isEqualTo(interestA.getName());
        assertThat(result.content().get(1).name()).isEqualTo(interestC.getName());
    }

    @Test
    void 커서_페이지_크기로_조회한다() throws Exception {
    // given
        UUID userId = UUID.randomUUID();

        CursorPageRequest request = CursorPageRequest.builder()
            .orderBy("name")
            .direction("asc")
            .cursor("게임")
            .limit(2)
            .userId(userId)
            .build();

        InterestDto dto = InterestDto.builder()
            .id(interestA.getId())
            .name(interestA.getName())
            .subscriberCount(interestA.getSubscriberCount())
            .keywords(List.of("키워드"))
            .subscribedByMe(false)
            .build();
        InterestDto dto2 = InterestDto.builder()
            .id(interestC.getId())
            .name(interestC.getName())
            .subscriberCount(interestC.getSubscriberCount())
            .keywords(List.of("키워드"))
            .subscribedByMe(false)
            .build();

        given(interestRepository.findAllInterestByRequest(any())).willReturn(List.of(interestA,interestC));
        given(interestRepository.countTotalElements(any())).willReturn(2L);
        given(keywordRepository.findAllByInterest(any())).willReturn(Set.of());
        given(userInterestRepository.findByUserId(any())).willReturn(Set.of());
        given(interestMapper.toDto(eq(interestA), any(), eq(false))).willReturn(dto);
        given(interestMapper.toDto(eq(interestC), any(), eq(false))).willReturn(dto2);

        // when
        CursorPageResponseInterestDto result = interestService.generateCursorPage(request);

        // then
        assertThat(result.content().size()).isEqualTo(2);
    }




}
