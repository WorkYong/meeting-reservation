package com.wiseai.meeting_reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "예약 수정 요청(PATCH)")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationUpdateRequest {

  @Schema(description = "변경할 회의실 ID", example = "2")
  @NotNull
  private Long meetingRoomId;

  @Schema(description = "변경할 시작시간", example = "2025-08-15T11:00:00")
  @NotNull
  private LocalDateTime startTime;

  @Schema(description = "변경할 종료시간", example = "2025-08-15T12:00:00")
  @NotNull
  private LocalDateTime endTime;
}