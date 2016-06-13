package com.awesomeproject;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

public abstract class MyReactModule extends ReactContextBaseJavaModule {

  protected static class HandlerHolder {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
  }

  protected static Handler handler() {
    return HandlerHolder.HANDLER;
  }

  public MyReactModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Nullable
  protected MainActivity activity() {
    Activity activity = getCurrentActivity();
    if (activity == null) return null;
    if (!(activity instanceof MainActivity)) {
      throw new RuntimeException("wrong Activity type!");
    }
    return (MainActivity) activity;
  }
}
