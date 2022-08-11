package com.paloit.training.sp01.model.request;

import lombok.Data;

import java.time.Instant;

@Data
public class FilterBookingPayload {
    private Long roomId;
    private Long userId;
    private Instant startedAt;
    private Instant endedAt;
}
