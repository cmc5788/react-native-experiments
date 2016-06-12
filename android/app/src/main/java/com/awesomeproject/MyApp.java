package com.awesomeproject;

import android.app.Application;
import android.support.annotation.NonNull;

import static com.facebook.react.bridge.ReactContextUtil.appContextFrom;

public class MyApp extends Application implements MyInjector {

  @Override
  public void onCreate() {
    super.onCreate();
  }

  @NonNull
  public MyInjector injector() {
    return this;
  }

  @NonNull
  @Override
  public MyReactPackage myReactPackage(MainActivity activity) {
    return new MyReactPackage( //
        new MyNavigator(appContextFrom(activity)));
  }
}
