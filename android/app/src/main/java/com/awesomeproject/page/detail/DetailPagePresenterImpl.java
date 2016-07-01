package com.awesomeproject.page.detail;

import android.graphics.Color;
import android.support.annotation.NonNull;
import com.awesomeproject.JSEventDispatcher;
import com.facebook.react.bridge.ReadableMap;
import java.util.ArrayList;
import java.util.List;

public class DetailPagePresenterImpl implements DetailPagePresenter {

  private final JSEventDispatcher eventDispatcher;
  private final DetailPageView view;

  private List<ReadableMap> preInitArgs;
  private boolean initAcked;

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
  public void labelClicked() {
    eventDispatcher.dispatch(String.format("%s.labelClicked", view.navTag()), null);
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

    if (args.hasKey("setLabelText")) {
      view.setLabelText(args.getString("setLabelText"));
      //return;
    }
  }
}
