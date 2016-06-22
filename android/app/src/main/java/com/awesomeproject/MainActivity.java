package com.awesomeproject;

import android.os.Bundle;
import android.util.Log;
import com.facebook.react.LiteAppCompatReactActivity;
import com.facebook.react.LiteReactInstanceManager;
import com.facebook.react.ReactInstanceManager.ReactInstanceEventListener;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.shell.MainReactPackage;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends LiteAppCompatReactActivity implements UiInteractable {

  public static final String TAG = "MainActivity";

  private MyReactPackage myReactPackage;
  private JSEventDispatcher myEventDispatcher;

  private void beginNewScopeAndInjectReactPackage() {
    MyInjector inject = MyApp.injector(this);
    inject.beginNewScope();
    myReactPackage = inject.reactPackageFor(this);
  }

  private void injectEventDispatcherWithInstanceManager(LiteReactInstanceManager instMgr) {
    MyInjector inject = MyApp.injector(this);
    myEventDispatcher = inject.eventDispatcherFor(this, instMgr);
  }

  private LiteReactInstanceManager reactInstanceManager;
  private boolean isPausedOrPausing;
  private boolean navigatorRestored;
  private boolean createdWithSavedState;

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
   * Returns whether dev mode should be enabled.
   * This enables e.g. the dev menu.
   */
  @Override
  protected boolean getUseDeveloperSupport() {
    return BuildConfig.DEBUG;
  }

  /**
   * A list of packages used by the app. If the app uses additional views
   * or modules besides the default ones, add more packages here.
   */
  @Override
  protected List<ReactPackage> getPackages() {
    return Arrays.asList(myReactPackage, new MainReactPackage());
  }

  @Override
  protected LiteReactInstanceManager createReactInstanceManager() {
    reactInstanceManager = super.createReactInstanceManager();
    injectEventDispatcherWithInstanceManager(reactInstanceManager);
    return reactInstanceManager;
  }

  @Override
  public void onCreate(Bundle savedState) {
    Log.d(TAG, String.format("onCreate(%d) savedInstanceState=%s", hashCode(), savedState));
    createdWithSavedState = savedState != null;
    beginNewScopeAndInjectReactPackage();
    super.onCreate(savedState);
    reactInstanceManager.addReactInstanceEventListener(reactInstanceEventListener);
  }

  private final ReactInstanceEventListener //
      reactInstanceEventListener = new ReactInstanceEventListener() {
    @Override
    public void onReactContextInitialized(ReactContext context) {
      Log.d(TAG, String.format("onReactContextInitialized(%d)", MainActivity.this.hashCode()));
      if (!navigatorRestored) {
        if (createdWithSavedState) {
          myReactPackage.navigator().restore();
        } else {
          myReactPackage.navigator().clear();
        }
        navigatorRestored = true;
      } else if (BuildConfig.DEBUG) {
        android.os.Process.killProcess(android.os.Process.myPid());
      } else {
        throw new IllegalStateException("onReactContextInitialized called twice.");
      }
      myEventDispatcher.dispatch("onAppInit", null);
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
      myReactPackage.navigator().goBack();
    }
  }
}
