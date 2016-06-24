package com.awesomeproject.page.home;

import android.view.ViewGroup;
import com.awesomeproject.MyNavigator;

public final class HomePageViewImplFactory
    implements MyNavigator.ViewFactory<HomePageViewImpl> {

  @Override
  public HomePageViewImpl createView(ViewGroup parent) {
    return new HomePageViewImpl(parent.getContext());
  }
}

