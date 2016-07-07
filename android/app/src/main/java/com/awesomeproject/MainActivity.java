package com.awesomeproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import com.facebook.react.LiteAppCompatReactActivity;
import com.facebook.react.LiteReactInstanceManager;
import com.facebook.react.ReactInstanceManager.ReactInstanceEventListener;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.bridge.ReactContext;

public class MainActivity extends LiteAppCompatReactActivity implements UiInteractable {

  public static final String TAG = "MainActivity";

  private static int createdActivityCount = 0;

  private MyReactNativeHost myReactNativeHost;
  private MyReactPackage myReactPackage;
  private LiteReactInstanceManager reactInstanceManager;
  private JSEventDispatcher myEventDispatcher;

  private void beginNewScopeAndInjectDeps() {
    MyInjector inject = MyApp.injector(this);
    inject.beginNewScope();
    myReactNativeHost = inject.reactNativeHostFor(this);
    myReactPackage = inject.reactPackageFor(this);
    reactInstanceManager = myReactNativeHost.getReactInstanceManager();
    myEventDispatcher = inject.eventDispatcherFor(this, reactInstanceManager);
  }

  private final Handler handler = new Handler(Looper.myLooper());

  private boolean isPausedOrPausing;
  private boolean initReactAppAfterResume;
  private boolean navigatorRestored;
  private Bundle savedState;

  @Override
  public boolean isUiInteractable() {
    return !isPausedOrPausing && !isFinishing();
  }

  /**
   * Returns the name of the main component registered from JavaScript.
   * This is used to schedule rendering of the component.
   */
  @Override
  protected String getMainComponentName() {
    return "MainComponent";
  }

  /**
   * Do this because the super implementation from React is mindblowingly stupid, doesn't account
   * for basic Android lifecycle concerns by failing to understand that Activity overlap may occur,
   * (case where one Activity instance is created before the previous is destroyed). tldr; React
   * shouldn't be grabbing its dependencies from the Application like it does. It technically works
   * for React as-written, but it is extremely fragile and will blow up frequently as-is.
   */
  @Override
  protected ReactNativeHost getReactNativeHost() {
    return myReactNativeHost;
  }

  /**
   * See {@link #getReactNativeHost}
   */
  @Override
  protected boolean getUseDeveloperSupport() {
    return myReactNativeHost.getUseDeveloperSupport();
  }

  @Override
  public void onCreate(Bundle savedState) {
    if (createdActivityCount != 0) {
      throw new IllegalStateException( //
          "Only one Activity instance may exist at a time. singleTask should be set - was it?");
    }
    createdActivityCount++;
    Log.d(TAG, String.format("onCreate(%d) savedInstanceState=%s", hashCode(), savedState));
    this.savedState = savedState;
    beginNewScopeAndInjectDeps();
    super.onCreate(savedState);
    reactInstanceManager.addReactInstanceEventListener(reactInstanceEventListener);
  }

  private final ReactInstanceEventListener //
      reactInstanceEventListener = new ReactInstanceEventListener() {
    @Override
    public void onReactContextInitialized(ReactContext context) {
      reactInstanceManager.removeReactInstanceEventListener(this);
      Log.d(TAG, String.format("onReactContextInitialized(%d)", MainActivity.this.hashCode()));
      if (isPausedOrPausing) {
        initReactAppAfterResume = true;
        return;
      }
      initReactApp();
    }
  };

  private void initReactApp() {
    if (!navigatorRestored) {
      myReactPackage.navigator().dispatchAppInit();
      if (savedState != null) {
        myReactPackage.navigator().restoreHierarchy(savedState);
        myReactPackage.navigator().restore();
        savedState = null;
      } else {
        myReactPackage.navigator().clear();
      }
      navigatorRestored = true;
    } else if (BuildConfig.DEBUG) {
      android.os.Process.killProcess(android.os.Process.myPid());
    } else {
      throw new IllegalStateException("onReactContextInitialized called twice.");
    }
  }

  @Override
  protected void onResume() {
    Log.d(TAG, String.format("onResume(%d)", hashCode()));
    super.onResume();
    isPausedOrPausing = false;
    if (navigatorRestored) {
      myEventDispatcher.dispatch("onAppResume", null);
    }
    if (initReactAppAfterResume) {
      // FIXME
      // This is hacky, but it works for now - when recovering from a situation where the
      // ReactContext was initialized while the Activity was paused, event dispatch can be lost.
      // To avoid this, for now, delay initializing the JS stack for a little while after resuming.
      handler.removeCallbacksAndMessages(MainActivity.class);
      handler.postAtTime(new Runnable() {
        @Override
        public void run() {
          if (!isPausedOrPausing) {
            initReactAppAfterResume = false;
            initReactApp();
          }
        }
      }, MainActivity.class, SystemClock.uptimeMillis() + 100);
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (outState != null && myReactPackage.navigatorReady() && navigatorRestored) {
      myReactPackage.navigator().saveHierarchy(outState);
    }
  }

  @Override
  protected void onPause() {
    Log.d(TAG, String.format("onPause(%d)", hashCode()));
    if (myReactPackage.navigatorReady() && navigatorRestored) {
      myReactPackage.navigator().save();
    }
    isPausedOrPausing = true;
    if (navigatorRestored) {
      myEventDispatcher.dispatch("onAppPause", null);
    }
    super.onPause();
  }

  @Override
  protected void onDestroy() {
    createdActivityCount--;
    Log.d(TAG, String.format("onDestroy(%d)", hashCode()));
    super.onDestroy();
    reactInstanceManager.removeReactInstanceEventListener(reactInstanceEventListener);
  }

  @Override
  public void onBackPressed() {
    Log.d(TAG, String.format("onBackPressed(%d)", hashCode()));
    if (myReactPackage.navigatorReady() && navigatorRestored) {
      myReactPackage.navigator().dispatchGoBack();
    }
  }
}
