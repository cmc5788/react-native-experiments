package com.awesomeproject;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.facebook.react.bridge.UiThreadUtil.assertOnUiThread;

public class MyAppRoot extends FrameLayout {

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

  private void injectDeps() {
    navigator = MyApp.injector(getContext()).navigatorFor(this);
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
    tv.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));
    tv.setGravity(Gravity.CENTER);
    tv.setText("I am an empty root view.\nYou shouldn't see me.");

    setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        navigator.navigate(HomePageView.TAG, 1, null);
      }
    });

    navigator.setRoot(this);
  }

  @Override
  public void setId(int id) {
    assertOnUiThread();
    ID = id;
    super.setId(id);
  }
}
