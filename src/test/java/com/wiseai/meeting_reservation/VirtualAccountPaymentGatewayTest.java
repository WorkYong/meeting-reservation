package com.wiseai.meeting_reservation;

import com.wiseai.meeting_reservation.domain.payment.gateway.VirtualAccountPaymentGateway;
import com.wiseai.meeting_reservation.domain.payment.PaymentResult;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class VirtualAccountPaymentGatewayTest {

  @Test
  void pay_issue_returns_PENDING_with_externalId() {
    var rt = mock(RestTemplate.class);
    var gw = new VirtualAccountPaymentGateway(rt);
    TestUtils.setField(gw, "baseUrl", "http://localhost:8081");

    var body = new VirtualAccountPaymentGateway.IssueRes("770-12-123456", "HANA", "ISSUED", "R-1");
    when(rt.postForEntity(anyString(), any(), eq(VirtualAccountPaymentGateway.IssueRes.class)))
        .thenReturn(ResponseEntity.ok(body));

    PaymentResult r = gw.pay(java.util.Map.of("bankCode", "HANA", "amount", 10000, "ref", "R-1"));

    assertEquals("PENDING", r.status().name());
    assertEquals("R-1", r.externalId());
    assertTrue(r.message().startsWith("issued:"));
  }
}
