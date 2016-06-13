package com.awesomeproject;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.awesomeproject.MyNavigator.NavigableView;
import com.awesomeproject.MyNavigator.ViewFactory;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.facebook.react.bridge.UiThreadUtil.assertOnUiThread;

public class HomePageView extends FrameLayout implements NavigableView {

  public static final String TAG = "HomePageView";

  public static final int ID = R.id.initial_home_page_view_id;

  private static class FactoryHolder {
    private static final ViewFactory<HomePageView> FACTORY = new ViewFactory<HomePageView>() {
      @Override
      public HomePageView createView(ViewGroup parent) {
        return new HomePageView(parent.getContext());
      }
    };
  }

  @NonNull
  public static ViewFactory factory() {
    return FactoryHolder.FACTORY;
  }

  // -----

  private JSEventDispatcher eventDispatcher;

  private void injectDeps() {
    eventDispatcher = MyApp.injector(getContext()).eventDispatcherFor(this);
  }

  // -----

  public HomePageView(Context context) {
    super(context);
    init();
  }

  public HomePageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public HomePageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public HomePageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  @Override
  public boolean matchesNavTag(String tag) {
    return TAG.equals(tag);
  }

  private void init() {
    setId(ID);

    injectDeps();

    setBackgroundColor(Color.WHITE);

    TextView tv;
    addView(tv = new TextView(getContext()));
    tv.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER));
    tv.setBackgroundColor(Color.GRAY);
    tv.setGravity(Gravity.CENTER);
    tv.setText("I am the Home Page.");
    tv.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        eventDispatcher.dispatch(TAG + ".ButtonClicked", null);
      }
    });
  }

  @Override
  public void setId(int id) {
    assertOnUiThread();
    if (id != ID) throw new IllegalArgumentException("not my id!");
    super.setId(id);
  }
}
