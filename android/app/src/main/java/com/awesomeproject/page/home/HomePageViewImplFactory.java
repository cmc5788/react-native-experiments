package com.awesomeproject.page.home;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import com.awesomeproject.MyApp;
import com.awesomeproject.MyNavigator;
import com.awesomeproject.MyNavigator.NavTag;

public final class HomePageViewImplFactory implements MyNavigator.ViewFactory<HomePageViewImpl> {

  public static final String KEY = "HomePage";

  @Override
  public HomePageViewImpl createView(@NonNull ViewGroup parent, @NonNull NavTag tag) {
    HomePageViewImpl impl = new HomePageViewImpl(parent.getContext());
    impl.setNavTag(tag);
    impl.setPresenter(MyApp.injector(impl.getContext()).presenterFor(impl));
    return impl;
  }
}

