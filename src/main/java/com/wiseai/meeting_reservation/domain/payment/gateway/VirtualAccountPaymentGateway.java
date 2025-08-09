package com.wiseai.meeting_reservation.domain.payment.gateway;

import com.wiseai.meeting_reservation.domain.payment.PaymentResult;
import com.wiseai.meeting_reservation.domain.payment.PaymentStatus;

import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class VirtualAccountPaymentGateway implements PaymentGateway {

  private final RestTemplate rt;

  @Value("${payment.mock.base-url}")
  private String baseUrl;

  @Override
  public PaymentResult pay(Map<String, Object> payload) {
    var url = baseUrl + "/c/va/issue";
    var req = Map.of(
        "bankCode", payload.get("bankCode"),
        "amount", payload.get("amount"),
        "ref", payload.get("ref"));
    ResponseEntity<IssueRes> res = rt.postForEntity(url, req, IssueRes.class);
    if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null)
      return PaymentResult.failed("va http error");
    var body = res.getBody();
    if ("ISSUED".equals(body.status)) {
      // 발급은 결제 전 → PENDING, externalId는 ref 사용(없으면 생성)
      String ext = body.ref != null ? body.ref : "VA-" + UUID.randomUUID();
      return new PaymentResult(PaymentStatus.PENDING, ext, "issued:" + body.vaNumber);
    }
    return PaymentResult.failed("VA not issued");
  }

  @Override
  public PaymentResult parseWebhook(Map<String, Object> body) {
    String event = String.valueOf(body.get("event")); // "PAID"|"CANCELLED"
    String externalId = (String) body.get("external_payment_id");
    return switch (event) {
      case "PAID" -> PaymentResult.success(externalId);
      case "CANCELLED" -> PaymentResult.cancelled("va cancelled");
      default -> new PaymentResult(PaymentStatus.PENDING, externalId, "unknown");
    };
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record IssueRes(String vaNumber, String bank, String status, String ref) {
  }
}
