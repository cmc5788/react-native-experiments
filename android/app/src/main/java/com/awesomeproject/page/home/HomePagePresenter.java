package com.awesomeproject.page.home;

import android.support.annotation.NonNull;
import com.facebook.react.bridge.ReadableMap;

public interface HomePagePresenter {

  void onButtonClicked();

  void processJsArgs(@NonNull ReadableMap args);
}
