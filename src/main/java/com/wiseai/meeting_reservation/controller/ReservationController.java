package com.wiseai.meeting_reservation.controller;

import com.wiseai.meeting_reservation.common.ApiResult;
import com.wiseai.meeting_reservation.dto.ReservationCreateRequest;
import com.wiseai.meeting_reservation.dto.ReservationResponse;
import com.wiseai.meeting_reservation.dto.ReservationUpdateRequest;
import com.wiseai.meeting_reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reservations", description = "예약 CRUD (모든 응답 JSON)")
@RestController
@RequestMapping(value = "/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @Operation(summary = "예약 생성", description = "정시/30분 단위, 시작<종료, 동일 회의실 중복 불가", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(schema = @Schema(implementation = ReservationCreateRequest.class), examples = @ExampleObject(value = """
      {
        "userId": 1,
        "meetingRoomId": 1,
        "startTime": "2025-08-15T09:00:00",
        "endTime": "2025-08-15T10:30:00"
      }
      """))))
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "success", value = """
          {
            "success": true,
            "data": {
              "id": 101,
              "userId": 1,
              "meetingRoomId": 1,
              "startTime": "2025-08-15T09:00:00",
              "endTime": "2025-08-15T10:30:00",
              "status": "CONFIRMED",
              "totalPrice": 15000
            }
          }
          """))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청(검증 실패 등)", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "bad_request", value = """
          {
            "success": false,
            "error": { "code": "BAD_REQUEST", "message": "startTime must be before endTime" }
          }
          """))),
      @ApiResponse(responseCode = "404", description = "리소스 없음(예: 사용자/회의실/예약 없음)", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "not_found", value = """
          {
            "success": false,
            "error": { "code": "NOT_FOUND", "message": "해당 리소스를 찾을 수 없습니다." }
          }
          """)))
  })
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResult<ReservationResponse>> create(
      @Valid @RequestBody ReservationCreateRequest request) {
    return ResponseEntity.ok(ApiResult.ok(reservationService.create(request)));
  }

  @Operation(summary = "예약 단건 조회")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "success", value = """
          {
            "success": true,
            "data": {
              "id": 101,
              "userId": 1,
              "meetingRoomId": 1,
              "startTime": "2025-08-15T09:00:00",
              "endTime": "2025-08-15T10:30:00",
              "status": "CONFIRMED",
              "totalPrice": 15000
            }
          }
          """))),
      @ApiResponse(responseCode = "404", description = "예약 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "not_found", value = """
          { "success": false, "error": { "code": "NOT_FOUND", "message": "해당 리소스를 찾을 수 없습니다." } }
          """)))
  })
  @GetMapping("/{id}")
  public ResponseEntity<ApiResult<ReservationResponse>> get(
      @Parameter(description = "예약 ID", example = "101") @PathVariable Long id) {
    return ResponseEntity.ok(ApiResult.ok(reservationService.get(id)));
  }

  @Operation(summary = "예약 수정(PATCH)", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(schema = @Schema(implementation = ReservationUpdateRequest.class), examples = @ExampleObject(value = """
      {
        "meetingRoomId": 2,
        "startTime": "2025-08-15T11:00:00",
        "endTime": "2025-08-15T12:00:00"
      }
      """))))
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "success", value = """
          { "success": true, "data": { "id": 101, "status": "CONFIRMED" } }
          """))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "bad_request", value = """
          { "success": false, "error": { "code": "BAD_REQUEST", "message": "시간대가 겹칩니다." } }
          """))),
      @ApiResponse(responseCode = "404", description = "예약 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "not_found", value = """
          { "success": false, "error": { "code": "NOT_FOUND", "message": "해당 리소스를 찾을 수 없습니다." } }
          """)))
  })
  @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResult<ReservationResponse>> update(
      @Parameter(description = "예약 ID", example = "101") @PathVariable Long id,
      @Valid @RequestBody ReservationUpdateRequest request) {
    return ResponseEntity.ok(ApiResult.ok(reservationService.update(id, request)));
  }

  @Operation(summary = "예약 취소 (Soft Delete)")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "success", value = """
          { "success": true, "data": { "id": 101, "status": "CANCELLED" } }
          """))),
      @ApiResponse(responseCode = "404", description = "예약 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "not_found", value = """
          { "success": false, "error": { "code": "NOT_FOUND", "message": "해당 리소스를 찾을 수 없습니다." } }
          """)))
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResult<ReservationResponse>> cancel(
      @Parameter(description = "예약 ID", example = "101") @PathVariable Long id) {
    return ResponseEntity.ok(ApiResult.ok(reservationService.cancel(id)));
  }
}
