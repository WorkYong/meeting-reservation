package com.wiseai.meeting_reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ReservationResponse {
  private Long id;
  private Long userId;
  private Long meetingRoomId;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private String status;
  private Integer totalPrice;
}
