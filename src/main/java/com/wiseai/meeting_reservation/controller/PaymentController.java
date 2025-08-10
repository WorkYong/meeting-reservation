package com.wiseai.meeting_reservation.controller;

import com.wiseai.meeting_reservation.common.ApiResult;
import com.wiseai.meeting_reservation.dto.PaymentRequest;
import com.wiseai.meeting_reservation.dto.PaymentResponse;
import com.wiseai.meeting_reservation.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payments", description = "결제 처리 / 상태 조회")
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @Operation(summary = "결제 처리 (모의)", description = """
      예약 ID 기준으로 결제를 진행합니다.
      - CARD: providerType=CARD, cardToken 사용
      - SIMPLE: providerType=SIMPLE, simplePayUserId 또는 cardToken 사용
      - VIRTUAL_ACCOUNT: providerType=VIRTUAL_ACCOUNT, bankCode 사용(발급 → PENDING)
      """, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(schema = @Schema(implementation = PaymentRequest.class), examples = {
      @ExampleObject(name = "CARD", value = """
          { "providerType": "CARD", "cardToken": "tok_sample_123" }
          """),
      @ExampleObject(name = "SIMPLE", value = """
          { "providerType": "SIMPLE", "simplePayUserId": "simple-user-001" }
          """),
      @ExampleObject(name = "VIRTUAL_ACCOUNT", value = """
          { "providerType": "VIRTUAL_ACCOUNT", "bankCode": "HANA" }
          """)
  })), responses = {
      @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ApiResult.class), examples = @ExampleObject(value = """
          {
            "result": {
              "paymentId": 501,
              "reservationId": 101,
              "providerType": "CARD",
              "status": "SUCCESS",
              "externalPaymentId": "CARD-A001",
              "amount": 15000
            }
          }
          """)))
  })
  @PostMapping(value = "/reservations/{id}/payment", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResult<PaymentResponse>> pay(
      @Parameter(description = "예약 ID", example = "101") @PathVariable Long id,
      @RequestBody PaymentRequest request) {
    return ResponseEntity.ok(ApiResult.ok(paymentService.pay(id, request)));
  }

  @Operation(summary = "결제 상태 조회", responses = @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ApiResult.class), examples = @ExampleObject(value = """
      {
        "result": {
          "paymentId": 501,
          "reservationId": 101,
          "providerType": "CARD",
          "status": "SUCCESS",
          "externalPaymentId": "CARD-A001",
          "amount": 15000
        }
      }
      """))))
  @GetMapping("/payments/{paymentId}/status")
  public ResponseEntity<ApiResult<PaymentResponse>> status(
      @Parameter(description = "결제 ID", example = "501") @PathVariable Long paymentId) {
    return ResponseEntity.ok(ApiResult.ok(paymentService.status(paymentId)));
  }
}
