package com.wiseai.meeting_reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 응답")
public record PaymentResponse(
    @Schema(description = "결제 ID", example = "501") Long paymentId,
    @Schema(description = "예약 ID", example = "101") Long reservationId,
    @Schema(description = "결제사 타입", example = "CARD") String providerType,
    @Schema(description = "결제 상태", example = "SUCCESS") String status,
    @Schema(description = "외부 결제 ID(벤더 트랜잭션 ID 등)", example = "CARD-A001") String externalPaymentId,
    @Schema(description = "결제 금액(원)", example = "15000") Integer amount) {
}
