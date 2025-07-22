package com.sprint5team.monew.domain.comment.mapper;


import com.sprint5team.monew.domain.comment.dto.CommentActivityDto;
import com.sprint5team.monew.domain.comment.dto.CommentDto;
import com.sprint5team.monew.domain.comment.dto.CommentLikeActivityDto;
import com.sprint5team.monew.domain.comment.dto.CommentLikeDto;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.entity.Like;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.comment.repository.LikeRepository;
import java.util.List;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "articleId", source = "comment.article.id")
    @Mapping(target = "userId", source = "comment.user.id")
    @Mapping(target = "userNickname", source = "comment.user.nickname")
    @Mapping(target = "likedByMe", expression = "java(getLikedByMe(nowUserId,comment))")
    @Mapping(target = "content", source = "comment.content")
    @Mapping(target = "likeCount", source = "comment.likeCount")
    @Mapping(target = "createdAt", source = "comment.createdAt")
    abstract public CommentDto toDto(UUID nowUserId,Comment comment);

    protected Boolean getLikedByMe(UUID nowUserId, Comment comment) {
        return likeRepository.findByUserIdAndCommentId(nowUserId, comment.getId()).isPresent();
    }

    @Mapping(target = "id", source = "like.id")
    @Mapping(target = "likedBy", source = "like.user.id")
    @Mapping(target = "commentId", source = "like.comment.id")
    @Mapping(target = "articleId", source = "like.comment.article.id")
    @Mapping(target = "commentUserId", source = "like.comment.user.id")
    @Mapping(target = "commentUserNickname", source = "like.comment.user.nickname")
    @Mapping(target = "commentContent", source = "like.comment.content")
    @Mapping(target = "commentLikeCount", source = "like.comment.likeCount")
    @Mapping(target = "commentCreatedAt", source = "like.comment.createdAt")
    abstract public CommentLikeDto toDto(Like like);

    // 사용자 활동 내역 조회 시 사용하는 Dto
    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "articleId", source = "comment.article.id")
    @Mapping(target = "articleTitle", source = "comment.article.title")
    @Mapping(target = "userId", source = "comment.user.id")
    @Mapping(target = "userNickname", source = "comment.user.nickname")
    @Mapping(target = "content", source = "comment.content")
    @Mapping(target = "likeCount", source = "comment.likeCount")
    @Mapping(target = "createdAt", source = "comment.createdAt")
    abstract public CommentActivityDto toActivityDto(Comment comment);

    // 사용자 활동 내역 조회 시 사용하는 Dto
    @Mapping(target = "id", source = "like.id")
    @Mapping(target = "createdAt", source = "like.createdAt")
    @Mapping(target = "commentId", source = "like.comment.id")
    @Mapping(target = "articleId", source = "like.comment.article.id")
    @Mapping(target = "articleTitle", source = "like.comment.article.title")
    @Mapping(target = "commentUserId", source = "like.comment.user.id")
    @Mapping(target = "commentUserNickname", source = "like.comment.user.nickname")
    @Mapping(target = "commentContent", source = "like.comment.content")
    @Mapping(target = "commentLikeCount", source = "like.comment.likeCount")
    @Mapping(target = "commentCreatedAt", source = "like.comment.createdAt")
    abstract public CommentLikeActivityDto toActivityDto(Like like);

}
