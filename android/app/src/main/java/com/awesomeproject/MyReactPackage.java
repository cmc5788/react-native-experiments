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

  @NonNull private final MyNavigator navigator;

  public MyReactPackage(@NonNull MyNavigator myNavigator) {
    this.navigator = myNavigator;
  }

  @NonNull
  public MyNavigator navigator() {
    return navigator;
  }

  @Override
  public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
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
