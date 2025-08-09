package com.wiseai.meeting_reservation.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // 우리 커스텀 예외
  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ErrorResponse> handleApi(ApiException e) {
    HttpStatus status = switch (e.getCode()) {
      case RESOURCE_NOT_FOUND -> HttpStatus.NOT_FOUND;
      case TIME_SLOT_ALREADY_BOOKED, INVALID_RESERVATION_STATUS -> HttpStatus.CONFLICT;
      case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
      default -> HttpStatus.INTERNAL_SERVER_ERROR;
    };
    return ResponseEntity.status(status).body(ErrorResponse.of(e.getCode(), e.getMessage()));
  }

  // @Valid 바디 검증 실패
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgNotValid(MethodArgumentNotValidException e) {
    String msg = e.getBindingResult().getFieldErrors().stream()
        .map(this::formatFieldError)
        .collect(Collectors.joining(", "));
    return ResponseEntity.badRequest().body(ErrorResponse.of(ErrorCode.BAD_REQUEST, msg));
  }

  // @Validated + @RequestParam / @PathVariable 검증 실패
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
    return ResponseEntity.badRequest().body(ErrorResponse.of(ErrorCode.BAD_REQUEST, e.getMessage()));
  }

  // 요청 본문 파싱 실패(JSON syntax 등)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException e) {
    return ResponseEntity.badRequest().body(ErrorResponse.of(ErrorCode.BAD_REQUEST, "Malformed JSON request"));
  }

  // 필수 쿼리 파라미터 누락
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException e) {
    return ResponseEntity.badRequest().body(ErrorResponse.of(ErrorCode.BAD_REQUEST, e.getMessage()));
  }

  // 마지막 보루
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleOthers(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR, "Unexpected error"));
  }

  private String formatFieldError(FieldError fe) {
    return fe.getField() + ": " + (fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage());
  }
}
