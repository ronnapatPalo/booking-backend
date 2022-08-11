package com.paloit.training.sp01.model.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateBookingPayload {
    private UUID roomId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant startedAt;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant endedAt;
}
