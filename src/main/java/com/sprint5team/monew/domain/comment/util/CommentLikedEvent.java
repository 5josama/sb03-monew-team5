package com.sprint5team.monew.domain.comment.util;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CommentLikedEvent {
    private final UUID commentOwnerId;
    private final UUID commentId;
    private final String likerNickname;

    public CommentLikedEvent(UUID commentOwnerId, UUID commentId, String likerNickname) {
        this.commentOwnerId = commentOwnerId;
        this.commentId = commentId;
        this.likerNickname = likerNickname;
    }

}
