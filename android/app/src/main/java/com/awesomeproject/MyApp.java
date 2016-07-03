package com.awesomeproject;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import com.awesomeproject.MyNavigator.NavTag;
import com.awesomeproject.MyNavigator.ViewFactory;
import com.awesomeproject.page.detail.DetailPagePresenter;
import com.awesomeproject.page.detail.DetailPagePresenterImpl;
import com.awesomeproject.page.detail.DetailPageViewImpl;
import com.awesomeproject.page.detail.DetailPageViewImplFactory;
import com.awesomeproject.page.home.HomePagePresenter;
import com.awesomeproject.page.home.HomePagePresenterImpl;
import com.awesomeproject.page.home.HomePageViewImpl;
import com.awesomeproject.page.home.HomePageViewImplFactory;
import com.awesomeproject.util.MapUtil;
import com.facebook.react.LiteReactInstanceManager;
import com.facebook.react.bridge.ReactApplicationContext;
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
  private volatile Scoped<JSEventReceiver> eventReceiver;
  private volatile Scoped<JSContent> content;
  private volatile Scoped<JSEventDispatcher> eventDispatcher;

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

  @NonNull
  @Override
  public JSEventDispatcher eventDispatcherFor(MainActivity activity,
      LiteReactInstanceManager instMgr) {
    return _eventDispatcher(instMgr);
  }

  @Override
  public MyNavigator navigatorFor(MyReactPackage reactPackage,
      ReactApplicationContext reactAppContext) {
    return _myNavigator(reactAppContext);
  }

  @Override
  public JSEventReceiver eventReceiverFor(MyReactPackage reactPackage,
      ReactApplicationContext reactAppContext) {
    return _eventReceiver(reactAppContext);
  }

  @Override
  public JSContent contentFor(MyReactPackage reactPackage,
      ReactApplicationContext reactAppContext) {
    return _content(reactAppContext);
  }

  @Override
  public MyNavigator navigatorFor(MyAppRoot appRoot) {
    return _myNavigator(null);
  }

  @Override
  public JSEventReceiver eventReceiverFor(MyAppRoot appRoot) {
    return _eventReceiver(null);
  }

  @Override
  public Map<String, ViewFactory> viewFactoriesFor(MyNavigator navigator) {
    // @formatter:off
    return MapUtil.<ViewFactory>buildStringMap()
        .put("HOME_PAGE", new HomePageViewImplFactory())
        .put("DETAIL_PAGE", new DetailPageViewImplFactory())
        .immutableMap();
    // @formatter:on
  }

  @Override
  public JSEventDispatcher eventDispatcherFor(MyNavigator navigator) {
    return _eventDispatcher(null);
  }

  @Override
  public DetailPagePresenter presenterFor(DetailPageViewImpl detailPageView,
      @NonNull NavTag navTag) {
    return new DetailPagePresenterImpl(_eventDispatcher(null), detailPageView, navTag);
  }

  @Override
  public HomePagePresenter presenterFor(HomePageViewImpl homePageView, @NonNull NavTag navTag) {
    return new HomePagePresenterImpl(_eventDispatcher(null), homePageView, navTag);
  }

  // Lazy init scoped injectables with DCL
  // TODO - too much boilerplate; put DCL idiom into Scoped impl instead, dropping immutability?

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

  private JSEventReceiver _eventReceiver(ReactApplicationContext reactAppContext) {
    Scoped<JSEventReceiver> er = eventReceiver;
    if (er == null || er.scope != scopeCounter) {
      synchronized (this) {
        er = eventReceiver;
        if (er == null || er.scope != scopeCounter) {
          if (reactAppContext == null) {
            throw new IllegalStateException("cannot init eventReceiver without reactAppContext");
          }
          eventReceiver = er = new Scoped<>(new JSEventReceiver(reactAppContext), scopeCounter);
        }
      }
    }
    return er.val;
  }

  private JSContent _content(ReactApplicationContext reactAppContext) {
    Scoped<JSContent> c = content;
    if (c == null || c.scope != scopeCounter) {
      synchronized (this) {
        c = content;
        if (c == null || c.scope != scopeCounter) {
          if (reactAppContext == null) {
            throw new IllegalStateException("cannot init content without reactAppContext");
          }
          content = c = new Scoped<>(new JSContent(reactAppContext), scopeCounter);
        }
      }
    }
    return c.val;
  }

  private JSEventDispatcher _eventDispatcher(LiteReactInstanceManager instMgr) {
    Scoped<JSEventDispatcher> ed = eventDispatcher;
    if (ed == null || ed.scope != scopeCounter) {
      synchronized (this) {
        ed = eventDispatcher;
        if (ed == null || ed.scope != scopeCounter) {
          if (instMgr == null) {
            throw new IllegalStateException("cannot init eventDispatcher without instMgr");
          }
          eventDispatcher = ed = //
              new Scoped<>((JSEventDispatcher) new MyEventDispatcher(instMgr), scopeCounter);
        }
      }
    }
    return ed.val;
  }
}
