package com.wiseai.meeting_reservation.util;

import java.time.Duration;
import java.time.LocalDateTime;

public final class DateTimeRules {
  private DateTimeRules() {
  }

  public static void validate(LocalDateTime start, LocalDateTime end) {
    if (start == null || end == null)
      throw new IllegalArgumentException("시간이 비어있습니다.");
    if (!(isAligned(start) && isAligned(end)))
      throw new IllegalArgumentException("정시 또는 30분 단위만 허용");
    if (!start.isBefore(end))
      throw new IllegalArgumentException("시작 < 종료 이어야 함");
  }

  private static boolean isAligned(LocalDateTime t) {
    return t.getMinute() == 0 || t.getMinute() == 30;
  }

  public static int calcTotalPrice(int hourlyPrice, LocalDateTime start, LocalDateTime end) {
    long minutes = Duration.between(start, end).toMinutes();
    long halfHours = (minutes + 29) / 30; // 30분 올림
    return (int) Math.round(hourlyPrice * (halfHours / 2.0));
  }
}
