package com.awesomeproject.page.home;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.awesomeproject.JSEventDispatcher;
import com.awesomeproject.MyNavigator.NavTag;
import com.facebook.react.bridge.ReadableMap;

public class HomePagePresenterImpl implements HomePagePresenter {

  private final JSEventDispatcher eventDispatcher;
  private final HomePageView view;
  private final NavTag tag;

  public HomePagePresenterImpl( //
      @NonNull JSEventDispatcher eventDispatcher, //
      @NonNull HomePageView view, //
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
  public void receiveViewEvent(@NonNull String viewTag, @Nullable ReadableMap args) {
    if (args == null) return;

    if (args.hasKey("setButtonColor")) {
      view.setButtonColor(Color.parseColor(args.getString("setButtonColor")));
      return;
    }

    if (args.hasKey("setImageUrl")) {
      view.setImageUrl(args.getString("setImageUrl"));
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
