package com.wiseai.meeting_reservation.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 모든 API 응답의 공통 포맷
 * 
 * @param <T> 응답 데이터 타입
 */
@Schema(description = "API 공통 응답 형식")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResult<T>(
    @Schema(description = "성공 여부", example = "true") boolean success,

    @Schema(description = "응답 데이터 (성공 시 값 있음)") T data,

    @Schema(description = "오류 정보 (실패 시 값 있음)") ErrorDetail error) {
  public static <T> ApiResult<T> ok(T data) {
    return new ApiResult<>(true, data, null);
  }

  public static <T> ApiResult<T> error(String code, String message) {
    return new ApiResult<>(false, null, new ErrorDetail(code, message));
  }

  @Schema(description = "오류 상세 정보")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public record ErrorDetail(
      @Schema(description = "에러 코드", example = "NOT_FOUND") String code,

      @Schema(description = "에러 메시지", example = "해당 리소스를 찾을 수 없습니다.") String message) {
  }
}
