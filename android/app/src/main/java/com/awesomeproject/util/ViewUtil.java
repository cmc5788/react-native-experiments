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

  public interface ViewPredicate<V extends View> {
    boolean isViewPredicateTrue(@NonNull V v);
  }

  public enum BackoffPolicy {
    LINEAR, EXPONENTIAL;

    int apply(int count, int backoff) {
      switch (this) {
        case LINEAR:
          return backoff * count;
        case EXPONENTIAL:
          if (count == 0) return 0;
          int amount = backoff;
          for (int i = 1; i < count; i++) {
            amount *= 2;
          }
          return amount;
        default:
          throw new UnsupportedOperationException();
      }
    }
  }

  public static <V extends View> void predicatedAction(@NonNull V v, //
      @NonNull final Object token, //
      @NonNull final ViewAction<? super V> action, //
      @NonNull final ViewPredicate<? super V> predicate, //
      @NonNull final BackoffPolicy backoffPolicy, final int backoffAmt, final int backoffTries) {
    final AtomicInteger tries = new AtomicInteger();
    final WeakReference<V> vRef = new WeakReference<>(v);
    new Runnable() {
      @Override
      public void run() {
        handler().removeCallbacksAndMessages(token);
        V v = vRef.get();
        if (v == null) return;
        if (!predicate.isViewPredicateTrue(v)) {
          int t = tries.getAndIncrement();
          if (t < backoffTries) {
            handler().postAtTime(this, token,
                SystemClock.uptimeMillis() + backoffPolicy.apply(t, backoffAmt));
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
