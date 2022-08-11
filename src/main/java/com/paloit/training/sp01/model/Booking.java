package com.paloit.training.sp01.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @JsonIgnore
    private UUID id;

    @JsonIgnoreProperties("bookings")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonIgnoreProperties("bookings")
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
    private Instant startedAt;
    private Instant endedAt;
}
