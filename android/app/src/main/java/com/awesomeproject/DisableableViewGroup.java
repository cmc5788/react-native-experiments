package com.awesomeproject;

import android.view.ViewGroup;

public interface DisableableViewGroup<VG extends ViewGroup & DisableableViewGroup<VG>> {

  void disable();

  void enable();

  VG disableableViewGroup();
}
