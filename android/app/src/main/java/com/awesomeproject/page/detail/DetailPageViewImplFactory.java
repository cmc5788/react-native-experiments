package com.awesomeproject.page.detail;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import com.awesomeproject.MyApp;
import com.awesomeproject.MyNavigator.NavTag;
import com.awesomeproject.MyNavigator.ViewFactory;

public final class DetailPageViewImplFactory implements ViewFactory<DetailPageViewImpl> {
  @Override
  public DetailPageViewImpl createView(@NonNull ViewGroup parent, @NonNull NavTag tag) {
    DetailPageViewImpl impl = new DetailPageViewImpl(parent.getContext());
    impl.setPresenter(MyApp.injector(impl.getContext()).presenterFor(impl, tag));
    return impl;
  }
}

