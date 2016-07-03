package com.awesomeproject.page.detail;

import com.awesomeproject.JSEventReceiver.JSViewEventTarget;
import com.awesomeproject.MyNavigator.NavTaggable;

public interface DetailPagePresenter extends NavTaggable, JSViewEventTarget {

  void buttonClicked();

  void labelClicked();
}
