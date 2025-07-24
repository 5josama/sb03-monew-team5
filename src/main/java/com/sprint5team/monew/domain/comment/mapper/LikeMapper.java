package com.sprint5team.monew.domain.comment.mapper;


import com.sprint5team.monew.domain.comment.dto.CommentLikeDto;
import com.sprint5team.monew.domain.comment.entity.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class LikeMapper {


    @Mapping(target = "id", source = "like.id")
    @Mapping(target = "likedBy", source = "like.user.id")
    @Mapping(target = "createdAt", source = "like.createdAt")
    @Mapping(target = "commentId", source = "like.comment.id")
    @Mapping(target = "articleId", source = "like.comment.article.id")
    @Mapping(target = "commentUserId", source = "like.comment.user.id")
    @Mapping(target = "commentUserNickname", source = "like.comment.user.nickname")
    @Mapping(target = "commentContent", source = "like.comment.content")
    @Mapping(target = "commentLikeCount", source = "like.comment.likeCount")
    @Mapping(target = "commentCreatedAt", source = "like.comment.createdAt")
    abstract public CommentLikeDto toDto(Like like);



}
