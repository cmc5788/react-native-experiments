package com.awesomeproject.layout;

import java.util.concurrent.atomic.AtomicInteger;

public class Views {

  private static final AtomicInteger nextGeneratedId = new AtomicInteger(1);

  /** Stripped out of API 17+ SDK for backwards compat. */
  public static int generateViewId() {
    for (; ; ) {
      final int result = nextGeneratedId.get();
      // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
      int newValue = result + 1;
      if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
      if (nextGeneratedId.compareAndSet(result, newValue)) {
        return result;
      }
    }
  }

  protected Views() {
    throw new UnsupportedOperationException();
  }
}
