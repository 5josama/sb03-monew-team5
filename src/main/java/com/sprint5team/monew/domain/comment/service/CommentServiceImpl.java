package com.sprint5team.monew.domain.comment.service;


import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.exception.ArticleNotFoundException;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.comment.dto.CommentDto;
import com.sprint5team.monew.domain.comment.dto.CommentRegisterRequest;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.mapper.CommentMapper;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.exception.UserNotFoundException;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        User user = userRepository.findById(request.userId())   // User가 없을경우 예외발생 (수정해야함)
                .orElseThrow(ArticleNotFoundException::new);

        Comment comment = new Comment(article, user, request.content());
        Comment createdComment = commentRepository.save(comment);

        log.info("댓글 생성 완료: commentId={}, content={}",createdComment.getId(),createdComment.getContent());
        return commentMapper.toDto(createdComment);
    }

}
