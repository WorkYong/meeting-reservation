package com.wiseai.meeting_reservation.exception;

public enum ErrorCode {

  RESOURCE_NOT_FOUND, // 리소스 없음 (404)
  BAD_REQUEST, // 잘못된 요청 (400)
  TIME_SLOT_ALREADY_BOOKED, // 예약 시간 중복 (409에 매핑 가능)
  INVALID_RESERVATION_STATUS, // 현재 상태에서 허용되지 않는 동작 (409/400)
  INTERNAL_ERROR // 서버 내부 오류 (500)
}
