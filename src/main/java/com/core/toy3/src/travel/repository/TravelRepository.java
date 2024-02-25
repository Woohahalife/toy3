package com.core.toy3.src.travel.repository;

import com.core.toy3.common.constant.State;
import com.core.toy3.src.travel.entity.Travel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TravelRepository extends JpaRepository<Travel, Long> {

    @Query("""
            select tv
            from Travel tv
            left join fetch tv.trip t
            where tv.state = 'ACTIVE'
            and (t is null or t.state = 'ACTIVE')
            order by tv.id asc, t.postedAt asc
            """)
    List<Travel> getAllTravelActive();

    @Query("""
            select tv
            from Travel tv
            left join fetch tv.trip t
            left join fetch tv.likes l
            where tv.state = 'ACTIVE'
            and (t is null or t.state = 'ACTIVE')
            and l.member.id =:memberId
            """)
    List<Travel> getAllTravelActiveAndMyLike(@Param("memberId") Long id);

    @Query("""
            select tv
            from Travel tv
            left join fetch tv.trip t
            where tv.id = :travel_id
            and tv.state = 'ACTIVE'
            and (t is null or t.state = 'ACTIVE')
            """)
    Optional<Travel> getTravelActive(@Param("travel_id") Long id);

    @Query("""
            update Travel tv
            set tv.state = 'DELETE'
            where tv.id = :travel_id
            and tv.state = 'ACTIVE'
            """)
    @Modifying
    Optional<Integer> deleteTravelById(@Param("travel_id") Long id);

    @Query("""
            select tv
            from Travel tv
            where tv.id = :travel_id
            """)
    Optional<Travel> findTravel(@Param("travel_id") Long id);

    @Query("""
            select tv
            from Travel tv
            left join fetch tv.trip t
            where (t is null or t.state = 'ACTIVE')
            and trim(tv.travelName) like concat('%',trim(:keyword), '%')
            order by tv.id asc, t.postedAt asc
            """)
    List<Travel> getTravelSearchByTravelName(@Param("keyword") String keyword);

    @Query("""
            select tv
            from Travel tv
            left join fetch tv.trip t
            where (t is null or t.state = 'ACTIVE')
            and trim(tv.departure) like concat('%',trim(:keyword), '%')
            order by tv.id asc, t.postedAt asc
            """)
    List<Travel> getTravelSearchByDeparture(@Param("keyword") String keyword);

    @Query("""
            select tv
            from Travel tv
            left join fetch tv.trip t
            where (t is null or t.state = 'ACTIVE')
            and trim(tv.arrival) like concat('%',trim(:keyword), '%')
            order by tv.id asc, t.postedAt asc
            """)
    List<Travel> getTravelSearchByArrival(@Param("keyword") String keyword);
}

