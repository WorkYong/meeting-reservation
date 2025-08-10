package com.wiseai.meeting_reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "예약 생성 요청")
public record ReservationCreateRequest(
    @NotNull @Schema(description = "사용자 ID", example = "1") Long userId,

    @NotNull @Schema(description = "회의실 ID", example = "2") Long meetingRoomId,

    @NotNull @Schema(description = "시작시간(ISO8601, 정시/30분)", example = "2025-08-15T09:00:00") LocalDateTime startTime,

    @NotNull @Schema(description = "종료시간(ISO8601, 정시/30분)", example = "2025-08-15T10:30:00") LocalDateTime endTime) {
}
