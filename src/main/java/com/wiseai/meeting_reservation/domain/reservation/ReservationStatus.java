package com.wiseai.meeting_reservation.domain.reservation;

public enum ReservationStatus {
  PENDING, // 결제 대기
  CONFIRMED, // 결제 완료
  CANCELLED // 취소됨
}
