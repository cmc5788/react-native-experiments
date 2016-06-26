package com.awesomeproject.page.home;

import android.graphics.Color;
import android.support.annotation.NonNull;
import com.awesomeproject.JSEventDispatcher;
import com.facebook.react.bridge.ReadableMap;

public class HomePagePresenterImpl implements HomePagePresenter {

  private final JSEventDispatcher eventDispatcher;
  private final HomePageView view;

  public HomePagePresenterImpl( //
      @NonNull JSEventDispatcher eventDispatcher, //
      @NonNull HomePageView view) {
    this.eventDispatcher = eventDispatcher;
    this.view = view;
  }

  @Override
  public void buttonClicked() {
    eventDispatcher.dispatch(String.format("%s.buttonClicked", view.navTag()), null);
  }

  @Override
  public void processJsArgs(@NonNull ReadableMap args) {
    if (args.hasKey("setButtonColor")) {
      view.setButtonColor(Color.parseColor(args.getString("setButtonColor")));
    } else if (args.hasKey("setImageUrl")) {
      view.setImageUrl(args.getString("setImageUrl"));
    }
  }
}
