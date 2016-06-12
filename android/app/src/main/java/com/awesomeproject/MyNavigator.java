package com.awesomeproject;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.awesomeproject.contract.Navigator;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;

public class MyNavigator extends MyReactModule implements Navigator {

  private static final String TAG = "MyNavigator";

  public MyNavigator(ReactApplicationContext reactAppContext) {
    super(reactAppContext);
  }

  @Override
  public String getName() {
    return TAG;
  }

  @Override
  @ReactMethod
  public void navigate(final String target, final int direction, final String meta) {
    final MainActivity activity = activity();
    if (activity == null || !activity.isUiInteractable()) {
      Log.i(TAG, "Aborting navigate: activity dead, dying, or dormant.");
      return;
    }

    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(getReactApplicationContext(), "navigator navigating to " + target,
            Toast.LENGTH_SHORT).show();
      }
    });
  }
}
