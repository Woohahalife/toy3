package com.core.toy3.src.travel.service;

import com.core.toy3.common.util.KakaoMapLocation;
import com.core.toy3.common.constant.State;
import com.core.toy3.common.exception.CustomException;
import com.core.toy3.common.response.Response;
import com.core.toy3.src.member.entity.AuthMember;
import com.core.toy3.src.travel.entity.Travel;
import com.core.toy3.src.travel.model.request.TravelRequest;
import com.core.toy3.src.travel.model.response.TravelResponse;
import com.core.toy3.src.travel.repository.TravelRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static com.core.toy3.common.response.ResponseStatus.*;

@Service
@RequiredArgsConstructor
public class TravelService {

    private final TravelRepository travelRepository;
    private final KakaoMapLocation kakaoMapLocation;

    @Transactional
    public TravelResponse createTravel(TravelRequest travelRequest,
                                       AuthMember member) {

        travelRequest = setRequestLocationWithKakao(travelRequest);

        Travel travel = getTravelFromRequest(travelRequest, member);

        return TravelResponse.toResult(travelRepository.save(travel));
    }

    private Travel getTravelFromRequest(TravelRequest travelRequest, AuthMember member) {

        validateTravelDepartureTime(travelRequest);

        return Travel.builder()
                .travelName(travelRequest.getTravelName())
                .state(State.ACTIVE)
                .departure(travelRequest.getDeparture())
                .arrival(travelRequest.getArrival())
                .departureTime(travelRequest.getDepartureTime())
                .arrivalTime(travelRequest.getArrivalTime())
                .member(member.getMember())
                .build();
    }

    private void validateTravelDepartureTime(TravelRequest travelRequest) {

        LocalDateTime arrivalTime = travelRequest.getArrivalTime();

        if(!arrivalTime.isAfter(travelRequest.getDepartureTime()) ||
           !arrivalTime.isAfter(LocalDateTime.now())) {

            throw new CustomException(ARRIVALTIME_IS_INCORRECT);
        }
    }

//    private void validateUserInfo(AuthMember member) {
//        if(member == null) {
//            throw new CustomException(USERINFO_IS_NOTFOUND);
//        }
//    }

    @Transactional
    public List<TravelResponse> getAllTravel() {

        // state가 ACTIVE인 데이터만 조회
        List<Travel> travelActive = travelRepository.getAllTravelActive();

        // Travel엔티티 list를 stream을 활용해 TravelResponse로 구성해서 리턴
        return travelActive.stream()
                .map(TravelResponse::toResult)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<TravelResponse> getAllTravelLikes(AuthMember authMember) {

        Long memberId = authMember.getId();
        List<Travel> allTravelActiveAndMyLike = travelRepository.getAllTravelActiveAndMyLike(memberId);

        return allTravelActiveAndMyLike.stream()
                .map(TravelResponse::toResult)
                .collect(Collectors.toList());
    }

    @Transactional
    public TravelResponse selectTravel(Long id) {

        Travel travel = travelRepository.getTravelActive(id)
                .orElseThrow(() -> new CustomException(TRAVEL_DOES_NOT_EXIST));

        return TravelResponse.toResult(travel);
    }

    // travelName 기준으로 검색
    @Transactional
    public List<TravelResponse> SearchTravelByTravelName(String keyword) {

        List<Travel> travelSearchByTravelName =
                travelRepository.getTravelSearchByTravelName(keyword);
        return travelSearchByTravelName
                .stream()
                .map(TravelResponse::toResult).collect(Collectors.toList());
    }

    @Transactional
    public List<TravelResponse> SearchTravelByDeparture(String keyword) {

        List<Travel> travelSearchByTravelName =
                travelRepository.getTravelSearchByDeparture(keyword);
        return travelSearchByTravelName
                .stream()
                .map(TravelResponse::toResult).collect(Collectors.toList());
    }

    @Transactional
    public List<TravelResponse> SearchTravelByArrival(String keyword) {

        List<Travel> travelSearchByTravelName =
                travelRepository.getTravelSearchByArrival(keyword);
        return travelSearchByTravelName
                .stream()
                .map(TravelResponse::toResult).collect(Collectors.toList());
    }

    @Transactional
    public TravelResponse updateTravel(long id, TravelRequest travelRequest, AuthMember member) {

        Travel travel = travelRepository.getTravelActive(id)
                .orElseThrow(() -> new CustomException(TRAVEL_DOES_NOT_EXIST));

        travelRequest = setRequestLocationWithKakao(travelRequest);
        validateMatches(member, travel);

        travel.update(travelRequest);

        return TravelResponse.toResult(travel);
    }

    @Transactional
    public Integer deleteTravel(Long id, AuthMember member) {

        Travel travel = travelRepository.getTravelActive(id)
                .orElseThrow(() -> new CustomException(TRAVEL_DOES_NOT_EXIST));

        validateMatches(member, travel);

        return travelRepository.deleteTravelById(id)
                .filter(result -> result == 1) // 1이 return 될 경우 삭제완료
                .orElseThrow(() -> new CustomException(DELETE_IS_FAIL));

    }



    // 게시물의 주인이 다른 경우 예외 발생(수정, 삭제)
    private void validateMatches(AuthMember member, Travel travel) {
        if(!Objects.equals(travel.getMember().getId(), member.getId())) {
            throw new CustomException(HAS_NOT_PERMISSION_TO_ACCESS);
        }
    }
    @Transactional
    public TravelResponse selectTravelLikes(Long id) {
        Travel travel = travelRepository.getTravelActive(id)
                .orElseThrow(() -> new CustomException(TRAVEL_DOES_NOT_EXIST));

        return TravelResponse.toResult(travel);
    }

    private TravelRequest setRequestLocationWithKakao(TravelRequest travelRequest){
        // departure 주소 받아오기
        String departureLocation = kakaoMapLocation.getLocation(
                travelRequest.getDeparture()
        );
        if (departureLocation!=null){
            travelRequest.setDeparture(departureLocation);
        }

        // arrival 주소 받아오기
        String arrivalLocation = kakaoMapLocation.getLocation(
                travelRequest.getArrival()
        );
        if (arrivalLocation!=null){
            travelRequest.setArrival(arrivalLocation);
        }
        return travelRequest;
    }

}
