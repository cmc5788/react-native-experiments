package com.awesomeproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.facebook.react.LiteAppCompatReactActivity;
import com.facebook.react.LiteReactInstanceManager;
import com.facebook.react.ReactInstanceManager.ReactInstanceEventListener;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.shell.MainReactPackage;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends LiteAppCompatReactActivity implements UiInteractable {

  private MyReactPackage myReactPackage;

  private void beginNewScopeAndInjectDeps() {
    MyInjector inject = MyApp.injector(this);
    inject.beginNewScope();
    myReactPackage = inject.reactPackageFor(this);
  }

  private LiteReactInstanceManager reactInstanceManager;
  private boolean isPausedOrPausing;
  private boolean navigatorRestored;
  //private MyAppRoot appRoot;

  @Override
  public boolean isUiInteractable() {
    return !isPausedOrPausing && !isFinishing();
  }

  //@NonNull
  //public MyAppRoot appRoot() {
  //  if (appRoot == null) {
  //    throw new NullPointerException("appRoot is null.");
  //  }
  //  return appRoot;
  //}

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
    return reactInstanceManager = super.createReactInstanceManager();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    beginNewScopeAndInjectDeps();
    super.onCreate(savedInstanceState);
    reactInstanceManager.addReactInstanceEventListener(reactInstanceEventListener);
  }

  private final ReactInstanceEventListener //
      reactInstanceEventListener = new ReactInstanceEventListener() {
    @Override
    public void onReactContextInitialized(ReactContext context) {
      // Crashes
      //appRoot = (MyAppRoot) findViewById(MyAppRoot.ID());
      //if (appRoot == null) {
      //  throw new IllegalStateException("Couldn't find root! Did JS Component render it?");
      //}
      if (!navigatorRestored) {
        myReactPackage.navigator().restore();
        navigatorRestored = true;
      }
      emitEvent("onAppInit", null);
    }
  };

  @Override
  protected void onResume() {
    super.onResume();
    isPausedOrPausing = false;
    emitEvent("onAppResume", null);
  }

  @Override
  protected void onPause() {
    if (myReactPackage.navigatorReady() && navigatorRestored) {
      myReactPackage.navigator().save();
    }
    isPausedOrPausing = true;
    emitEvent("onAppPause", null);
    super.onPause();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    reactInstanceManager.removeReactInstanceEventListener(reactInstanceEventListener);
  }

  @Override
  public void onBackPressed() {
    if (myReactPackage.navigatorReady() && navigatorRestored) {
      myReactPackage.navigator()._goBack();
    }
  }

  private void emitEvent(@NonNull String name, @Nullable Object data) {
    try {
      if (reactInstanceManager == null) return;
      ReactContext rc = reactInstanceManager.getCurrentReactContext();
      if (rc == null) return;
      rc.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(name, data);
    } catch (Exception e) {
      Log.e("MainActivity", "emitEvent err", e);
    }
  }
}
