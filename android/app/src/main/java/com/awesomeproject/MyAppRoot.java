package com.awesomeproject;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MyAppRoot extends RelativeLayout {

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
    TextView tv;
    addView(tv = new TextView(getContext()));
    tv.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));
    tv.setGravity(Gravity.CENTER);
    tv.setText("Hello World!");
  }
}
