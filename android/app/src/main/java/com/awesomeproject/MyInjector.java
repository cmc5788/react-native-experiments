package com.awesomeproject;

import com.facebook.react.LiteReactInstanceManager;
import com.facebook.react.bridge.ReactApplicationContext;
import java.util.Map;

public interface MyInjector {

  // Defines the scope

  void beginNewScope();

  //void registerMainActivity(MainActivity activity);

  //void unregisterMainActivity(MainActivity activity);

  // Injectable values

  MyReactPackage reactPackageFor(MainActivity activity);

  JSEventDispatcher eventDispatcherFor(MainActivity activity, LiteReactInstanceManager instMgr);

  MyNavigator navigatorFor(MyReactPackage reactPackage, ReactApplicationContext reactAppContext);

  JSEventReceiver eventReceiverFor( //
      MyReactPackage reactPackage, ReactApplicationContext reactAppContext);

  MyNavigator navigatorFor(MyAppRoot appRoot);

  JSEventReceiver eventReceiverFor(MyAppRoot appRoot);

  Map<String, MyNavigator.ViewFactory> viewFactoriesFor(MyNavigator navigator);

  JSEventDispatcher eventDispatcherFor(MyNavigator navigator);

  JSEventDispatcher eventDispatcherFor(HomePageView homePageView);
}
