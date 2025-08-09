package com.wiseai.meeting_reservation;

import java.lang.reflect.Field;

public final class TestUtils {
  private TestUtils() {
  }

  public static void setField(Object target, String fieldName, Object value) {
    try {
      Field f = target.getClass().getDeclaredField(fieldName);
      f.setAccessible(true);
      f.set(target, value);
    } catch (NoSuchFieldException e) {
      // 부모 클래스에 있을 수도 있으니 상위에서도 찾아보기
      Class<?> cls = target.getClass().getSuperclass();
      while (cls != null) {
        try {
          Field f = cls.getDeclaredField(fieldName);
          f.setAccessible(true);
          f.set(target, value);
          return;
        } catch (NoSuchFieldException ignore) {
          cls = cls.getSuperclass();
        } catch (IllegalAccessException iae) {
          throw new RuntimeException(iae);
        }
      }
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
