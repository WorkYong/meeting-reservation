package com.wiseai.meeting_reservation.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request"),
  TIME_SLOT_ALREADY_BOOKED(HttpStatus.CONFLICT, "Time slot already booked"),
  INVALID_RESERVATION_STATUS(HttpStatus.CONFLICT, "Invalid reservation status"),
  INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

  private final HttpStatus status;
  private final String defaultMessage;

  ErrorCode(HttpStatus status, String defaultMessage) {
    this.status = status;
    this.defaultMessage = defaultMessage;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getDefaultMessage() {
    return defaultMessage;
  }
}
