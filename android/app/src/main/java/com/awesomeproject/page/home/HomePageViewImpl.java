package com.awesomeproject.page.home;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.awesomeproject.CustomTextView;
import com.awesomeproject.MyApp;
import com.awesomeproject.R;
import com.awesomeproject.layout.imageview.ImageViewBuilder;
import com.awesomeproject.layout.imageview.ImageViews;
import com.awesomeproject.layout.linearlayout.LinearLayouts;
import com.awesomeproject.layout.relativelayout.RelativeLayouts;
import com.awesomeproject.layout.scrollview.ScrollViews;
import com.awesomeproject.layout.space.Spaces;
import com.awesomeproject.layout.textview.TextViews;
import com.awesomeproject.layout.view.Views;
import com.facebook.react.bridge.ReadableMap;
import com.squareup.picasso.Picasso;

import static android.graphics.Color.WHITE;
import static android.view.Gravity.CENTER;
import static com.awesomeproject.layout.LayoutParams.ALIGN_PARENT_LEFT;
import static com.awesomeproject.layout.LayoutParams.ALIGN_PARENT_RIGHT;
import static com.awesomeproject.layout.LayoutParams.CENTER_IN_PARENT;
import static com.awesomeproject.layout.LayoutParams.LEFT_OF;
import static com.awesomeproject.layout.LayoutParams.RIGHT_OF;
import static com.awesomeproject.layout.LayoutParams.WEIGHT;
import static com.facebook.react.bridge.UiThreadUtil.assertOnUiThread;

public class HomePageViewImpl extends ScrollView implements HomePageView {

  // -----
  // STATICS

  public static final String TAG = "HomePageView";

  public static final int ID = Views.generateViewId();

  private static final int TEXT_ID = Views.generateViewId();
  private static final int IMAGE_ID = Views.generateViewId();
  private static final int TEXT_CENTER_ANCHOR = Views.generateViewId();
  private static final int TEXT_LEFT_ANCHOR = Views.generateViewId();
  private static final int TEXT_RIGHT_ANCHOR = Views.generateViewId();

  // -----
  // INJECTS

  private HomePagePresenter presenter;

  private void injectDeps() {
    if (isInEditMode()) return;
    presenter = MyApp.injector(getContext()).presenterFor(this);
  }

  // -----
  // BOILERPLATE ... stuff we can abstract away later

  public HomePageViewImpl(Context context) {
    super(context);
    init();
  }

  public HomePageViewImpl(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public HomePageViewImpl(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public HomePageViewImpl(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
    if (args != null) presenter.processJsArgs(args);
  }

  @Override
  public boolean respondsToTag(@NonNull String viewTag) {
    return TAG.equals(viewTag);
  }

  @NonNull
  @Override
  public String viewTag() {
    return TAG;
  }

  // -----
  // REAL CODE ... ?

  ImageViewBuilder imageCell(@ColorInt int color) {
    // @formatter:off
    return ImageViews.build()
        .bgColorInt(color)
        .width(0).editMode().heightDp(64).layout(WEIGHT, 1f)
        .heightFlexPct(1f);
    // @formatter:on
  }

  private void buildLayout() {
    // @formatter:off
    ScrollViews.buildWithInnerLinear()
        .matchParent()
        .gravity(CENTER)
        .bgColorInt(WHITE)

        .child(Spaces.vSpace(1))

        .childXml(R.layout.simple_textview)

        .child(Spaces.vSpace(0.5f))

        .child(
          LinearLayouts.buildRow()
          .gravity(CENTER)

          .child(imageCell(Color.MAGENTA).id(IMAGE_ID))
          .child(imageCell(Color.GREEN))
          .child(imageCell(Color.BLUE))
          .child(imageCell(Color.RED))
          .child(imageCell(Color.YELLOW))
        )

        .child(
          LinearLayouts.buildRow()
          .gravity(CENTER)

          .child(imageCell(Color.YELLOW))
          .child(imageCell(Color.RED))
          .child(imageCell(Color.GREEN))
          .child(imageCell(Color.BLUE))
          .child(imageCell(Color.MAGENTA))
        )

        .child(Spaces.vSpace(1))

        .child(
            TextViews.build()
            .id(TEXT_ID)
            .wrapWidth()
            .heightFlexPct(1f).editMode().heightDp(128)
            .gravity(CENTER)
            .vPaddingDp(14).hPaddingDp(10)
            .bgColorInt(Color.LTGRAY)
            .text("I am just some text")
            .onClick(onBtnClick)
        )

        .child(Spaces.vSpace(1))

        .child(
            LinearLayouts.buildRow()
            .gravity(CENTER)

            .child(Spaces.hSpace(1))

            .child(
                TextViews.build()
                .wrapContent()
                .text("COL 1")
            )

            .child(Spaces.hSpace(1.5f))

            .child(
                CustomTextView.build()
                .wrapContent()
                .textClr(Color.GREEN)
                .text("COL 2")
            )

            .child(Spaces.hSpace(1.5f))

            .child(
                TextViews.build()
                .wrapContent()
                .text("COL 3")
            )

            .child(Spaces.hSpace(1))
        )

        .child(Spaces.vSpace(1))

        .child(
            RelativeLayouts.build()
            .matchWidth()

            .child(
                TextViews.build()
                .wrapContent()
                .gravity(CENTER)
                .layout(ALIGN_PARENT_LEFT, true)
                .layout(LEFT_OF, TEXT_LEFT_ANCHOR)
                .text("COL ??")
            )

            .child(
                TextViews.build()
                .id(TEXT_LEFT_ANCHOR)
                .wrapContent()
                .layout(LEFT_OF, TEXT_CENTER_ANCHOR)
                .text("COL A")
            )

            .child(
                TextViews.build()
                .id(TEXT_CENTER_ANCHOR)
                .editMode().bgColorInt(Color.MAGENTA)
                .bgColorInt(Color.CYAN)
                .wrapContent()
                .hMarginsDp(16)
                .layout(CENTER_IN_PARENT, true)
                .text("COL B")
            )

            .child(
                TextViews.build()
                .id(TEXT_RIGHT_ANCHOR)
                .wrapContent()
                .layout(RIGHT_OF, TEXT_CENTER_ANCHOR)
                .text("COL C")
            )

            .child(
                TextViews.build()
                .wrapContent()
                .gravity(CENTER)
                .layout(ALIGN_PARENT_RIGHT, true)
                .layout(RIGHT_OF, TEXT_RIGHT_ANCHOR)
                .text("COL !!")
            )
        )

        .child(Spaces.vSpace(1))

    .applyOnto(this);
    // @formatter:on
  }

  private final OnClickListener onBtnClick = new OnClickListener() {
    @Override
    public void onClick(View v) {
      presenter.onButtonClicked();
    }
  };

  @Override
  public void setButtonColor(@ColorInt int color) {
    findViewById(TEXT_ID).setBackgroundColor(color);
    TextViews.build().widthDp(256).applyOnto((TextView) findViewById(TEXT_ID));
  }

  @Override
  public void setImageUrl(@NonNull String url) {
    Picasso.with(getContext())
        .load(url)
        .fit()
        .centerInside()
        .into((ImageView) findViewById(IMAGE_ID));
  }
}
