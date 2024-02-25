package com.core.toy3.src.like.entity;

import com.core.toy3.src.travel.entity.Travel;
import com.core.toy3.src.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "travel_id")
    private Travel travel;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public static UserLike createUserLike(Travel travel) {
        return UserLike.builder()
                .travel(travel)
                .build();
    }
}
