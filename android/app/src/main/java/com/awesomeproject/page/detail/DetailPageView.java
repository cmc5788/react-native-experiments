package com.awesomeproject.page.detail;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import com.awesomeproject.JSEventReceiver.JSViewEventTarget;
import com.awesomeproject.MyNavigator.NavigableView;

public interface DetailPageView extends NavigableView, JSViewEventTarget {

  void setButtonColor(@ColorInt int color);

  void setLabelText(@NonNull CharSequence text);
}
