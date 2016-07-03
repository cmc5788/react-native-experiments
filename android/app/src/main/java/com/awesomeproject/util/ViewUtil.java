package com.awesomeproject.util;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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

  public static <V extends View> void predicatedViewAction(@NonNull V v, //
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

  public static <V extends View> void onNextLayout(@NonNull V v,
      @NonNull final ViewAction<? super V> action) {
    ViewTreeObserver vto = v.getViewTreeObserver();
    final WeakReference<ViewTreeObserver> vtoRef = new WeakReference<>(vto);
    final WeakReference<V> vRef = new WeakReference<>(v);
    final AtomicReference<OnGlobalLayoutListener> gllRef = new AtomicReference<>(null);
    final Runnable cleanup = new Runnable() {
      @Override
      public void run() {
        try {
          vtoRef.get().removeOnGlobalLayoutListener(gllRef.get());
        } catch (Exception ignored) {
        }
        try {
          vRef.get().getViewTreeObserver().removeOnGlobalLayoutListener(gllRef.get());
        } catch (Exception ignored) {
        }
      }
    };
    gllRef.set(new OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        cleanup.run();
        V v = vRef.get();
        if (v != null) {
          action.performViewAction(v);
        }
      }
    });
    v.requestLayout();
    vto.addOnGlobalLayoutListener(gllRef.get());
  }

  public static <V extends View> void onNextLayout(@NonNull V v,
      @NonNull final Collection<? extends ViewAction<? super V>> actions) {
    ViewTreeObserver vto = v.getViewTreeObserver();
    final WeakReference<ViewTreeObserver> vtoRef = new WeakReference<>(vto);
    final WeakReference<V> vRef = new WeakReference<>(v);
    final AtomicReference<OnGlobalLayoutListener> gllRef = new AtomicReference<>(null);
    final Runnable cleanup = new Runnable() {
      @Override
      public void run() {
        try {
          vtoRef.get().removeOnGlobalLayoutListener(gllRef.get());
        } catch (Exception ignored) {
        }
        try {
          vRef.get().getViewTreeObserver().removeOnGlobalLayoutListener(gllRef.get());
        } catch (Exception ignored) {
        }
      }
    };
    gllRef.set(new OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        cleanup.run();
        V v = vRef.get();
        if (v != null) {
          for (ViewAction<? super V> action : actions) {
            action.performViewAction(v);
          }
        }
      }
    });
    v.requestLayout();
    vto.addOnGlobalLayoutListener(gllRef.get());
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
