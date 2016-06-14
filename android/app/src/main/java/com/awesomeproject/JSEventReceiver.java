package com.awesomeproject;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import static com.facebook.react.bridge.UiThreadUtil.assertOnUiThread;

public class JSEventReceiver extends MyReactModule {

  public interface JSViewEventTarget {
    void receiveViewEvent(@NonNull String viewTag, @Nullable ReadableMap args);

    boolean respondsToTag(@NonNull String viewTag);
  }

  private static final String TAG = "JSEventReceiver";

  private JSViewEventTarget viewEventTarget;

  public JSEventReceiver(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  public void setViewEventTarget(JSViewEventTarget viewEventTarget) {
    assertOnUiThread();
    this.viewEventTarget = viewEventTarget;
  }

  @Override
  public String getName() {
    return TAG;
  }

  @ReactMethod
  public void sendToView(final String viewTag, final ReadableMap args, final Promise p) {
    handler().post(new Runnable() {
      @Override
      public void run() {
        assertOnUiThread();
        if (viewEventTarget != null && viewTag != null && //
            viewEventTarget.respondsToTag(viewTag)) {
          viewEventTarget.receiveViewEvent(viewTag, args);
        }
        p.resolve(null);
      }
    });
  }
}
