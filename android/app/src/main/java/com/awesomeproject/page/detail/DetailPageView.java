package com.awesomeproject.page.detail;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import com.awesomeproject.page.JSPageView;

public interface DetailPageView extends JSPageView {

  void setButtonColor(@ColorInt int color);

  void setButtonText(@NonNull String text);

  void setLabelText(@NonNull String text);
}
