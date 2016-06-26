package com.awesomeproject.page.detail;

import android.graphics.Color;
import android.support.annotation.NonNull;
import com.awesomeproject.JSEventDispatcher;
import com.facebook.react.bridge.ReadableMap;

public class DetailPagePresenterImpl implements DetailPagePresenter {

  private final JSEventDispatcher eventDispatcher;
  private final DetailPageView view;

  public DetailPagePresenterImpl( //
      @NonNull JSEventDispatcher eventDispatcher, //
      @NonNull DetailPageView view) {
    this.eventDispatcher = eventDispatcher;
    this.view = view;
  }

  @Override
  public void goBackClicked() {
    eventDispatcher.dispatch(String.format("%s.goBackClicked", view.navTag()), null);
  }

  @Override
  public void processJsArgs(@NonNull ReadableMap args) {
    if (args.hasKey("setButtonColor")) {
      view.setButtonColor(Color.parseColor(args.getString("setButtonColor")));
    } else if (args.hasKey("setLabelText")) {
      view.setLabelText(args.getString("setLabelText"));
    }
  }
}
