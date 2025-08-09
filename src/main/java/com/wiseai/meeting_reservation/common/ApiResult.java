package com.wiseai.meeting_reservation.common;

public record ApiResult<T>(boolean success, T data, ErrorDetail error) {
  public static <T> ApiResult<T> ok(T data) {
    return new ApiResult<>(true, data, null);
  }

  public static <T> ApiResult<T> error(String code, String message) {
    return new ApiResult<>(false, null, new ErrorDetail(code, message));
  }

  public record ErrorDetail(String code, String message) {
  }
}
