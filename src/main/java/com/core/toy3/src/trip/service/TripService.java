package com.core.toy3.src.trip.service;

import com.core.toy3.common.util.KakaoMapLocation;
import com.core.toy3.common.constant.State;
import com.core.toy3.common.exception.CustomException;
import com.core.toy3.src.member.entity.AuthMember;
import com.core.toy3.src.travel.entity.Travel;
import com.core.toy3.src.travel.repository.TravelRepository;
import com.core.toy3.src.trip.entity.Trip;
import com.core.toy3.src.trip.model.request.TripRequest;
import com.core.toy3.src.trip.model.request.TripUpdateRequest;
import com.core.toy3.src.trip.model.response.TripResponse;
import com.core.toy3.src.trip.repository.TripRepository;
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
public class TripService {

    private final TravelRepository travelRepository;
    private final TripRepository tripRepository;
    private final KakaoMapLocation kakaoMapLocation;

    @Transactional
    public TripResponse createTrip(Long travelId, TripRequest tripRequest, AuthMember member) {

        Travel travel = getExistTravel(travelId);
        validateLimitedAccess(travel); // 이미 삭제된 상위 엔티티에 대해서는 접근 제한
        validateMatches(member, travel); // 사용자 자신의 travel에만 trip을 추가할 수 있어야함

        // 입력받은 주소 kakao 주소로 받아옴
        tripRequest.setLocation(
                setTripLocationWithKakao(tripRequest.getLocation())
        );

        Trip trip = fromRequestToTripEntity(travel, tripRequest);

        validPostedAt(travel, trip);

        return TripResponse.toResult(tripRepository.save(trip));
    }

    @Transactional
    public List<TripResponse> getAllTripFromTravel(Long travelId) {

        Travel travel = getExistTravel(travelId);
        validateLimitedAccess(travel);// 이미 삭제된 상위 엔티티에 대해서는 접근 제한

        List<Trip> tripActive = tripRepository.getAllTripActiveFromTravel(travelId); // 선택한 여행의 모든 여정만 조회

        return tripActive.stream()
                .map(TripResponse::toResult)
                .collect(Collectors.toList());
    }

    @Transactional
    public TripResponse getTrip(Long travelId, Long tripId) {

        Travel travel = getExistTravel(travelId);
        validateLimitedAccess(travel);

        Trip trip = getExistTrip(travelId, tripId);

        return TripResponse.toResult(trip);
    }

    @Transactional
    public TripResponse updateTrip(Long tripId, Long travelId, TripUpdateRequest tripRequest, AuthMember member) {

        Travel travel = getExistTravel(travelId);
        validateLimitedAccess(travel);

        // 입력받은 주소 kakao 주소로 받아옴
        tripRequest.setLocation(
                setTripLocationWithKakao(tripRequest.getLocation())
        );

        Trip trip = getExistTrip(travelId, tripId);

        validateMatches(member, travel);

        trip.update(tripRequest);

        validPostedAt(travel, trip); // update이후 시점에 여정 시간과 여행 시간이 논리적으로 맞는지 검증

        return TripResponse.toResult(trip);
    }

    @Transactional
    public Integer deleteTrip(Long tripId, Long travelId, AuthMember member) {

        Travel travel = getExistTravel(travelId);
        validateLimitedAccess(travel);
        validateMatches(member, travel);

        return tripRepository.deleteTripById(tripId) // 더티체킹 없이 JPQL로 바로 변환
                .filter(result -> result == 1)
                .orElseThrow(() -> new CustomException(DELETE_IS_FAIL));

    }

    private Travel getExistTravel(Long travelId) {
        return travelRepository.findTravel(travelId)
                .orElseThrow(() -> new CustomException(TRIP_DOES_NOT_EXIST));
    }

    private Trip getExistTrip(Long travelId, Long tripId) {
        return tripRepository.getTripById(travelId, tripId)
                .orElseThrow(() -> new CustomException(TRIP_DOES_NOT_EXIST));
    }

    private Trip fromRequestToTripEntity(Travel travel, TripRequest tripRequest) {

        return Trip.builder()
                .location(tripRequest.getLocation())
                .state(tripRequest.getState())
                .postedAt(tripRequest.getPostedAt())
                .travel(travel)
                .build();
    }

    private void validateMatches(AuthMember member, Travel travel) {
        if(!Objects.equals(travel.getMember().getId(), member.getId())) {
            throw new CustomException(HAS_NOT_PERMISSION_TO_ACCESS);
        }
    }

    private void validPostedAt(Travel travel, Trip trip) {

        LocalDateTime postedAt = trip.getPostedAt();

        if(travel.getArrivalTime().isBefore(postedAt) || travel.getDepartureTime().isAfter(postedAt)) {
            throw new CustomException(POSTAT_IS_INCORRECT,
                    """
                    | 여정 시간이 여행의 도착 시간보다 이후의 시간이거나
                    | 여행의 출발 시간보다 이전의 시간으로 설정되어 논리상 맞지 않습니다.
                    """);
        }
    }

    private void validateLimitedAccess(Travel travel) {
        if(travel.getState() == State.DELETE) {
            throw new CustomException(HAS_NOT_PERMISSION_TO_ACCESS,"""
                    | 삭제 상태인 여행 게시물에
                    | 여정 정보를 등록, 수정 및 삭제할 수 없습니다.
                    """);
        }
    }

    private String setTripLocationWithKakao(String location){
        String kakaoLocation = kakaoMapLocation.getLocation(location);
        if (kakaoLocation!=null){
            return kakaoLocation;
        } else {
            return location;
        }
    }

}
