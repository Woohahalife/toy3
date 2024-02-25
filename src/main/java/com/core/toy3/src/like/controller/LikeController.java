package com.core.toy3.src.like.controller;

import com.core.toy3.common.response.Response;
import com.core.toy3.src.like.model.request.LikeRequest;
import com.core.toy3.src.like.model.response.LikeResponse;
import com.core.toy3.src.like.service.LikeService;
import com.core.toy3.src.member.entity.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    @Autowired
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    // 좋아요 추가 또는 취소
    @PostMapping("/like-manager")
    @Operation(description = """
                            좋아요를 추가 또는 취소한다.\t
                            특정 사용자가 이미 좋아요를 누른 게시물이라고 검증할 시 좋아요가 취소된다.
                            """)
    public Response<LikeResponse> createAndCancelLike(@AuthenticationPrincipal AuthMember member,
                                                      @RequestBody LikeRequest likeRequest) {

        return likeService.createAndCancelLike(member,likeRequest);
    }

//    // 특정 사용자가 좋아요를 누른 여행 목록 조회
//    @GetMapping("/list")
//    public Response<List<LikeResponse>> getLikedTravelsByMemberId(
//            @AuthenticationPrincipal AuthMember member) {
//        Long memberId = member.getId();
//        return likeService.getLikedTravelsByMemberId(memberId);
//    }

}
