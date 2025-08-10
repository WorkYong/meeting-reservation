package com.wiseai.meeting_reservation.controller;

import com.wiseai.meeting_reservation.common.ApiResult;
import com.wiseai.meeting_reservation.domain.payment.PaymentProviderType;
import com.wiseai.meeting_reservation.domain.payment.PaymentStatus;
import com.wiseai.meeting_reservation.domain.payment.gateway.PaymentGateway;
import com.wiseai.meeting_reservation.domain.payment.gateway.PaymentGatewayResolver;
import com.wiseai.meeting_reservation.dto.CardWebhookReq;
import com.wiseai.meeting_reservation.dto.SimpleWebhookReq;
import com.wiseai.meeting_reservation.dto.VaWebhookReq;
import com.wiseai.meeting_reservation.repository.PaymentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Webhooks", description = "결제사 웹훅 수신 (멱등성 보장)")
@RestController
@RequestMapping(value = "/webhooks/payments", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

  private static final EnumSet<PaymentStatus> TERMINAL = EnumSet.of(PaymentStatus.SUCCESS, PaymentStatus.CANCELLED);

  private final PaymentRepository paymentRepository;
  private final PaymentGatewayResolver resolver;

  @Operation(summary = "결제사 웹훅 수신 (provider = card|simple|virtual_account)", description = """
      - 각 결제사별 바디 스펙은 다르지만, 게이트웨이가 공통 모델(PaymentResult)로 변환합니다.
      - 동일 이벤트 재전송(멱등성) 또는 상태 역전(예: SUCCESS 후 FAILED) 시 변경하지 않습니다.
      """, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {
      CardWebhookReq.class, SimpleWebhookReq.class, VaWebhookReq.class }), examples = {
          @ExampleObject(name = "CARD", value = """
              { "txid": "CARD-A001", "result": "APPROVED", "reason": "ok" }
              """),
          @ExampleObject(name = "SIMPLE", value = """
              { "payment": { "id": "SIMPLE-B001", "status": "OK" }, "message": "noted" }
              """),
          @ExampleObject(name = "VIRTUAL_ACCOUNT", value = """
              { "va": "770-12-123456", "event": "PAID", "external_payment_id": "VA-C001" }
              """)
      })))
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", examples = {
          @ExampleObject(name = "OK", value = """
              { "success": true, "data": "OK" }
              """),
          @ExampleObject(name = "IGNORED (이미 최종 상태)", value = """
              { "success": true, "data": "IGNORED" }
              """)
      })),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "BAD_REQUEST", value = """
          { "success": false, "error": { "code": "BAD_REQUEST", "message": "external id missing" } }
          """))),
      @ApiResponse(responseCode = "404", description = "결제 레코드 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "NOT_FOUND", value = """
          { "success": false, "error": { "code": "NOT_FOUND", "message": "Payment not found by external id: VA-C001" } }
          """)))
  })
  @PostMapping(value = "/{provider}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResult<String>> handle(
      @Parameter(description = "결제 제공자(card|simple|virtual_account)", example = "card") @PathVariable String provider,
      // 런타임은 Map으로 받아 게이트웨이에 그대로 전달 (파싱 책임은 게이트웨이에)
      @RequestBody Map<String, Object> body) {

    final PaymentProviderType type;
    try {
      type = PaymentProviderType.valueOf(provider.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      return ResponseEntity
          .badRequest()
          .body(ApiResult.error("BAD_PROVIDER", "Unknown provider: " + provider));
    }

    final PaymentGateway gateway = resolver.resolve(type);
    final var result = gateway.parseWebhook(body);

    final String externalId = Optional.ofNullable(result.externalId())
        .map(String::trim)
        .orElse("");

    if (externalId.isEmpty()) {
      return ResponseEntity
          .badRequest()
          .body(ApiResult.error("BAD_REQUEST", "external id missing"));
    }

    final var paymentOpt = paymentRepository.findByExternalPaymentId(externalId);
    if (paymentOpt.isEmpty()) {
      return ResponseEntity
          .status(404)
          .body(ApiResult.error("NOT_FOUND", "Payment not found by external id: " + externalId));
    }

    final var payment = paymentOpt.get();
    final PaymentStatus current = payment.getStatus();
    final PaymentStatus next = result.status();

    if (TERMINAL.contains(current)) {
      log.debug("[WEBHOOK] externalId={} already terminal({}) → IGNORE", externalId, current);
      return ResponseEntity.ok(ApiResult.ok("IGNORED"));
    }

    switch (next) {
      case SUCCESS -> payment.success(externalId);
      case CANCELLED -> payment.cancel();
      case FAILED -> {
        if (current != PaymentStatus.SUCCESS)
          payment.fail();
      }
      case PENDING -> log.debug("[WEBHOOK] externalId={} PENDING → keep {}", externalId, current);
    }

    paymentRepository.save(payment);
    return ResponseEntity.ok(ApiResult.ok("OK"));
  }
}