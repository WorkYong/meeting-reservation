package com.wiseai.meeting_reservation.controller;

import com.wiseai.meeting_reservation.common.ApiResult;
import com.wiseai.meeting_reservation.dto.ReservationCreateRequest;
import com.wiseai.meeting_reservation.dto.ReservationUpdateRequest;
import com.wiseai.meeting_reservation.dto.ReservationResponse;
import com.wiseai.meeting_reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reservations", description = "예약 CRUD (모든 응답 JSON)")
@RestController
@RequestMapping(value = "/reservations", produces = "application/json")
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @Operation(summary = "예약 생성")
  @PostMapping
  public ResponseEntity<ApiResult<ReservationResponse>> create(@RequestBody ReservationCreateRequest request) {
    return ResponseEntity.ok(ApiResult.ok(reservationService.create(request)));
  }

  @Operation(summary = "예약 단건 조회")
  @GetMapping("/{id}")
  public ResponseEntity<ApiResult<ReservationResponse>> get(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResult.ok(reservationService.get(id)));
  }

  @Operation(summary = "예약 수정")
  @PatchMapping("/{id}")
  public ResponseEntity<ApiResult<ReservationResponse>> update(
      @PathVariable Long id,
      @RequestBody ReservationUpdateRequest request) {
    return ResponseEntity.ok(ApiResult.ok(reservationService.update(id, request)));
  }

  @Operation(summary = "예약 취소 (Soft Delete)")
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResult<ReservationResponse>> cancel(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResult.ok(reservationService.cancel(id)));
  }
}