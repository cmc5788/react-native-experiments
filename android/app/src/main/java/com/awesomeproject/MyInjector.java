package com.awesomeproject;

import com.facebook.react.bridge.ReactApplicationContext;
import java.util.Map;

public interface MyInjector {

  // Defines the scope

  void beginNewScope();

  //void registerMainActivity(MainActivity activity);
  //
  //void unregisterMainActivity(MainActivity activity);

  // Injectable values

  MyReactPackage reactPackageFor(MainActivity activity);

  MyNavigator navigatorFor(MyReactPackage reactPackage, ReactApplicationContext reactAppContext);

  MyNavigator navigatorFor(MyAppRoot appRoot);

  Map<String, MyNavigator.ViewFactory> viewFactoriesFor(MyNavigator navigator);
}
