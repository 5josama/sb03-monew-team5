package com.sprint5team.monew.service.interest;

import com.sprint5team.monew.domain.interest.dto.CursorPageRequest;
import com.sprint5team.monew.domain.interest.dto.CursorPageResponseInterestDto;
import com.sprint5team.monew.domain.interest.dto.InterestDto;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


import static org.assertj.core.api.Assertions.assertThat;
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
        CursorPageRequest validRequest = CursorPageRequest.builder()
            .keyword("축구")
            .orderBy("name")
            .direction("asc")
            .limit(10)
            .userId(UUID.randomUUID())
            .build();

        given(interestRepository.findAllInterestByRequest(validRequest))
            .willReturn(Arrays.asList());

        // when
        interestService.generateCursorPage(validRequest);

        verify(interestRepository).findAllInterestByRequest(validRequest);
    }

    @Test
    void 이름으로_정렬한다() throws Exception {
        // given
        String keyword = "스포츠";
        String orderBy = "name";
        String direction = "asc";
        String cursor = null;
        Instant after = null;
        Integer limit = 10;
        UUID userId = UUID.randomUUID();

        CursorPageRequest request = new CursorPageRequest(keyword, orderBy, direction, cursor, after, limit, userId);
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());

        Interest interestA = Interest.builder()
            .name("게임").subscriberCount(100L).build();
        ReflectionTestUtils.setField(interestA, "createdAt", now.minusMinutes(10));
        Interest interestB = Interest.builder()
            .name("다큐멘터리").subscriberCount(50L).build();
        ReflectionTestUtils.setField(interestB, "createdAt", now.minusMinutes(20));
        Interest interestC = Interest.builder()
            .name("스포츠").subscriberCount(200L).build();
        ReflectionTestUtils.setField(interestC, "createdAt", now.minusMinutes(5));
        List<Interest> interests = Arrays.asList(interestA, interestB, interestC);

        given(interestRepository.findAllInterestByRequest(any(CursorPageRequest.class)))
            .willReturn(interests);


        InterestDto expectedDtoA = InterestDto.builder()
            .id(UUID.nameUUIDFromBytes(interestA.getId().toString().getBytes()))
            .name(interestA.getName())
            .subscriberCount(interestA.getSubscriberCount())
            .keywords(Collections.emptyList())
            .subscribedByMe(false).build();
        InterestDto expectedDtoB = InterestDto.builder()
            .id(UUID.nameUUIDFromBytes(interestB.getId().toString().getBytes()))
            .name(interestB.getName())
            .subscriberCount(interestB.getSubscriberCount())
            .keywords(Collections.emptyList())
            .subscribedByMe(false).build();
        InterestDto expectedDtoC = InterestDto.builder()
            .id(UUID.nameUUIDFromBytes(interestC.getId().toString().getBytes()))
            .name(interestC.getName())
            .subscriberCount(interestC.getSubscriberCount())
            .keywords(Collections.emptyList())
            .subscribedByMe(false).build();
        List<InterestDto> expectedInterestDtos = Arrays.asList(expectedDtoA, expectedDtoB, expectedDtoC);

        // when
        CursorPageResponseInterestDto result = interestService.generateCursorPage(request);

        // then
        verify(interestRepository).findAllInterestByRequest(request);

        assertThat(result.content())
            .isNotNull()
            .hasSize(interests.size())
            .containsExactlyElementsOf(expectedInterestDtos);

        assertThat(result.nextCursor()).isEqualTo(interestC.getId().toString());
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    void 구독자수로_정렬한다() throws Exception {
        // given
        String keyword = "스포츠";
        String orderBy = "subscriberCount";
        String direction = "asc";
        String cursor = null;
        Instant after = null;
        Integer limit = 10;
        UUID userId = UUID.randomUUID();

        CursorPageRequest request = new CursorPageRequest(keyword, orderBy, direction, cursor, after, limit, userId);
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());

        Interest interestA = Interest.builder()
            .name("게임").subscriberCount(100L).build();
        ReflectionTestUtils.setField(interestA, "createdAt", now.minusMinutes(10));
        Interest interestB = Interest.builder()
            .name("다큐멘터리").subscriberCount(50L).build();
        ReflectionTestUtils.setField(interestB, "createdAt", now.minusMinutes(20));
        Interest interestC = Interest.builder()
            .name("스포츠").subscriberCount(200L).build();
        ReflectionTestUtils.setField(interestC, "createdAt", now.minusMinutes(5));
        List<Interest> interests = Arrays.asList(interestA, interestB, interestC);

        given(interestRepository.findAllInterestByRequest(any(CursorPageRequest.class)))
            .willReturn(interests);


        InterestDto expectedDtoA = InterestDto.builder()
            .id(UUID.nameUUIDFromBytes(interestA.getId().toString().getBytes()))
            .name(interestA.getName())
            .subscriberCount(interestA.getSubscriberCount())
            .keywords(Collections.emptyList())
            .subscribedByMe(false).build();
        InterestDto expectedDtoB = InterestDto.builder()
            .id(UUID.nameUUIDFromBytes(interestB.getId().toString().getBytes()))
            .name(interestB.getName())
            .subscriberCount(interestB.getSubscriberCount())
            .keywords(Collections.emptyList())
            .subscribedByMe(false).build();
        InterestDto expectedDtoC = InterestDto.builder()
            .id(UUID.nameUUIDFromBytes(interestC.getId().toString().getBytes()))
            .name(interestC.getName())
            .subscriberCount(interestC.getSubscriberCount())
            .keywords(Collections.emptyList())
            .subscribedByMe(false).build();
        List<InterestDto> expectedInterestDtos = Arrays.asList(expectedDtoB, expectedDtoA, expectedDtoC);

        // when
        CursorPageResponseInterestDto result = interestService.generateCursorPage(request);

        // then
        verify(interestRepository).findAllInterestByRequest(request);

        assertThat(result.content())
            .isNotNull()
            .hasSize(interests.size())
            .containsExactlyElementsOf(expectedInterestDtos);

        assertThat(result.nextCursor()).isEqualTo(interestC.getId().toString());
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    void 내림차순_정렬한다() throws Exception {
        // given
        String keyword = "스포츠";
        String orderBy = "subscriberCount";
        String direction = "desc";
        String cursor = null;
        Instant after = null;
        Integer limit = 10;
        UUID userId = UUID.randomUUID();

        CursorPageRequest request = new CursorPageRequest(keyword, orderBy, direction, cursor, after, limit, userId);
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());

        Interest interestA = Interest.builder()
            .name("게임").subscriberCount(100L).build();
        ReflectionTestUtils.setField(interestA, "createdAt", now.minusMinutes(10));
        Interest interestB = Interest.builder()
            .name("다큐멘터리").subscriberCount(50L).build();
        ReflectionTestUtils.setField(interestB, "createdAt", now.minusMinutes(20));
        Interest interestC = Interest.builder()
            .name("스포츠").subscriberCount(200L).build();
        ReflectionTestUtils.setField(interestC, "createdAt", now.minusMinutes(5));
        List<Interest> interests = Arrays.asList(interestA, interestB, interestC);

        given(interestRepository.findAllInterestByRequest(any(CursorPageRequest.class)))
            .willReturn(interests);


        InterestDto expectedDtoA = InterestDto.builder()
            .id(UUID.nameUUIDFromBytes(interestA.getId().toString().getBytes()))
            .name(interestA.getName())
            .subscriberCount(interestA.getSubscriberCount())
            .keywords(Collections.emptyList())
            .subscribedByMe(false).build();
        InterestDto expectedDtoB = InterestDto.builder()
            .id(UUID.nameUUIDFromBytes(interestB.getId().toString().getBytes()))
            .name(interestB.getName())
            .subscriberCount(interestB.getSubscriberCount())
            .keywords(Collections.emptyList())
            .subscribedByMe(false).build();
        InterestDto expectedDtoC = InterestDto.builder()
            .id(UUID.nameUUIDFromBytes(interestC.getId().toString().getBytes()))
            .name(interestC.getName())
            .subscriberCount(interestC.getSubscriberCount())
            .keywords(Collections.emptyList())
            .subscribedByMe(false).build();
        List<InterestDto> expectedInterestDtos = Arrays.asList(expectedDtoC, expectedDtoA, expectedDtoB);

        // when
        CursorPageResponseInterestDto result = interestService.generateCursorPage(request);

        // then
        verify(interestRepository).findAllInterestByRequest(request);

        assertThat(result.content())
            .isNotNull()
            .hasSize(interests.size())
            .containsExactlyElementsOf(expectedInterestDtos);

        assertThat(result.nextCursor()).isEqualTo(interestC.getId().toString());
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    void 커서값을_기준으로_조회한다() throws Exception {
        // given
        String keyword = "스포츠";
        String orderBy = "name";
        String direction = "desc";
        String cursor = "게임";
        Instant after = null;
        Integer limit = 10;
        UUID userId = UUID.randomUUID();

        CursorPageRequest request = new CursorPageRequest(keyword, orderBy, direction, cursor, after, limit, userId);
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());


        Interest interestB = Interest.builder()
            .name("다큐멘터리").subscriberCount(50L).build();
        ReflectionTestUtils.setField(interestB, "createdAt", now.minusMinutes(20));
        Interest interestC = Interest.builder()
            .name("스포츠").subscriberCount(200L).build();
        ReflectionTestUtils.setField(interestC, "createdAt", now.minusMinutes(5));
        List<Interest> interests = Arrays.asList(interestB, interestC);

        given(interestRepository.findAllInterestByRequest(any(CursorPageRequest.class)))
            .willReturn(interests);


        InterestDto expectedDtoB = InterestDto.builder()
            .id(UUID.nameUUIDFromBytes(interestB.getId().toString().getBytes()))
            .name(interestB.getName())
            .subscriberCount(interestB.getSubscriberCount())
            .keywords(Collections.emptyList())
            .subscribedByMe(false).build();
        InterestDto expectedDtoC = InterestDto.builder()
            .id(UUID.nameUUIDFromBytes(interestC.getId().toString().getBytes()))
            .name(interestC.getName())
            .subscriberCount(interestC.getSubscriberCount())
            .keywords(Collections.emptyList())
            .subscribedByMe(false).build();
        List<InterestDto> expectedInterestDtos = Arrays.asList(expectedDtoB, expectedDtoC);

        // when
        CursorPageResponseInterestDto result = interestService.generateCursorPage(request);

        // then
        verify(interestRepository).findAllInterestByRequest(request);

        assertThat(result.content())
            .isNotNull()
            .hasSize(interests.size())
            .containsExactlyElementsOf(expectedInterestDtos);
        assertThat(result.content().get(0).name())
            .isEqualTo("expectedDtoB");

        assertThat(result.nextCursor()).isEqualTo(interestC.getId().toString());
        assertThat(result.hasNext()).isFalse();

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

    @Test
    void orderBy가_name이거나_subscriberCount가_아닐경우_정상적으로_동작하지_않는다() throws Exception {
        // given
        UUID id = UUID.randomUUID();

        String invalidOrderBy = "invalid";

        // when
    }

    @Test
    void 정렬방향은_ASC이거나_DESC가_아닐경우_정상적으로_동작하지_않는다() throws Exception {
        // given

        // when

        // then

    }

    @Test
    void 커서패이지_크기가_숫자가_아닐경우_정상적으로_동작하지_않는다() throws Exception {
        // given

        // when

        // then

    }

}
