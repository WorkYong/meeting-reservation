package com.wiseai.meeting_reservation.dto;

public record MeetingRoomItemDto(
    Long id,
    String name,
    Integer capacity,
    Integer hourlyPrice) {
}