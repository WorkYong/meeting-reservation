package com.wiseai.meeting_reservation.domain.provider.a;

import lombok.Builder;

import java.util.Map;
import java.util.UUID;

public class CardPaymentClient {

  // 요청 DTO (예: 카드토큰과 금액이 필요)
  public record CardPayRequest(String cardToken, Integer amount, String orderId) {
  }

  // 응답 DTO (A사 고유 스키마)
  @Builder
  public record CardPayResponse(String transId, boolean approved, String message) {
  }

  // 웹훅 DTO (A사 고유)
  public record CardWebhook(String txid, String result, String reason) {
  }

  public CardPayResponse pay(CardPayRequest req) {
    // --- 외부 HTTP 호출이라고 가정(Mock) ---
    if (req.cardToken() == null || req.cardToken().isBlank()) {
      return CardPayResponse.builder().transId(null).approved(false).message("missing cardToken").build();
    }
    return CardPayResponse.builder()
        .transId("A-" + UUID.randomUUID())
        .approved(true)
        .message("APPROVED")
        .build();
  }

  public CardWebhook parseWebhook(Map<String, Object> body) {
    return new CardWebhook(
        (String) body.get("txid"),
        (String) body.get("result"), // "APPROVED" | "DECLINED" | "CANCELLED"
        (String) body.getOrDefault("reason", null));
  }
}
