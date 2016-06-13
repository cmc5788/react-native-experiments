package com.awesomeproject;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import com.awesomeproject.MyNavigator.ViewFactory;
import com.facebook.react.bridge.ReactApplicationContext;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.react.bridge.UiThreadUtil.assertOnUiThread;

public class MyApp extends Application implements MyInjector {

  @NonNull
  public static MyInjector injector(@NonNull Context context) {
    return ((MyApp) context.getApplicationContext()).injector();
  }

  // Monotonically increment to force creation of new injectables when a new singleton scope inits
  // (i.e., when a new Activity is created)
  private volatile int scopeCounter;

  // Injectable scoped singletons
  private volatile Scoped<MyReactPackage> reactPackage;
  private volatile Scoped<MyNavigator> myNavigator;

  @NonNull
  public MyInjector injector() {
    return this;
  }

  @Override
  public void beginNewScope() {
    assertOnUiThread();
    scopeCounter++;
  }

  @NonNull
  @Override
  public MyReactPackage reactPackageFor(MainActivity activity) {
    return _myReactPackage();
  }

  @Override
  public MyNavigator navigatorFor(MyReactPackage reactPackage,
      ReactApplicationContext reactAppContext) {
    return _myNavigator(reactAppContext);
  }

  @Override
  public MyNavigator navigatorFor(MyAppRoot appRoot) {
    return _myNavigator(null);
  }

  @Override
  public Map<String, ViewFactory> viewFactoriesFor(MyNavigator navigator) {
    HashMap<String, ViewFactory> m = new HashMap<>();
    m.put(HomePageView.TAG, HomePageView.factory());
    return m;
  }

  // Lazy init scoped injectables with DCL

  private MyReactPackage _myReactPackage() {
    Scoped<MyReactPackage> mrp = reactPackage;
    if (mrp == null || mrp.scope != scopeCounter) {
      synchronized (this) {
        mrp = reactPackage;
        if (mrp == null || mrp.scope != scopeCounter) {
          reactPackage = mrp = new Scoped<>(new MyReactPackage(), scopeCounter);
        }
      }
    }
    return mrp.val;
  }

  private MyNavigator _myNavigator(ReactApplicationContext reactAppContext) {
    Scoped<MyNavigator> mn = myNavigator;
    if (mn == null || mn.scope != scopeCounter) {
      synchronized (this) {
        mn = myNavigator;
        if (mn == null || mn.scope != scopeCounter) {
          if (reactAppContext == null) {
            throw new IllegalStateException("cannot init myNavigator without reactAppContext");
          }
          myNavigator = mn = new Scoped<>(new MyNavigator(reactAppContext), scopeCounter);
        }
      }
    }
    return mn.val;
  }
}
