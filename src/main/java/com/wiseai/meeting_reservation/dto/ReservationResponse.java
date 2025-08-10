package com.wiseai.meeting_reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "예약 응답")
@Getter
@AllArgsConstructor
@Builder
public class ReservationResponse {
  @Schema(description = "예약 ID", example = "101")
  private Long id;

  @Schema(description = "사용자 ID", example = "1")
  private Long userId;

  @Schema(description = "회의실 ID", example = "1")
  private Long meetingRoomId;

  @Schema(description = "시작시간", example = "2025-08-15T09:00:00")
  private LocalDateTime startTime;

  @Schema(description = "종료시간", example = "2025-08-15T10:30:00")
  private LocalDateTime endTime;

  @Schema(description = "상태", example = "CONFIRMED")
  private String status;

  @Schema(description = "총 요금(원)", example = "15000")
  private Integer totalPrice;
}
