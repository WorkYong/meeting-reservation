package com.wiseai.meeting_reservation.domain.provider.c;

import java.util.Map;

public class VirtualAccountClient {

  public record VAIssueRequest(String bankCode, Integer amount, String ref) {
  }

  // 발급 응답(입금이 아님)
  public record VAIssueResponse(String vaNumber, String bank, String status, String ref) {
  } // status: "ISSUED"

  // 웹훅 — 입금/취소 통지
  public record VAWebhook(String va, String event, String externalId) {
  } // event: "PAID"|"CANCELLED"

  public VAIssueResponse issue(VAIssueRequest req) {
    if (req.bankCode() == null || req.bankCode().isBlank()) {
      return new VAIssueResponse(null, null, "ERROR", req.ref());
    }
    return new VAIssueResponse("770-12-" + (int) (Math.random() * 1000000), req.bankCode(), "ISSUED", req.ref());
  }

  public VAWebhook parseWebhook(Map<String, Object> body) {
    return new VAWebhook(
        (String) body.get("va"),
        (String) body.get("event"),
        (String) body.get("external_payment_id"));
  }
}
