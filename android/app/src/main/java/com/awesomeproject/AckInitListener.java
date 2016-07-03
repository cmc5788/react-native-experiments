package com.awesomeproject;

import android.support.annotation.NonNull;

public interface AckInitListener {
  void onAckInit(@NonNull AckInitDispatcher messenger);
}
