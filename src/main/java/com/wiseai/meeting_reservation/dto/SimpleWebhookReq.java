package com.wiseai.meeting_reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SimpleWebhookReq(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "심플 결제 객체") Payment payment,
    @Schema(example = "noted") String message) {
  public record Payment(
      @Schema(example = "SIMPLE-B001") String id,
      @Schema(example = "OK", description = "OK|FAIL") String status) {
  }
}