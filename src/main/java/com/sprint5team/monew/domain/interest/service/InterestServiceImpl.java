package com.sprint5team.monew.domain.interest.service;

import com.sprint5team.monew.domain.interest.dto.CursorPageRequest;
import com.sprint5team.monew.domain.interest.dto.CursorPageResponseInterestDto;
import com.sprint5team.monew.domain.interest.dto.InterestDto;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.interest.repository.InterestRepositoryImpl;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import com.sprint5team.monew.domain.user_interest.entity.UserInterest;
import com.sprint5team.monew.domain.user_interest.repository.UserInterestRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * PackageName  : com.sprint5team.monew.domain.interest.service
 * FileName     : InterestService
 * Author       : dounguk
 * Date         : 2025. 7. 11.
 */
@Validated
@Service
@RequiredArgsConstructor
public class InterestServiceImpl implements InterestService{
    private final InterestRepository interestRepository;

    private final KeywordRepository keywordRepository;

    private final UserInterestRepository userInterestRepository;

    public CursorPageResponseInterestDto generateCursorPage(@Valid CursorPageRequest request) {

        // contents
        List<Interest> contents = interestRepository.findAllInterestByRequest(request);

        Set<UserInterest> userInterests = userInterestRepository.findByUserId(request.getUserId());

        Set<InterestDto> contentDtos = new HashSet<>();
        for (Interest interest : contents){
            List<String> keywordNames = keywordRepository.findAllByInterest(interest).stream().map(Keyword::getName).toList();
            boolean subscribedByMe = false;
            if(userInterests.contains(interest)){
                subscribedByMe = true;
            }
            InterestDto interestDto = new InterestDto(interest.getId(), interest.getName(), keywordNames, interest.getSubscriberCount(), subscribedByMe);
            contentDtos.add(interestDto);
        }


        return null;
    }
}
