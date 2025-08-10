package com.wiseai.meeting_reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회의실 목록 조회 요청 파라미터(쿼리스트링)")
public record MeetingRoomsRequestDto(
		@Schema(description = "최소 수용인원(옵션)", example = "4") Integer minCapacity,
		@Schema(description = "페이지 번호(1부터, null이면 1)", example = "1") Integer pageNum,
		@Schema(description = "페이지 크기(null이면 10)", example = "10") Integer perPage,
		@Schema(description = "정렬 필드(기본: hourlyPrice)", example = "hourlyPrice") String sort,
		@Schema(description = "정렬 방향(asc|desc, 기본 asc)", example = "asc") String direction) {
}
