package com.core.toy3.src.like.service;

import com.core.toy3.common.response.Response;
import com.core.toy3.src.like.entity.UserLike;
import com.core.toy3.src.like.model.request.LikeRequest;
import com.core.toy3.src.like.model.response.LikeResponse;
import com.core.toy3.src.like.repository.UserLikeRepository;
import com.core.toy3.src.member.entity.AuthMember;
import com.core.toy3.src.member.entity.Member;
import com.core.toy3.src.travel.entity.Travel;
import com.core.toy3.src.travel.repository.TravelRepository;
import com.core.toy3.src.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LikeService {

    private final UserLikeRepository likeRepository;
    private final TravelRepository travelRepository;
    private final MemberRepository memberRepository;

    public LikeService(UserLikeRepository likeRepository, TravelRepository travelRepository, MemberRepository memberRepository) {
        this.likeRepository = likeRepository;
        this.travelRepository = travelRepository;
        this.memberRepository = memberRepository;
    }

    //좋아요 추가 및 취소 메서드
    @Transactional
    public Response<LikeResponse> createAndCancelLike(AuthMember member, LikeRequest likeRequest) {

        Travel travel = travelRepository.findById(likeRequest.getTravelId())
                .orElseThrow(() -> new RuntimeException("Travel not found with ID: " + likeRequest.getTravelId()));


        UserLike existingLike = likeRepository.findByMemberAndTravel(member.getMember(), travel);

        if (existingLike == null) {
            // 좋아요가 없으면 새로 생성합니다.
            UserLike newLike = UserLike.builder()
                    .member(member.getMember())
                    .travel(travel)
                    .build();
            likeRepository.save(newLike);
            return Response.response(LikeResponse.fromEntity(newLike));
        } else {
            // 이미 좋아요가 있는 경우 취소합니다.
            likeRepository.delete(existingLike);
            return Response.response(LikeResponse.fromEntity(existingLike));
        }
    }
//    //좋아요를 누른 여행 목록을 가져오는 메서드
//    @Transactional
//    public Response<List<LikeResponse>> getLikedTravelsByMemberId(Long memberId) {
//        //memberId에 해당하는 Member를 조회
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new RuntimeException("Member not found with ID: " + memberId));
//
//        // Member가 좋아요를 누른 목록을 조회
//        List<UserLike> likes = likeRepository.findAllByMember(member);
//
//        // LikeResponse로 변환하여 응답
//        List<LikeResponse> likeResponses = likes.stream()
//                .map(LikeResponse::fromEntity)
//                .collect(Collectors.toList());
//
//        return Response.response(likeResponses);
//    }
}
