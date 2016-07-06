package com.awesomeproject.page.home;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import com.awesomeproject.page.JSPageView;

public interface HomePageView extends JSPageView {

  void setButtonColor(@ColorInt int color);

  void setImageUrl(@NonNull String url);
}
