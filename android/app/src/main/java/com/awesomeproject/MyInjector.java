package com.awesomeproject;

import android.support.annotation.NonNull;
import com.awesomeproject.MyNavigator.NavTag;
import com.awesomeproject.page.detail.DetailPagePresenter;
import com.awesomeproject.page.detail.DetailPageViewImpl;
import com.awesomeproject.page.home.HomePagePresenter;
import com.awesomeproject.page.home.HomePageViewImpl;
import com.facebook.react.LiteReactInstanceManager;
import com.facebook.react.bridge.ReactApplicationContext;
import java.util.Map;

public interface MyInjector {

  // Defines the scope

  void beginNewScope();

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

  DetailPagePresenter presenterFor(DetailPageViewImpl detailPageView, @NonNull NavTag navTag);

  HomePagePresenter presenterFor(HomePageViewImpl homePageView, @NonNull NavTag navTag);
}
