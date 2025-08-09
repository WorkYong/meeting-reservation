package com.wiseai.meeting_reservation.domain.payment.gateway;

import com.wiseai.meeting_reservation.domain.payment.PaymentResult;
import com.wiseai.meeting_reservation.domain.payment.PaymentStatus;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class SimplePaymentGateway implements PaymentGateway {

  private final RestTemplate rt;

  @Value("${payment.mock.base-url}")
  private String baseUrl;

  @Override
  public PaymentResult pay(Map<String, Object> payload) {
    var url = baseUrl + "/b/simple/pay";
    var req = Map.of(
        "simpleUserId", payload.get("simplePayUserId"),
        "amount", payload.get("amount"),
        "merchantUid", payload.get("merchantUid"));
    ResponseEntity<SimpleRes> res = rt.postForEntity(url, req, SimpleRes.class);
    if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null)
      return PaymentResult.failed("simple http error");
    var body = res.getBody();
    if (body.payment != null && "OK".equals(body.payment.status)) {
      return PaymentResult.success(body.payment.id);
    }
    return PaymentResult.failed("B:" + (body.payment == null ? "NULL" : body.payment.status));
  }

  @Override
  public PaymentResult parseWebhook(Map<String, Object> body) {
    Map<?, ?> payment = (Map<?, ?>) body.get("payment"); // {id,status}
    String id = payment == null ? null : String.valueOf(payment.get("id"));
    String status = payment == null ? null : String.valueOf(payment.get("status"));
    return switch (status) {
      case "OK" -> PaymentResult.success(id);
      case "ERR" -> PaymentResult.failed(String.valueOf(body.getOrDefault("message", "err")));
      case "CANCELLED" -> PaymentResult.cancelled(String.valueOf(body.getOrDefault("message", "cancelled")));
      default -> new PaymentResult(PaymentStatus.PENDING, id, "unknown");
    };
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class SimpleRes {
    public Payment payment; // 중첩 응답
    @SuppressWarnings("unused")
    public String userRef;

    public SimpleRes() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payment {
      public String id;
      public String status; // "OK" | "ERR" | "CANCELLED"

      public Payment() {
      }
    }
  }
}