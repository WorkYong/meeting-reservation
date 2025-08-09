package com.wiseai.meeting_reservation.controller;

import com.wiseai.meeting_reservation.common.ApiResult;
import com.wiseai.meeting_reservation.domain.payment.PaymentProviderType;

import com.wiseai.meeting_reservation.domain.payment.gateway.PaymentGatewayResolver;
import com.wiseai.meeting_reservation.repository.PaymentRepository;
import com.wiseai.meeting_reservation.exception.ApiException;
import com.wiseai.meeting_reservation.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/webhooks/payments", produces = "application/json")
@RequiredArgsConstructor
public class WebhookController {

  private final PaymentRepository paymentRepository;
  private final PaymentGatewayResolver resolver;

  @PostMapping("/{provider}")
  public ResponseEntity<ApiResult<String>> handle(@PathVariable String provider,
      @RequestBody Map<String, Object> body) {
    var type = PaymentProviderType.valueOf(provider.toUpperCase());
    var gateway = resolver.resolve(type);

    var result = gateway.parseWebhook(body);
    // external_payment_id / txid 등은 게이트웨이가 알아서 읽음 → result.externalId() 표준화
    String externalId = result.externalId();
    if (externalId == null) {
      return ResponseEntity.badRequest().body(ApiResult.error("BAD_REQUEST", "external id missing"));
    }

    var payment = paymentRepository.findByExternalPaymentId(externalId)
        .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Payment not found by external id"));

    switch (result.status()) {
      case SUCCESS -> payment.success(externalId);
      case FAILED -> payment.fail();
      case CANCELLED -> payment.cancel();
      case PENDING -> {
        /* ignore */ }
    }
    paymentRepository.save(payment);

    return ResponseEntity.ok(ApiResult.ok("OK"));
  }
}
