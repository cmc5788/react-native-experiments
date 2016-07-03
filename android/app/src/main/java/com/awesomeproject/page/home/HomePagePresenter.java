package com.awesomeproject.page.home;

import com.awesomeproject.JSEventReceiver.JSViewEventTarget;
import com.awesomeproject.MyNavigator.NavTaggable;

public interface HomePagePresenter extends NavTaggable, JSViewEventTarget {

  void buttonClicked();
}
