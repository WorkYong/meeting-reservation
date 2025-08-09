package com.wiseai.meeting_reservation;

import com.wiseai.meeting_reservation.domain.payment.gateway.CardPaymentGateway;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CardPaymentGatewayTest {

  @Test
  void pay_success_maps_to_PaymentResult_SUCCESS() {
    var rt = mock(RestTemplate.class);
    var gw = new CardPaymentGateway(rt);
    // baseUrl은 @Value 주입이지만 테스트에서는 리플렉션/세터로 설정
    TestUtils.setField(gw, "baseUrl", "http://localhost:8081");

    var body = new CardPaymentGateway.CardRes("A-1", true, "APPROVED");

    when(rt.postForEntity(anyString(), any(), eq(CardPaymentGateway.CardRes.class)))
        .thenReturn(ResponseEntity.ok(body));

    var result = gw.pay(Map.of("cardToken", "tok", "amount", 1000, "orderId", "ORD-1"));
    assertEquals("SUCCESS", result.status().name());
    assertEquals("A-1", result.externalId());
  }

  @Test
  void pay_declined_maps_to_FAILED() {
    var rt = mock(RestTemplate.class);
    var gw = new CardPaymentGateway(rt);
    TestUtils.setField(gw, "baseUrl", "http://localhost:8081");

    var body = new CardPaymentGateway.CardRes("A-2", false, "DECLINED");
    when(rt.postForEntity(anyString(), any(), eq(CardPaymentGateway.CardRes.class)))
        .thenReturn(ResponseEntity.ok(body));

    var r = gw.pay(java.util.Map.of("cardToken", "tok", "amount", 1000, "orderId", "ORD-2"));
    assertEquals("FAILED", r.status().name());
  }
}
