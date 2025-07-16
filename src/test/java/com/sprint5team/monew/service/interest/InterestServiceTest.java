package com.sprint5team.monew.service.interest;

import com.sprint5team.monew.domain.interest.exception.InterestNotExistException;
import com.sprint5team.monew.domain.interest.exception.SimilarInterestException;
import com.sprint5team.monew.domain.interest.dto.CursorPageRequest;
import com.sprint5team.monew.domain.interest.dto.CursorPageResponseInterestDto;
import com.sprint5team.monew.domain.interest.dto.InterestDto;
import com.sprint5team.monew.domain.interest.dto.InterestRegisterRequest;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.interest.service.InterestServiceImpl;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import com.sprint5team.monew.domain.user_interest.entity.UserInterest;
import com.sprint5team.monew.domain.interest.mapper.InterestMapper;
import com.sprint5team.monew.domain.user_interest.repository.UserInterestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.*;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    private static final double THRESHOLD = 0.75;
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

        Keyword validKeyword = Keyword.builder()
            .name("관심사")
            .interest(interestB)
            .build();
        ReflectionTestUtils.setField(validKeyword, "id", UUID.randomUUID());

        given(interestRepository.findAllInterestByRequest(validRequest))
            .willReturn(List.of(interestB));

        given(userInterestRepository.findByUserId(validRequest.getUserId()))
            .willReturn(Set.of(userInterest));

        given(interestRepository.countTotalElements(validRequest))
            .willReturn(1L);

        given(keywordRepository.findAllByInterestIn(any()))
            .willReturn(List.of(validKeyword));

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

        Keyword validKeyword = Keyword.builder()
            .name("관심사")
            .interest(interestB)
            .build();
        ReflectionTestUtils.setField(validKeyword, "id", UUID.randomUUID());

        given(interestRepository.findAllInterestByRequest(request))
            .willReturn(List.of(interestA, interestB, interestC));

        given(interestRepository.countTotalElements(request))
            .willReturn(3L);

        given(keywordRepository.findAllByInterestIn(any()))
            .willReturn(List.of(validKeyword));

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
        given(keywordRepository.findAllByInterestIn(any())).willReturn(List.of());
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

        Keyword validKeyword = Keyword.builder()
            .name("관심사")
            .interest(interestA)
            .build();
        ReflectionTestUtils.setField(validKeyword, "id", UUID.randomUUID());

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
        given(keywordRepository.findAllByInterestIn(any()))
            .willReturn(List.of(validKeyword));
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
        given(keywordRepository.findAllByInterestIn(any())).willReturn(List.of());
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
        given(keywordRepository.findAllByInterestIn(any())).willReturn(List.of());
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
        given(keywordRepository.findAllByInterestIn(any())).willReturn(List.of());
        given(userInterestRepository.findByUserId(any())).willReturn(Set.of());
        given(interestMapper.toDto(eq(interestA), any(), eq(false))).willReturn(dto);
        given(interestMapper.toDto(eq(interestC), any(), eq(false))).willReturn(dto2);

        // when
        CursorPageResponseInterestDto result = interestService.generateCursorPage(request);

        // then
        assertThat(result.content().size()).isEqualTo(2);
    }


    // TODO 관심사 추가 로직 관련 테스트 코드 작성

    @Test
    void 관심사를_추가한다_() throws Exception {
        // given
        InterestRegisterRequest request = InterestRegisterRequest.builder()
            .name("주종")
            .keywords(List.of("막걸리"))
            .build();
        InterestDto response = InterestDto.builder()
            .name("주종")
            .keywords(List.of("막걸리"))
            .build();

        given(interestRepository.existsByNameEqualsIgnoreCase(any())).willReturn(false);
        given(interestMapper.toDto(any(Interest.class), any(), eq(false)))
            .willReturn(response);

        // when
        InterestDto result = interestService.registerInterest(request);

        // then
        assertThat(result.name()).isEqualTo(response.name());
        assertThat(result.keywords()).isEqualTo(response.keywords());
        then(interestMapper).should(times(1)).toDto(any(Interest.class), any(), eq(false));
        then(interestRepository).should(times(1)).save(any(Interest.class));
        then(keywordRepository).should(times(1)).saveAll(anyList());
    }


    @Test
    void 관심사_이름의_유사도가_80퍼센트_이상일경우_SimilarInterestException_409_을_반환한다() throws Exception {
        // given
        InterestRegisterRequest request = InterestRegisterRequest.builder()
            .name("위스키 브랜드")
            .keywords(List.of("보모어"))
            .build();
        given(interestRepository.existsSimilarName(anyString(),anyDouble())).willReturn(true);

        // when n then
        assertThatThrownBy(() -> interestService.registerInterest(request))
            .isInstanceOf(SimilarInterestException.class)
            .hasMessageContaining("관심사 80% 이상 일치");

        then(keywordRepository).shouldHaveNoMoreInteractions();
        then(interestMapper).shouldHaveNoInteractions();

    }

    @Test
    void 관심사_이름이_일치하는_관심사가_있을경우_오류를_반환한다() throws Exception {
        // given
        InterestRegisterRequest request = InterestRegisterRequest.builder()
            .name("주종")
            .keywords(List.of("막걸리"))
            .build();
        given(interestRepository.existsByNameEqualsIgnoreCase(any())).willReturn(true);


        // when n then
        assertThatThrownBy(() -> interestService.registerInterest(request))
            .isInstanceOf(SimilarInterestException.class)
            .hasMessageContaining("관심사 80% 이상 일치");

        then(keywordRepository).shouldHaveNoMoreInteractions();
        then(interestMapper).shouldHaveNoInteractions();
    }

    @Test
    void 입력받은_키워드수만큼_키워드를_저장한다() throws Exception {
        // given
        List<String> keywords = List.of("보모어", "글랜피딕", "토버모리", "발베니", "블라드녹");
        InterestRegisterRequest request = InterestRegisterRequest.builder()
            .name("위스키 브랜드")
            .keywords(List.of("보모어","글랜피딕","토버모리","발베니","블라드녹"))
            .build();

        InterestDto response = InterestDto.builder()
            .name("위스키 브랜드")
            .keywords(List.of("보모어","글랜피딕","토버모리","발베니","블라드녹"))
            .build();

        given(interestRepository.existsSimilarName(anyString(),anyDouble())).willReturn(false);
        given(interestMapper.toDto(any(Interest.class), any(), eq(false)))
            .willReturn(response);


        // when
        InterestDto result = interestService.registerInterest(request);

        // then
        assertThat(result.name()).isEqualTo(response.name());
        assertThat(result.keywords()).isEqualTo(response.keywords());
        then(interestMapper).should(times(1)).toDto(any(Interest.class), any(), eq(false));
        then(interestRepository).should(times(1)).save(any(Interest.class));

        ArgumentCaptor<List<Keyword>> captor = ArgumentCaptor.forClass(List.class);
        then(keywordRepository).should(times(1)).saveAll(captor.capture());

        List<Keyword> savedKeywords = captor.getValue();
        assertThat(savedKeywords).hasSize(5);
        assertThat(savedKeywords)
            .extracting("name")
            .containsExactlyInAnyOrderElementsOf(keywords);
    }

    // TODO 관심사 삭제 로직
    @Test
    void 관심사를_삭제할_수_있다() throws Exception {
        // given
        UUID interestId = UUID.randomUUID();
        given(interestRepository.existsById(interestId)).willReturn(true);

        // when
        interestService.deleteInterest(interestId);

        // then
        then(interestRepository).should(times(1)).deleteById(interestId);
    }

    @Test
    void 관심사가_없으면_InterestNotExistException_404_를_반환한다() throws Exception {
        // given
        UUID interestId = UUID.randomUUID();
        given(interestRepository.existsById(interestId)).willReturn(false);


        // when n then
        assertThatThrownBy(() -> interestService.deleteInterest(interestId))
            .isInstanceOf(InterestNotExistException.class)
            .hasMessageContaining("일치하는 관심사 없음");
    }





}
