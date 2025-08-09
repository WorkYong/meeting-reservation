package com.wiseai.meeting_reservation.dto;

public record MeetingRoomsRequestDto(
		Integer minCapacity, // 최소 수용인원
		Integer pageNum, // 1부터 시작 (null이면 1)
		Integer perPage, // 페이지 크기 (null이면 10)
		String sort, // 기본: hourlyPrice
		String direction // asc|desc (기본 asc)
) {
}
