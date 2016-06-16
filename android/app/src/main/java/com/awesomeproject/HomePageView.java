package com.awesomeproject;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import com.awesomeproject.JSEventReceiver.JSViewEventTarget;
import com.awesomeproject.MyNavigator.NavigableView;
import com.awesomeproject.MyNavigator.ViewFactory;
import com.awesomeproject.layout.CustomTextView;
import com.awesomeproject.layout.FrameLayouts;
import com.awesomeproject.layout.LinearLayouts;
import com.awesomeproject.layout.ScrollViews;
import com.awesomeproject.layout.TextViews;
import com.facebook.react.bridge.ReadableMap;

import static android.graphics.Color.WHITE;
import static android.view.Gravity.CENTER;
import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;
import static com.awesomeproject.R.id.home_page_text_id;
import static com.awesomeproject.R.id.home_page_view_id;
import static com.facebook.react.bridge.UiThreadUtil.assertOnUiThread;

public class HomePageView extends ScrollView implements NavigableView, JSViewEventTarget {

  // -----
  // STATICS

  public static final String TAG = "HomePageView";

  public static final int ID = home_page_view_id;

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
  // INJECTS

  private JSEventDispatcher eventDispatcher;

  private void injectDeps() {
    if (isInEditMode()) return;
    eventDispatcher = MyApp.injector(getContext()).eventDispatcherFor(this);
  }

  // -----
  // BOILERPLATE ... stuff we can abstract away later

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
    if (args != null && args.hasKey("setButtonColor")) {
      setBtnColor(Color.parseColor(args.getString("setButtonColor")));
    }
  }

  @Override
  public boolean respondsToTag(@NonNull String viewTag) {
    return TAG.equals(viewTag);
  }

  // -----
  // REAL CODE ... ?

  private void buildLayout() {
    // @formatter:off
    ScrollViews.build()
        .layoutParams(FrameLayouts.params().matchParent())
        .fillViewport(true)
        .bgColorInt(WHITE)

        .child(LinearLayouts.build()
            .layoutParams(ScrollViews.params().matchParent())
            .orientation(VERTICAL)
            .gravity(CENTER)

            .child(LinearLayouts.vSpace(1))

            .child(TextViews.build()
                .layoutParams(LinearLayouts.params().wrapContent())
                .text("I am the Home Page")
            )

            .child(LinearLayouts.vSpace(1))

            .child(TextViews.build()
                .id(home_page_text_id)
                .layoutParams(LinearLayouts.params().wrapContent())
                .vPaddingDp(14).hPaddingDp(10)
                .bgColorInt(Color.LTGRAY)
                .text("I am just some text")
                .onClick(onBtnClick)
            )

            .child(LinearLayouts.vSpace(1))

            .child(LinearLayouts.build()
                .layoutParams(LinearLayouts.params().matchWidth())
                .orientation(HORIZONTAL)
                .gravity(CENTER)

                .child(LinearLayouts.hSpace(1))

                .child(TextViews.build()
                    .layoutParams(LinearLayouts.params().wrapContent())
                    .text("COL1")
                )

                .child(LinearLayouts.hSpace(1.5f))

                .child(CustomTextView.build()
                    .layoutParams(LinearLayouts.params().wrapContent())
                    .textClr(Color.GREEN)
                    .text("COL2")
                )

                .child(LinearLayouts.hSpace(1.5f))

                .child(TextViews.build()
                    .layoutParams(LinearLayouts.params().wrapContent())
                    .text("COL3")
                )

                .child(LinearLayouts.hSpace(1))
            )

            .child(LinearLayouts.vSpace(1))
        )

    .applyOnto(this);
    // @formatter:on
  }

  private final OnClickListener onBtnClick = new OnClickListener() {
    @Override
    public void onClick(View v) {
      eventDispatcher.dispatch(TAG + ".ButtonClicked", null);
    }
  };

  private void setBtnColor(@ColorInt int color) {
    findViewById(home_page_text_id).setBackgroundColor(color);
  }
}
