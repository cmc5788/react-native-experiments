package com.awesomeproject.page.detail;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import com.awesomeproject.JSEventReceiver.JSViewEventTarget;
import com.awesomeproject.LayoutBuildable;
import com.awesomeproject.MyNavigator.NavTaggable;

public interface DetailPageView extends LayoutBuildable, NavTaggable, JSViewEventTarget {

  void setButtonColor(@ColorInt int color);

  void setLabelText(@NonNull CharSequence text);
}
