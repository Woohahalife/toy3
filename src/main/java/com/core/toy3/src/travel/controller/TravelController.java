package com.core.toy3.src.travel.controller;

import com.core.toy3.common.response.Response;
import com.core.toy3.src.like.repository.UserLikeRepository;
import com.core.toy3.src.member.entity.AuthMember;
import com.core.toy3.src.travel.model.request.TravelRequest;
import com.core.toy3.src.travel.model.response.TravelResponse;
import com.core.toy3.common.util.KakaoMapLocation;
import com.core.toy3.src.travel.service.TravelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/travel")
public class TravelController {

    private final TravelService travelService;
    private final UserLikeRepository likeRepository;

    @PostMapping("/create")
    @Operation(description = "여행 데이터를 생성한다.")
    public Response<TravelResponse> createTravel(@RequestBody TravelRequest travelRequest,
                                                 @AuthenticationPrincipal AuthMember authMember) {

        return Response.response(travelService.createTravel(travelRequest, authMember));
    }

    @GetMapping("/read")
    @Operation(description = "모든 여행 데이터를 조회한다.")
    public Response<List<TravelResponse>> getAllTravel() {

        List<TravelResponse> travelResponseList = travelService.getAllTravel();

        return Response.response(travelResponseList);
    }

    @GetMapping("/read/like")
    @Operation(description = "유저가 좋아요 누른 여행 데이터를 조회한다.")
    public Response<List<TravelResponse>> getAllTravelLikes(@AuthenticationPrincipal AuthMember authMember) {

        return Response.response(travelService.getAllTravelLikes(authMember));
    }

    @GetMapping("/read/{travelId}")
    @Operation(description = "단일 여행 데이터를 조회한다.")
    public Response<TravelResponse> selectTravel(@PathVariable("travelId") Long id) {

        return Response.response(travelService.selectTravel(id));
    }

    @GetMapping("/read/search")
    @Operation(description = """
                            여행 데이터를 검색한다.\t
                            데이터 검색 기준은 travelName, departure, arrival3가지이다.\t
                            셋 중 하나의 기준을 사용해 검색이 가능하다.\t
                            클라이언트에서 select box를 활용해 검색 기준을 정한다는 것을 가정했기 때문에\t
                            2개 이상의 데이터를 추가해 테스트할 시 빈 데이터가 출력된다.
                            """)
    public Response<List<TravelResponse>> searchTravel(
            @RequestParam(value = "travelName", required = false) String travelName,
            @RequestParam(value = "departure", required = false) String departure,
            @RequestParam(value = "arrival", required = false) String arrival) {

        List<TravelResponse> travelSearchList = travelService.getAllTravel(); // 검색 안할경우 전체 리스트 출력

        if (travelName != null && !travelName.trim().isEmpty()) {
            System.out.println("travelName = " + travelName);
            travelSearchList = travelService.SearchTravelByTravelName(travelName);
        }

        if (departure != null && !departure.trim().isEmpty()) {
            System.out.println("departure = " + departure);
            travelSearchList = travelService.SearchTravelByDeparture(departure);
        }

        if (arrival != null && !arrival.trim().isEmpty()) {
            System.out.println("arrival = " + arrival);
            travelSearchList = travelService.SearchTravelByArrival(arrival);
        }

        return Response.response(travelSearchList);
    }

    @PutMapping("/update-travel/{travelId}")
    @Operation(description = "단일 여행 데이터를 수정한다(사용자 검증 필요)")
    public Response<TravelResponse> updateTravel(
            @PathVariable("travelId") long id,
            @RequestBody TravelRequest travelRequest,
            @AuthenticationPrincipal AuthMember authMember) {

        return Response.response(travelService.updateTravel(id, travelRequest, authMember));
    }

    @DeleteMapping("/delete-travel/{travelId}")
    @Operation(description = """
                            단일 여행 데이터를 삭제한다(사용자 검증 필요)\t
                            데이터는 실제로 삭제되는 것이 아닌 ACTIVE -> DELETE상태로 변경한다.
                            여행 데이터 삭제 처리시 여정 데이터에도 접근할 수 없다.
                            """)
    public Response<String> deleteTravel(@PathVariable("travelId") Long id,
                                         @AuthenticationPrincipal AuthMember authMember) {

        Integer result = travelService.deleteTravel(id, authMember);

        String resultMessage = result == 1 ? "삭제가 완료되었습니다." : "삭제에 실패했습니다.";

        return Response.response(resultMessage);
    }
}
