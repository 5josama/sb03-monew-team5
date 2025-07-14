package com.sprint5team.monew.domain.comment.service;


import com.sprint5team.monew.domain.comment.dto.CommentDto;
import com.sprint5team.monew.domain.comment.dto.CommentRegisterRequest;

public interface CommentService {

    CommentDto create(CommentRegisterRequest request);
}
