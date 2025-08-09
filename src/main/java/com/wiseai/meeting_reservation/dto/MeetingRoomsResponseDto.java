package com.wiseai.meeting_reservation.dto;

import java.util.List;

public record MeetingRoomsResponseDto(
    List<MeetingRoomItemDto> result,
    int pageNum, // 1-based
    int perPage,
    int totalPages) {
}