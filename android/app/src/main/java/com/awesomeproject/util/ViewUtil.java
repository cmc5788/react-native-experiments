package com.awesomeproject.util;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.View;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

public class ViewUtil {

  public interface ViewAction<V extends View> {
    void performViewAction(@NonNull V v);
  }

  public static <V extends View> void dimRequiredAction(@NonNull V v, //
      @NonNull final ViewAction<? super V> action, @NonNull final Object token, //
      final int minWidth, final int minHeight, //
      final int maxTries, final int backoffMult) {
    final AtomicInteger tries = new AtomicInteger();
    final WeakReference<V> vRef = new WeakReference<>(v);
    new Runnable() {
      @Override
      public void run() {
        handler().removeCallbacksAndMessages(token);
        V v = vRef.get();
        if (v == null) return;
        if (v.getWidth() < minWidth || v.getHeight() < minHeight) {
          int t = tries.getAndIncrement();
          if (t < maxTries) {
            handler().postAtTime(this, token, SystemClock.uptimeMillis() + (t * backoffMult));
          }
          return;
        }
        action.performViewAction(v);
      }
    }.run();
  }

  protected static Handler handler() {
    return HandlerHolder.HANDLER;
  }

  private static class HandlerHolder {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
  }

  protected ViewUtil() {
    throw new UnsupportedOperationException();
  }
}
