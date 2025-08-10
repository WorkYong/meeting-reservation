package com.wiseai.meeting_reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CardWebhookReq(
    @Schema(example = "CARD-A001", description = "외부 결제 트랜잭션 ID") String txid,
    @Schema(example = "APPROVED", description = "APPROVED|DECLINED|CANCELLED") String result,
    @Schema(example = "ok", description = "사유/메시지") String reason) {
}
