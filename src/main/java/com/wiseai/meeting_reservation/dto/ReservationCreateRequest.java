package com.wiseai.meeting_reservation.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ReservationCreateRequest(
        @NotNull Long userId,
        @NotNull Long meetingRoomId,
        @NotNull LocalDateTime startTime,
        @NotNull LocalDateTime endTime) {

}
