package com.wiseai.meeting_reservation.exception;

import com.wiseai.meeting_reservation.common.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(basePackages = "com.wiseai.meeting_reservation.controller")
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiResult<Void>> handleApi(HttpServletRequest req, ApiException e) {
    var code = e.getCode();
    var status = code.getStatus();
    logWarn(req, code.name(), e.getMessage());
    return ResponseEntity
        .status(status)
        .body(ApiResult.error(code.name(), messageOrDefault(e)));
  }

  // === 검증/요청 포맷 예외 → 400 ===
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResult<Void>> handleMethodArgNotValid(HttpServletRequest req,
      MethodArgumentNotValidException e) {
    String msg = e.getBindingResult().getFieldErrors().stream()
        .map(this::formatFieldError)
        .collect(Collectors.joining(", "));
    logWarn(req, ErrorCode.BAD_REQUEST.name(), msg);
    return ResponseEntity
        .status(ErrorCode.BAD_REQUEST.getStatus())
        .body(ApiResult.error(ErrorCode.BAD_REQUEST.name(), msg));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResult<Void>> handleConstraintViolation(HttpServletRequest req,
      ConstraintViolationException e) {
    logWarn(req, ErrorCode.BAD_REQUEST.name(), e.getMessage());
    return ResponseEntity
        .status(ErrorCode.BAD_REQUEST.getStatus())
        .body(ApiResult.error(ErrorCode.BAD_REQUEST.name(), e.getMessage()));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResult<Void>> handleNotReadable(HttpServletRequest req, HttpMessageNotReadableException e) {
    String msg = "Malformed JSON request";
    logWarn(req, ErrorCode.BAD_REQUEST.name(), msg + " - " + e.getMessage());
    return ResponseEntity
        .status(ErrorCode.BAD_REQUEST.getStatus())
        .body(ApiResult.error(ErrorCode.BAD_REQUEST.name(), msg));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiResult<Void>> handleMissingParam(HttpServletRequest req,
      MissingServletRequestParameterException e) {
    logWarn(req, ErrorCode.BAD_REQUEST.name(), e.getMessage());
    return ResponseEntity
        .status(ErrorCode.BAD_REQUEST.getStatus())
        .body(ApiResult.error(ErrorCode.BAD_REQUEST.name(), e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResult<Void>> handleTypeMismatch(HttpServletRequest req,
      MethodArgumentTypeMismatchException e) {
    String msg = "Invalid parameter: " + e.getName();
    logWarn(req, ErrorCode.BAD_REQUEST.name(), msg + " - " + e.getMessage());
    return ResponseEntity
        .status(ErrorCode.BAD_REQUEST.getStatus())
        .body(ApiResult.error(ErrorCode.BAD_REQUEST.name(), msg));
  }

  // === 메서드 미지원 → 405 ===
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiResult<Void>> handleMethodNotSupported(HttpServletRequest req,
      HttpRequestMethodNotSupportedException e) {
    var code = ErrorCode.BAD_REQUEST; // 별도 코드 만들고 싶으면 METHOD_NOT_ALLOWED 추가
    String msg = "Method not supported";
    logWarn(req, code.name(), msg + " - " + e.getMessage());
    return ResponseEntity
        .status(405)
        .body(ApiResult.error(code.name(), msg));
  }

  // === springdoc(swagger) 요청은 기본 처리로 패스 ===
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResult<Void>> handleOthers(HttpServletRequest req, Exception e) throws Exception {
    String uri = req.getRequestURI();
    if (uri.startsWith("/v3/api-docs") || uri.startsWith("/swagger-ui")) {
      throw e; // 기본 처리로 넘김
    }
    log.error("[UNHANDLED] {} {} -> {}", req.getMethod(), uri, e.getMessage(), e);
    var code = ErrorCode.INTERNAL_ERROR;
    return ResponseEntity
        .status(code.getStatus())
        .body(ApiResult.error(code.name(), code.getDefaultMessage()));
  }

  private String formatFieldError(FieldError fe) {
    return fe.getField() + ": " + (fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage());
  }

  private void logWarn(HttpServletRequest req, String code, String msg) {
    log.warn("[API] {} {} -> {}: {}", req.getMethod(), req.getRequestURI(), code, msg);
  }

  private String messageOrDefault(ApiException e) {
    return (e.getMessage() == null || e.getMessage().isBlank())
        ? e.getCode().getDefaultMessage()
        : e.getMessage();
  }
}
