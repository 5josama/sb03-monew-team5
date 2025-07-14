package com.sprint5team.monew.domain.comment.mapper;


import com.sprint5team.monew.domain.comment.dto.CommentDto;
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


}
