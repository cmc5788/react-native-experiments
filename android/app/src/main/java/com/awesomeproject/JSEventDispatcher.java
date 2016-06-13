package com.awesomeproject;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface JSEventDispatcher {
  void dispatch(@NonNull String name, @Nullable Object data);
}
