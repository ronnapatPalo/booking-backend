package com.paloit.training.sp01.repository;

import com.paloit.training.sp01.model.Room;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.paloit.training.sp01.model.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, UUID> {
    @Query("SELECT u FROM User u WHERE u.email=:email")
    Optional<User> findUserByEmail(@Param("email") String email);
}
