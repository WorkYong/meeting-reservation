package com.wiseai.meeting_reservation.domain.provider.b;

import java.util.Map;
import java.util.UUID;

import lombok.Builder;

public class SimplePaymentClient {

  public record SimplePayRequest(String simpleUserId, Integer amount, String merchantUid) {
  }

  // 응답 중첩 구조 (B사 고유)
  @Builder
  public record SimplePayResponse(Payment payment, String userRef) {
    @Builder
    public record Payment(String id, String status) {
    } // status: "OK"|"ERR"
  }

  public record SimpleWebhook(String paymentId, String status, String desc) {
  }

  public SimplePayResponse pay(SimplePayRequest req) {
    if (req.simpleUserId() == null || req.simpleUserId().isBlank()) {
      return SimplePayResponse.builder()
          .payment(SimplePayResponse.Payment.builder().id(null).status("ERR").build())
          .userRef(null)
          .build();
    }
    return SimplePayResponse.builder()
        .payment(SimplePayResponse.Payment.builder().id("B-" + UUID.randomUUID()).status("OK").build())
        .userRef(req.simpleUserId())
        .build();
  }

  public SimpleWebhook parseWebhook(Map<String, Object> body) {
    Map<?, ?> payment = (Map<?, ?>) body.get("payment"); // { "id": "...", "status": "OK|ERR|CANCELLED" }
    return new SimpleWebhook(
        payment == null ? null : String.valueOf(payment.get("id")),
        payment == null ? null : String.valueOf(payment.get("status")),
        (String) body.getOrDefault("message", null));
  }
}