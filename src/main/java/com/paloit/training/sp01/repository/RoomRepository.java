package com.paloit.training.sp01.repository;
import com.paloit.training.sp01.model.Room;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface RoomRepository extends PagingAndSortingRepository<Room, UUID> {
    @Query("SELECT r FROM Room r LEFT JOIN r.bookings bk WHERE (r.size >= :roomSize AND (bk IS NULL OR :startedAt >= bk.endedAt OR :endedAt <= bk.startedAt))")
    List<Room> findAvailableRooms(@Param("roomSize") Integer roomSize, @Param("startedAt") Instant startedAt, @Param("endedAt") Instant endedAt);
}