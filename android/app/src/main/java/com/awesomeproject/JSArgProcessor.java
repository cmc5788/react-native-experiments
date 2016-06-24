package com.awesomeproject;

import android.support.annotation.NonNull;
import com.facebook.react.bridge.ReadableMap;

public interface JSArgProcessor {
  void processJsArgs(@NonNull ReadableMap args);
}
