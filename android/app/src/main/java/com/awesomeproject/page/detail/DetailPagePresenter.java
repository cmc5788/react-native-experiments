package com.awesomeproject.page.detail;

import android.support.annotation.NonNull;
import com.facebook.react.bridge.ReadableMap;

public interface DetailPagePresenter {

  void goBackClicked();

  void processJsArgs(@NonNull ReadableMap args);
}
