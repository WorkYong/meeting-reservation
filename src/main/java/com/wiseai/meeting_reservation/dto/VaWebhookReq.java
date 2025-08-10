package com.wiseai.meeting_reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record VaWebhookReq(
    @Schema(example = "770-12-123456", description = "가상 계좌 번호") String va,
    @Schema(example = "PAID", description = "PAID|ISSUED|CANCELLED") String event,
    @Schema(example = "VA-C001", description = "외부 결제 트랜잭션 ID") String external_payment_id) {
}
