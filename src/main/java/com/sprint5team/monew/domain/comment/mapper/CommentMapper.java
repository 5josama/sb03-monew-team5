package com.sprint5team.monew.domain.comment.mapper;


import com.sprint5team.monew.domain.comment.dto.CommentDto;
import com.sprint5team.monew.domain.comment.dto.CommentLikeDto;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.entity.Like;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.comment.repository.LikeRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Mapping(target = "articleId", source = "article.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userNickname", source = "user.nickname")
    @Mapping(target = "likedByMe", expression = "java(getLikedByMe(comment))")
    abstract public CommentDto toDto(Comment comment);

    protected Boolean getLikedByMe(Comment comment) {
        List<Like> likeList = likeRepository.findAllByUserIdAndCommentId(comment.getUser().getId(), comment.getId());
        if (!likeList.isEmpty()) {
            return true;
        }
        return false;
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

}
