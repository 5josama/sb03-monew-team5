package com.sprint5team.monew.domain.comment.service;


import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.exception.ArticleNotFoundException;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.comment.dto.CommentDto;
import com.sprint5team.monew.domain.comment.dto.CommentRegisterRequest;
import com.sprint5team.monew.domain.comment.dto.CursorPageResponseCommentDto;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.mapper.CommentMapper;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    /**
     * 댓글을 생성하는 메소드
     * @param request 댓글 생성 요청
     * @return 댓글 생성 결과 CommentDto
     */
    @Override
    public CommentDto create(CommentRegisterRequest request) {
        log.debug("댓글 생성 시작: articleId={}, userId={}, content={}",request.articleId(),request.userId(),request.content());

        Article article = articleRepository.findById(request.articleId()) // Article이 없을경우 예외발생
                .orElseThrow(ArticleNotFoundException::new);

        //TODO UserNotFound 예외 리팩토링시 수정예정
        User user = userRepository.findById(request.userId())   // User가 없을경우 예외발생
                .orElseThrow(ArticleNotFoundException::new);

        Comment comment = new Comment(article, user, request.content());
        Comment createdComment = commentRepository.save(comment);

        log.info("댓글 생성 완료: commentId={}, content={}",createdComment.getId(),createdComment.getContent());
        return commentMapper.toDto(createdComment);
    }

    @Override
    public CursorPageResponseCommentDto find(UUID articleId, String cursor, Instant after, Pageable pageable) {
        //커서페이지네이션 수행
        List<Comment> commentList = commentRepository.findCommentsWithCursor(articleId, cursor, after, pageable);

        //totalElements 계산 로직
        Long totalElements = commentRepository.countTotalElements(articleId);

        //커서게산을 위한 lastIndex 검색 로직 (페이징할때 원하는 사이즈보다 1 더 큰 사이즈를 가져와서 마지막인덱스를 커서로 사용하기 위함)
        Comment lastIndex = commentList.get(commentList.size() - 1);
        String nextCursor = null;


        //hasNext 판단 로직
        boolean hasNext = false;
        if(commentList.size() == pageable.getPageSize()) {
            hasNext = true;

            //lastIndex의 커서를 확인하는 로직(hasNext가 존재할경우만 판단)
            if(isInstantCursor(cursor)){
                nextCursor = lastIndex.getCreatedAt().toString();
            }else if (isLongCursor(cursor)){
                nextCursor = lastIndex.getLikeCount().toString();
            }
        }

        //마지막 인덱스를 제외한 comments를 반환하는 로직
        commentList.remove(commentList.size()-1); //마지막 인덱스 제거

        List<CommentDto> list = commentList.stream()
                .map(commentMapper::toDto)
                .toList();

        CursorPageResponseCommentDto response = new CursorPageResponseCommentDto(
                list,
                nextCursor,
                lastIndex.getCreatedAt(),
                pageable.getPageSize()-1,
                totalElements,
                hasNext
        );

        return response;
    }

    /**
     * Cursor가 CreatedAt(Instant)인지 판단하는 로직
     * @param cursor 커서
     * @return true: Instant, false: Long
     */
    private boolean isInstantCursor(String cursor) {
        try {
            Instant.parse(cursor);  // ISO-8601 형태 파싱 시도
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Cursor가 likeCount(Long)인지 판단하는 로직
     * @param cursor 커서
     * @return true: Long, false: Instant
     */
    private boolean isLongCursor(String cursor) {
        try {
            Long.parseLong(cursor);  // 숫자 파싱 시도
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
