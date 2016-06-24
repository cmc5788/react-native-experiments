package com.awesomeproject.page.detail;

import android.view.ViewGroup;
import com.awesomeproject.MyNavigator;

public final class DetailPageViewImplFactory
    implements MyNavigator.ViewFactory<DetailPageViewImpl> {

  @Override
  public DetailPageViewImpl createView(ViewGroup parent) {
    return new DetailPageViewImpl(parent.getContext());
  }
}

