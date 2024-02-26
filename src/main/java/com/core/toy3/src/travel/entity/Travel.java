package com.core.toy3.src.travel.entity;

import com.core.toy3.common.constant.State;
import com.core.toy3.common.entity.BaseEntity;
import com.core.toy3.src.comment.entity.Comment;
import com.core.toy3.src.like.entity.UserLike;
import com.core.toy3.src.member.entity.Member;
import com.core.toy3.src.travel.model.request.TravelRequest;
import com.core.toy3.src.trip.entity.Trip;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Travel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "travel_name")
    private String travelName;

    @Enumerated(EnumType.STRING)
    private State state;

    private String departure;
    private String arrival;

    @Column(name = "departure_time")
    private LocalDateTime departureTime;

    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;

    @Builder.Default
    @OneToMany(mappedBy = "travel", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Trip> trip = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "travel", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Comment> comment = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "travel")
    private List<UserLike> likes = new ArrayList<>();

//    public static Travel fromRequest(TravelRequest travelRequest) {
//        return Travel.builder()
//                .travelName(travelRequest.getTravelName())
//                .state(State.ACTIVE)
//                .departure(travelRequest.getDeparture())
//                .arrival(travelRequest.getArrival())
//                .departureTime(travelRequest.getDepartureTime())
//                .arrivalTime(travelRequest.getArrivalTime())
//                .build();
//    }

    public void update(TravelRequest travelRequest) {

        this.travelName = travelRequest.getTravelName();
        this.departure = travelRequest.getDeparture();
        this.arrival = travelRequest.getArrival();
        this.departureTime = travelRequest.getDepartureTime();
        this.arrivalTime = travelRequest.getArrivalTime();
    }

    public int getLikeCount() {
        return likes != null ? likes.size() : 0;
    }
}

