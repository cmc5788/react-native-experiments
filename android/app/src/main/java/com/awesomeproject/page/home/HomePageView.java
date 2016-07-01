package com.awesomeproject.page.home;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import com.awesomeproject.JSEventReceiver.JSViewEventTarget;
import com.awesomeproject.LayoutBuildable;
import com.awesomeproject.MyNavigator.NavTaggable;

public interface HomePageView extends LayoutBuildable, NavTaggable, JSViewEventTarget {

  void setButtonColor(@ColorInt int color);

  void setImageUrl(@NonNull String url);
}
