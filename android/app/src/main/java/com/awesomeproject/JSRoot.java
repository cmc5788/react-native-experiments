package com.awesomeproject;

import android.view.ViewGroup;

public interface JSRoot<VG extends ViewGroup & DisableableViewGroup<VG>>
    extends JSEventReceiver.JSViewEventTarget, DisableableViewGroup<VG>, AckInitDispatcher {
}
