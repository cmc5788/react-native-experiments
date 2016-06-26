package com.awesomeproject.page.detail;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import com.awesomeproject.MyApp;
import com.awesomeproject.MyNavigator;
import com.awesomeproject.MyNavigator.NavTag;

public final class DetailPageViewImplFactory
    implements MyNavigator.ViewFactory<DetailPageViewImpl> {

  public static final String KEY = "DETAIL_PAGE";

  @Override
  public DetailPageViewImpl createView(@NonNull ViewGroup parent, @NonNull NavTag tag) {
    DetailPageViewImpl impl = new DetailPageViewImpl(parent.getContext());
    impl.setNavTag(tag);
    impl.setPresenter(MyApp.injector(impl.getContext()).presenterFor(impl));
    return impl;
  }
}

