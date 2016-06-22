package com.awesomeproject;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import com.awesomeproject.JSEventReceiver.JSViewEventTarget;
import com.awesomeproject.MyNavigator.NavigableView;
import com.awesomeproject.MyNavigator.ViewFactory;
import com.awesomeproject.layout.scrollview.ScrollViews;
import com.awesomeproject.layout.space.Spaces;
import com.awesomeproject.layout.textview.TextViews;
import com.awesomeproject.layout.view.Views;
import com.facebook.react.bridge.ReadableMap;

import static android.graphics.Color.WHITE;
import static android.view.Gravity.CENTER;
import static com.facebook.react.bridge.UiThreadUtil.assertOnUiThread;

public class DetailPageView extends ScrollView implements NavigableView, JSViewEventTarget {

  // -----
  // STATICS

  public static final String TAG = "DetailPageView";

  public static final int ID = Views.generateViewId();

  private static class FactoryHolder {
    private static final ViewFactory<DetailPageView> FACTORY = new ViewFactory<DetailPageView>() {
      @Override
      public DetailPageView createView(ViewGroup parent) {
        return new DetailPageView(parent.getContext());
      }
    };
  }

  @NonNull
  public static ViewFactory factory() {
    return FactoryHolder.FACTORY;
  }

  // -----
  // INJECTS

  private JSEventDispatcher eventDispatcher;

  private void injectDeps() {
    if (isInEditMode()) return;
    eventDispatcher = MyApp.injector(getContext()).eventDispatcherFor(this);
  }

  // -----
  // BOILERPLATE ... stuff we can abstract away later

  public DetailPageView(Context context) {
    super(context);
    init();
  }

  public DetailPageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public DetailPageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public DetailPageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
    buildLayout();
  }

  @Override
  public void setId(int id) {
    assertOnUiThread();
    // Making sure that React isn't setting this. Have to be a bit
    // defensive since it likes to go rogue setting IDs elsewhere.
    if (id != ID) throw new IllegalArgumentException("not my id!");
    super.setId(id);
  }

  @Override
  public void receiveViewEvent(@NonNull String viewTag, @Nullable ReadableMap args) {
    //if (args != null && args.hasKey("setButtonColor")) {
    //  setBtnColor(Color.parseColor(args.getString("setButtonColor")));
    //}
    //if (args != null && args.hasKey("setImageUrl")) {
    //  setImageUrl(args.getString("setImageUrl"));
    //}
  }

  @Override
  public boolean respondsToTag(@NonNull String viewTag) {
    return TAG.equals(viewTag);
  }

  // -----
  // REAL CODE ... ?

  private void buildLayout() {
    // @formatter:off
    ScrollViews.buildWithInnerLinear()
        .matchParent()
        .gravity(CENTER)
        .bgColorInt(WHITE)

        .child(Spaces.vSpace(1))

        .child(
            TextViews.build()
            .wrapContent()
            .text("I am a Detail Page")
        )

        .child(
            TextViews.build()
            .wrapContent()
            .topMarginDp(32)
            .paddingDp(12)
            .bgColorInt(Color.LTGRAY)
            .text("<-- Go Back")
            .onClick(goBackClicked)
        )

        .child(Spaces.vSpace(1))

    .applyOnto(this);
    // @formatter:on
  }

  private final OnClickListener goBackClicked = new OnClickListener() {
    @Override
    public void onClick(View v) {
      eventDispatcher.dispatch(TAG + ".goBackClicked", null);
    }
  };
}
