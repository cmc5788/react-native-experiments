package com.awesomeproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactInstanceManager.ReactInstanceEventListener;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.shell.MainReactPackage;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatReactActivity implements UiInteractable {

  private MyReactPackage myReactPackage;

  private void injectDeps() {
    myReactPackage = ((MyApp) getApplication()).injector().myReactPackage(this);
  }

  private ReactInstanceManager reactInstanceManager;
  private boolean isPausedOrPausing;

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
    return "AwesomeProject";
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
  protected ReactInstanceManager createReactInstanceManager() {
    return reactInstanceManager = super.createReactInstanceManager();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    injectDeps();
    super.onCreate(savedInstanceState);
    reactInstanceManager.addReactInstanceEventListener(reactInstanceEventListener);
  }

  private final ReactInstanceEventListener //
      reactInstanceEventListener = new ReactInstanceEventListener() {
    @Override
    public void onReactContextInitialized(ReactContext context) {
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
    isPausedOrPausing = true;
    emitEvent("onAppPause", null);
    super.onPause();
  }

  @Override
  protected void onDestroy() {
    emitEvent("onAppDestroy", null);
    super.onDestroy();
    reactInstanceManager.removeReactInstanceEventListener(reactInstanceEventListener);
  }

  private void emitEvent(@NonNull String name, @Nullable Object data) {
    try {
      if (reactInstanceManager == null) return;
      ReactContext rc = reactInstanceManager.getCurrentReactContext();
      if (rc == null) return;
      rc.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(name, data);
    } catch (Exception ignored) {
    }
  }
}
