package com.wiseai.meeting_reservation.controller;

import com.wiseai.meeting_reservation.common.ApiResult;
import com.wiseai.meeting_reservation.dto.PaymentRequest;
import com.wiseai.meeting_reservation.dto.PaymentResponse;
import com.wiseai.meeting_reservation.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payments", description = "결제 처리 / 상태 조회")
@RestController
@RequestMapping(produces = "application/json")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @Operation(summary = "결제 처리 (모의)")
  @PostMapping("/reservations/{id}/payment")
  public ResponseEntity<ApiResult<PaymentResponse>> pay(@PathVariable Long id,
      @RequestBody PaymentRequest request) {
    return ResponseEntity.ok(ApiResult.ok(paymentService.pay(id, request)));
  }

  @Operation(summary = "결제 상태 조회")
  @GetMapping("/payments/{paymentId}/status")
  public ResponseEntity<ApiResult<PaymentResponse>> status(@PathVariable Long paymentId) {
    return ResponseEntity.ok(ApiResult.ok(paymentService.status(paymentId)));
  }
}
