package com.awesomeproject.page.home;

import android.graphics.Color;
import android.support.annotation.NonNull;
import com.awesomeproject.JSEventDispatcher;
import com.facebook.react.bridge.ReadableMap;
import java.util.ArrayList;
import java.util.List;

public class HomePagePresenterImpl implements HomePagePresenter {

  private final JSEventDispatcher eventDispatcher;
  private final HomePageView view;

  private List<ReadableMap> preInitArgs;
  private boolean initAcked;

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
    if (args.hasKey("___ackInit")) {
      initAcked = true;
      view.buildLayout();
      if (preInitArgs != null) {
        for (ReadableMap a : preInitArgs) {
          processJsArgs(a);
        }
        preInitArgs = null;
      }
      return;
    }

    if (!initAcked) {
      if (preInitArgs == null) preInitArgs = new ArrayList<>();
      preInitArgs.add(args);
      return;
    }

    if (args.hasKey("setButtonColor")) {
      view.setButtonColor(Color.parseColor(args.getString("setButtonColor")));
      return;
    }

    if (args.hasKey("setImageUrl")) {
      view.setImageUrl(args.getString("setImageUrl"));
      //return;
    }
  }
}
