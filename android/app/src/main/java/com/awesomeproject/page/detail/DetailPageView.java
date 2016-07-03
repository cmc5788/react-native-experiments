package com.awesomeproject.page.detail;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.awesomeproject.JSEventReceiver.JSViewEventTarget;
import com.awesomeproject.MyNavigator.NavTaggable;

public interface DetailPageView extends NavTaggable, JSViewEventTarget {

  void setButtonColor(@ColorInt int color);

  void setButtonText(@StringRes int text);

  void setButtonText(@NonNull CharSequence text);

  void setLabelText(@StringRes int text);

  void setLabelText(@NonNull CharSequence text);
}
