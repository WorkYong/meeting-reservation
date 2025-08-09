package com.wiseai.meeting_reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
public class ReservationUpdateRequest {
  @NotNull
  Long meetingRoomId;
  @NotNull
  LocalDateTime startTime;
  @NotNull
  LocalDateTime endTime;
}
