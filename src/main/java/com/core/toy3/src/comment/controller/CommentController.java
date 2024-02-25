package com.core.toy3.src.comment.controller;

import com.core.toy3.common.response.Response;
import com.core.toy3.src.comment.entity.Comment;
import com.core.toy3.src.comment.model.request.CommentRequest;
import com.core.toy3.src.comment.model.response.CommentResponse;
import com.core.toy3.src.comment.service.CommentService;
import com.core.toy3.src.member.entity.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/comments")
//@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts")
    @Operation(description = "댓글 생성 API")
    public Response<CommentResponse> saveComment(
            @AuthenticationPrincipal AuthMember member,
            @RequestBody CommentRequest commentRequest
    ) {
         Comment comment = commentService.createComment(member, commentRequest);

        return Response.response(CommentResponse.fromEntity(comment));

    }

    @GetMapping("/list")
    @Operation(description = "특정 사용자가 남긴 댓글목록을 조회한다.")
    public List<CommentResponse> viewComments(
            @AuthenticationPrincipal AuthMember member){

        List<Comment> commentList = commentService.selectAllCommentByMe(member);
        List<CommentResponse> commentResponseList = new ArrayList<>();

        for(Comment comment:commentList){
            commentResponseList.add(CommentResponse.fromEntity(comment));
        }
        return commentResponseList;
    }

}
