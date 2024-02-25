package com.core.toy3.src.like.repository;


import com.core.toy3.src.like.entity.UserLike;
import com.core.toy3.src.member.entity.Member;
import com.core.toy3.src.travel.entity.Travel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLikeRepository extends JpaRepository<UserLike, Long> {
    UserLike findByMember(Member member);
    UserLike findByTravel(Travel travel);
    List<UserLike> findAllByMember(Member member);

    UserLike findByMemberAndTravel(Member member, Travel travel);
}
