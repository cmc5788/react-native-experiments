package com.awesomeproject.page.detail;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.awesomeproject.JSEventDispatcher;
import com.awesomeproject.MyNavigator.NavTag;
import com.facebook.react.bridge.ReadableMap;

public class DetailPagePresenterImpl implements DetailPagePresenter {

  private final JSEventDispatcher eventDispatcher;
  private final DetailPageView view;
  private final NavTag tag;

  public DetailPagePresenterImpl( //
      @NonNull JSEventDispatcher eventDispatcher, //
      @NonNull DetailPageView view, //
      @NonNull NavTag tag) {
    this.eventDispatcher = eventDispatcher;
    this.view = view;
    this.tag = tag;
  }

  @Override
  public void buttonClicked() {
    eventDispatcher.dispatch(String.format("%s.buttonClicked", view.navTag()), null);
  }

  @Override
  public void labelClicked() {
    eventDispatcher.dispatch(String.format("%s.labelClicked", view.navTag()), null);
  }

  @Override
  public void receiveViewEvent(@NonNull String viewTag, @Nullable ReadableMap args) {
    if (args == null) return;

    if (args.hasKey("setButtonColor")) {
      view.setButtonColor(Color.parseColor(args.getString("setButtonColor")));
      return;
    }

    if (args.hasKey("setButtonText")) {
      view.setButtonText(args.getInt("setButtonText"));
      return;
    }

    if (args.hasKey("setLabelText")) {
      view.setLabelText(args.getString("setLabelText"));
      //return;
    }
  }

  @Override
  public boolean respondsToTag(@NonNull String viewTag) {
    return tag.equals(NavTag.parse(viewTag));
  }

  @NonNull
  @Override
  public NavTag navTag() {
    return tag;
  }
}
