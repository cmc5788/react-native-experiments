package com.awesomeproject;

import android.support.annotation.NonNull;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MyReactPackage implements ReactPackage {

  private MyNavigator navigator;
  private JSEventReceiver eventReceiver;

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

  @NonNull
  public JSEventReceiver eventReceiver() {
    if (eventReceiver == null) throw new NullPointerException("eventReceiver is null.");
    return eventReceiver;
  }

  @Override
  public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
    MyInjector inject = MyApp.injector(reactContext);
    navigator = inject.navigatorFor(this, reactContext);
    eventReceiver = inject.eventReceiverFor(this, reactContext);
    return Arrays.<NativeModule>asList(navigator, eventReceiver);
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
