package com.wiseai.meeting_reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회의실 목록 아이템")
public record MeetingRoomItemDto(
    @Schema(description = "회의실 ID", example = "1") Long id,
    @Schema(description = "회의실 이름", example = "Alpha") String name,
    @Schema(description = "수용 인원", example = "10") Integer capacity,
    @Schema(description = "시간당 요금(원)", example = "15000") Integer hourlyPrice) {
}