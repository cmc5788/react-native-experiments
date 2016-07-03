package com.awesomeproject;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface AckInitDispatcher {
  void setAckInitListener(@NonNull String viewTag, @Nullable AckInitListener listener);

  void removeCurrentAckInitListener();
}
