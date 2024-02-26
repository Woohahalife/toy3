package com.core.toy3.src.trip.entity;

import com.core.toy3.common.constant.State;
import com.core.toy3.common.entity.BaseEntity;
import com.core.toy3.src.travel.entity.Travel;
import com.core.toy3.src.trip.model.request.TripUpdateRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Trip extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String location;

    @Enumerated(EnumType.STRING)
    private State state;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "travel_id")
    private Travel travel;

    public void update(TripUpdateRequest request) {
        this.location = request.getLocation();
        this.postedAt = request.getPostedAt();
    }
}
