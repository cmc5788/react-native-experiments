package com.awesomeproject;

import android.os.Bundle;
import com.facebook.react.LiteAppCompatReactActivity;
import com.facebook.react.LiteReactInstanceManager;
import com.facebook.react.ReactInstanceManager.ReactInstanceEventListener;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.shell.MainReactPackage;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends LiteAppCompatReactActivity implements UiInteractable {

  private MyReactPackage myReactPackage;
  private JSEventDispatcher myEventDispatcher;

  private void beginNewScopeAndInjectReactPackage() {
    MyInjector inject = MyApp.injector(this);
    inject.beginNewScope();
    myReactPackage = inject.reactPackageFor(this);
  }

  private void injectEventDispatcher() {
    MyInjector inject = MyApp.injector(this);
    myEventDispatcher = inject.eventDispatcherFor(this, reactInstanceManager);
  }

  private LiteReactInstanceManager reactInstanceManager;
  private boolean isPausedOrPausing;
  private boolean navigatorRestored;

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
    injectEventDispatcher();
    return reactInstanceManager;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    beginNewScopeAndInjectReactPackage();
    super.onCreate(savedInstanceState);
    reactInstanceManager.addReactInstanceEventListener(reactInstanceEventListener);
  }

  private final ReactInstanceEventListener //
      reactInstanceEventListener = new ReactInstanceEventListener() {
    @Override
    public void onReactContextInitialized(ReactContext context) {
      if (!navigatorRestored) {
        myReactPackage.navigator().restore();
        navigatorRestored = true;
      }
      myEventDispatcher.dispatch("onAppInit", null);
    }
  };

  @Override
  protected void onResume() {
    super.onResume();
    isPausedOrPausing = false;
    myEventDispatcher.dispatch("onAppResume", null);
  }

  @Override
  protected void onPause() {
    if (myReactPackage.navigatorReady() && navigatorRestored) {
      myReactPackage.navigator().save();
    }
    isPausedOrPausing = true;
    myEventDispatcher.dispatch("onAppPause", null);
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
}
