package com.awesomeproject;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.awesomeproject.JSEventReceiver.JSViewEventTarget;
import com.facebook.react.bridge.ReadableMap;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.facebook.react.bridge.UiThreadUtil.assertOnUiThread;

public class MyAppRoot extends FrameLayout implements JSViewEventTarget {

  // We have to use this bizarre "ID tracking" system since React wants to assign view IDs to
  // certain views based on its own internal logic. This should be safe for now; eventually we
  // should determine how to avoid being a part of React's view system entirely so that this is
  // no longer necessary.
  private static int ID = R.id.initial_app_root_id;

  public static int ID() {
    assertOnUiThread();
    return ID;
  }

  private MyNavigator navigator;
  private JSEventReceiver eventReceiver;

  private void injectDeps() {
    navigator = MyApp.injector(getContext()).navigatorFor(this);
    eventReceiver = MyApp.injector(getContext()).eventReceiverFor(this);
  }

  public MyAppRoot(Context context) {
    super(context);
    init();
  }

  public MyAppRoot(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public MyAppRoot(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public MyAppRoot(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    setId(ID);

    injectDeps();

    setBackgroundColor(Color.WHITE);

    TextView tv;
    addView(tv = new TextView(getContext()));
    tv.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER));
    tv.setText("I am an empty root view.\nYou shouldn't see me.");

    navigator.setRoot(this);
    eventReceiver.setViewEventReceiver(this);
  }

  @Override
  public void setId(int id) {
    assertOnUiThread();
    ID = id;
    super.setId(id);
  }

  @Override
  public void receiveViewEvent(@NonNull String viewTag, @Nullable ReadableMap args) {
    for (int i = 0; i < getChildCount(); i++) {
      View child = getChildAt(i);
      if (child instanceof JSViewEventTarget && //
          ((JSViewEventTarget) child).respondsToTag(viewTag)) {
        ((JSViewEventTarget) child).receiveViewEvent(viewTag, args);
      }
    }
  }

  @Override
  public boolean respondsToTag(@NonNull String viewTag) {
    return true; // respond to all tags since we dispatch them
  }
}
