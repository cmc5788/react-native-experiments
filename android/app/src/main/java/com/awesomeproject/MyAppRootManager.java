package com.awesomeproject;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

public class MyAppRootManager extends SimpleViewManager<MyAppRoot> {

  private static final String NAME = "MyAppRoot";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  protected MyAppRoot createViewInstance(ThemedReactContext reactContext) {
    return new MyAppRoot(reactContext);
  }
}
