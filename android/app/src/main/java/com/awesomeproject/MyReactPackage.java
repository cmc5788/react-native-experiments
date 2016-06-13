package com.awesomeproject;

import android.support.annotation.NonNull;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import java.util.Collections;
import java.util.List;

public class MyReactPackage implements ReactPackage {

  private MyNavigator navigator;

  public MyReactPackage() {
  }

  public boolean navigatorReady() {
    return navigator != null;
  }

  @NonNull
  public MyNavigator navigator() {
    if (navigator == null) throw new NullPointerException("navigator is null.");
    return navigator;
  }

  @Override
  public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
    navigator = MyApp.injector(reactContext).navigatorFor(this, reactContext);
    return Collections.<NativeModule>singletonList(navigator);
  }

  @Override
  public List<Class<? extends JavaScriptModule>> createJSModules() {
    return Collections.emptyList();
  }

  @Override
  public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
    return Collections.<ViewManager>singletonList(new MyAppRootManager());
  }
}
