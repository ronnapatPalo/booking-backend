package com.paloit.training.sp01.model;
import lombok.Data;

import java.time.Instant;

@Data
public class GetBookingResponse {
    private User user;
    private Room room;
    private Instant startedAt;
    private Instant endedAt;
}
