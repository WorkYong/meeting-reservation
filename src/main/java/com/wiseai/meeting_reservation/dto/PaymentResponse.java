package com.wiseai.meeting_reservation.dto;

public record PaymentResponse(
    Long paymentId,
    Long reservationId,
    String providerType,
    String status,
    String externalPaymentId,
    Integer amount) {
}
