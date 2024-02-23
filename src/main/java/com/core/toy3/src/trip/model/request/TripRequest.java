package com.core.toy3.src.trip.model.request;

import com.core.toy3.common.constant.State;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TripRequest {

    private String location;
    private State state;
    private LocalDateTime postedAt;
}
