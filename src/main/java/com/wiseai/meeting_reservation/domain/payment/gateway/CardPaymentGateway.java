package com.wiseai.meeting_reservation.domain.payment.gateway;

import com.wiseai.meeting_reservation.domain.payment.PaymentResult;
import com.wiseai.meeting_reservation.domain.payment.PaymentStatus;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class CardPaymentGateway implements PaymentGateway {

  private final RestTemplate rt;

  @Value("${payment.mock.base-url}")
  private String baseUrl;

  @Override
  public PaymentResult pay(Map<String, Object> payload) {
    var url = baseUrl + "/a/card/pay";
    var req = Map.of(
        "cardToken", payload.get("cardToken"),
        "amount", payload.get("amount"),
        "orderId", payload.get("orderId"));
    ResponseEntity<CardRes> res = rt.postForEntity(url, req, CardRes.class);
    if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
      return PaymentResult.failed("card http error");
    }
    var body = res.getBody();
    if (Boolean.TRUE.equals(body.approved))
      return PaymentResult.success(body.transId);
    return PaymentResult.failed(body.message);
  }

  @Override
  public PaymentResult parseWebhook(Map<String, Object> body) {
    String result = String.valueOf(body.get("result")); // "APPROVED"|"DECLINED"|"CANCELLED"
    String txid = (String) body.get("txid");
    return switch (result) {
      case "APPROVED" -> PaymentResult.success(txid);
      case "DECLINED" -> PaymentResult.failed(String.valueOf(body.get("reason")));
      case "CANCELLED" -> PaymentResult.cancelled(String.valueOf(body.get("reason")));
      default -> new PaymentResult(PaymentStatus.PENDING, null, "unknown");
    };
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class CardRes {
    public String transId;
    public Boolean approved;
    public String message;

    public CardRes() {
    }

    public CardRes(String transId, Boolean approved, String message) {
      this.transId = transId;
      this.approved = approved;
      this.message = message;
    }
  }
}
