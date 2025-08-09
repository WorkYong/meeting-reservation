package com.wiseai.meeting_reservation.domain.reservation;

public enum ReservationStatus {
  PENDING_PAYMENT, // 결제 대기
  PAID, // 결제 완료(확정 전)
  CONFIRMED, // 확정됨
  CANCELLED, // 취소됨
  NO_SHOW // 예약했지만 이용 안 함
}
