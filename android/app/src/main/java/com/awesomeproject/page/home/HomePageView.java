package com.awesomeproject.page.home;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import com.awesomeproject.JSEventReceiver.JSViewEventTarget;
import com.awesomeproject.MyNavigator.NavigableView;

public interface HomePageView extends NavigableView, JSViewEventTarget {

  void setButtonColor(@ColorInt int color);

  void setImageUrl(@NonNull String url);

  @NonNull
  String viewTag();
}
