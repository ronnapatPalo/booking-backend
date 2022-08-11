package com.paloit.training.sp01.repository;

import com.paloit.training.sp01.model.Booking;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends PagingAndSortingRepository<Booking, UUID> {
    @Query("SELECT bk FROM Booking bk WHERE bk.room.id = :roomId AND bk.startedAt >= :startedAt AND bk.endedAt <= :endedAt")
    List<Booking> findBooking(
            @Param("roomId") UUID roomId,
            @Param("startedAt") Instant startedAt,
            @Param("endedAt") Instant endedAt
    );

}
