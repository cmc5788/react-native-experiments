package com.awesomeproject;

import android.os.Bundle;
import android.util.Log;
import com.facebook.react.LiteAppCompatReactActivity;
import com.facebook.react.LiteReactInstanceManager;
import com.facebook.react.ReactInstanceManager.ReactInstanceEventListener;
import com.facebook.react.bridge.ReactContext;

public class MainActivity extends LiteAppCompatReactActivity implements UiInteractable {

  public static final String TAG = "MainActivity";

  private MyReactNativeHost myReactNativeHost;
  private MyReactPackage myReactPackage;
  private LiteReactInstanceManager reactInstanceManager;
  private JSEventDispatcher myEventDispatcher;

  private void beginNewScopeAndInjectReactPackage() {
    MyInjector inject = MyApp.injector(this);
    inject.beginNewScope();
    myReactNativeHost = inject.reactNativeHostFor(this);
    myReactPackage = inject.reactPackageFor(this);
  }

  private void injectEventDispatcherWithInstanceManager() {
    MyInjector inject = MyApp.injector(this);
    reactInstanceManager = myReactNativeHost.getReactInstanceManager();
    myEventDispatcher = inject.eventDispatcherFor(this, reactInstanceManager);
  }

  private boolean isPausedOrPausing;
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

  @Override
  public void onCreate(Bundle savedState) {
    Log.d(TAG, String.format("onCreate(%d) savedInstanceState=%s", hashCode(), savedState));
    this.savedState = savedState;
    beginNewScopeAndInjectReactPackage();
    super.onCreate(savedState);
    injectEventDispatcherWithInstanceManager();
    reactInstanceManager.addReactInstanceEventListener(reactInstanceEventListener);
  }

  private final ReactInstanceEventListener //
      reactInstanceEventListener = new ReactInstanceEventListener() {
    @Override
    public void onReactContextInitialized(ReactContext context) {
      Log.d(TAG, String.format("onReactContextInitialized(%d)", MainActivity.this.hashCode()));
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
  };

  @Override
  protected void onResume() {
    Log.d(TAG, String.format("onResume(%d)", hashCode()));
    super.onResume();
    isPausedOrPausing = false;
    myEventDispatcher.dispatch("onAppResume", null);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (outState != null) {
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
    myEventDispatcher.dispatch("onAppPause", null);
    super.onPause();
  }

  @Override
  protected void onDestroy() {
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
