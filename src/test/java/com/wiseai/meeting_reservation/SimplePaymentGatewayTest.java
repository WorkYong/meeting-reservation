package com.wiseai.meeting_reservation;

import com.wiseai.meeting_reservation.domain.payment.gateway.SimplePaymentGateway;
import com.wiseai.meeting_reservation.domain.payment.PaymentResult;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SimplePaymentGatewayTest {

  @Test
  void pay_success_maps_OK_to_SUCCESS() {
    var rt = mock(RestTemplate.class);
    var gw = new SimplePaymentGateway(rt);
    TestUtils.setField(gw, "baseUrl", "http://localhost:8081");

    var res = new SimplePaymentGateway.SimpleRes();
    var pay = new SimplePaymentGateway.SimpleRes.Payment();
    pay.id = "B-1";
    pay.status = "OK";
    res.payment = pay;

    when(rt.postForEntity(anyString(), any(), eq(SimplePaymentGateway.SimpleRes.class)))
        .thenReturn(ResponseEntity.ok(res));

    PaymentResult r = gw.pay(java.util.Map.of("simplePayUserId", "u1", "amount", 1000, "merchantUid", "M1"));
    assertEquals("SUCCESS", r.status().name());
    assertEquals("B-1", r.externalId());
  }

  @Test
  void pay_err_maps_ERR_to_FAILED() {
    var rt = mock(RestTemplate.class);
    var gw = new SimplePaymentGateway(rt);
    TestUtils.setField(gw, "baseUrl", "http://localhost:8081");

    var res = new SimplePaymentGateway.SimpleRes();
    var pay = new SimplePaymentGateway.SimpleRes.Payment();
    pay.id = "B-2";
    pay.status = "ERR";
    res.payment = pay;

    when(rt.postForEntity(anyString(), any(), eq(SimplePaymentGateway.SimpleRes.class)))
        .thenReturn(ResponseEntity.ok(res));

    PaymentResult r = gw.pay(java.util.Map.of("simplePayUserId", "u1", "amount", 1000, "merchantUid", "M1"));
    assertEquals("FAILED", r.status().name());
  }
}
