package com.awesomeproject;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.facebook.react.LiteReactInstanceManager;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import java.lang.ref.WeakReference;

public class MyEventDispatcher implements JSEventDispatcher {

  public static final String TAG = "MyEventDispatcher";

  private final WeakReference<LiteReactInstanceManager> instMgrRef;

  public MyEventDispatcher(@NonNull LiteReactInstanceManager instMgr) {
    instMgrRef = new WeakReference<>(instMgr);
  }

  @Override
  public void dispatch(@NonNull String name, @Nullable Object data) {
    LiteReactInstanceManager instMgr = instMgrRef.get();
    if (instMgr == null) {
      Log.e(TAG, "dispatch failed - null instMgr");
      return;
    }
    try {
      ReactContext rc = instMgr.getCurrentReactContext();
      if (rc == null) {
        Log.e(TAG, "dispatch failed - null instMgr.getCurrentReactContext()");
        return;
      }
      rc.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(name, data);
    } catch (Exception e) {
      Log.e(TAG, "emitEvent err", e);
    }
  }
}
