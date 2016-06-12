package com.facebook.react.bridge;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class ReactContextUtil {

  @Nullable
  public static <T extends Activity> T activityFrom(@NonNull ReactContext reactContext) {
    //noinspection unchecked
    return (T) reactContext.getCurrentActivity();
  }

  @NonNull
  public static ReactApplicationContext appContextFrom(@NonNull Context context) {
    return new ReactApplicationContext(context);
  }

  private ReactContextUtil() {
  }
}
