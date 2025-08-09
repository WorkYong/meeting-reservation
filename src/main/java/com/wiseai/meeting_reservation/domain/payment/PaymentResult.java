package com.wiseai.meeting_reservation.domain.payment;

import lombok.Builder;

@Builder
public record PaymentResult(
    PaymentStatus status, // PENDING | SUCCESS | FAILED | CANCELLED
    String externalId, // 외부 결제 ID(결제사마다 이름 다름 → 표준화)
    String message // 결제사 사유/메시지(옵션)
) {
  public static PaymentResult pending(String msg) {
    return PaymentResult.builder().status(PaymentStatus.PENDING).message(msg).build();
  }

  public static PaymentResult success(String id) {
    return PaymentResult.builder().status(PaymentStatus.SUCCESS).externalId(id).build();
  }

  public static PaymentResult failed(String msg) {
    return PaymentResult.builder().status(PaymentStatus.FAILED).message(msg).build();
  }

  public static PaymentResult cancelled(String msg) {
    return PaymentResult.builder().status(PaymentStatus.CANCELLED).message(msg).build();
  }
}