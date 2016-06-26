package com.awesomeproject.page.detail;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import com.awesomeproject.MyNavigator.NavTag;
import com.awesomeproject.layout.scrollview.ScrollViews;
import com.awesomeproject.layout.space.Spaces;
import com.awesomeproject.layout.textview.TextViews;
import com.awesomeproject.layout.view.Views;
import com.facebook.react.bridge.ReadableMap;

import static android.graphics.Color.WHITE;
import static android.view.Gravity.CENTER;
import static com.facebook.react.bridge.UiThreadUtil.assertOnUiThread;

public class DetailPageViewImpl extends ScrollView implements DetailPageView {

  // -----
  // STATICS

  public static final int ID = Views.generateViewId();
  public static final int LABEL_ID = Views.generateViewId();
  public static final int BUTTON_ID = Views.generateViewId();

  // -----
  // BOILERPLATE ... stuff we can abstract away later

  private NavTag tag;
  private DetailPagePresenter presenter;

  /*package*/ void setNavTag(@NonNull NavTag tag) {
    this.tag = tag;
  }

  /*package*/ void setPresenter(@NonNull DetailPagePresenter presenter) {
    this.presenter = presenter;
  }

  public DetailPageViewImpl(Context context) {
    super(context);
    init();
  }

  public DetailPageViewImpl(Context context, AttributeSet attrs) {
    super(context, attrs);
    if (!isInEditMode()) throw new RuntimeException("no inflated instances.");
    init();
  }

  public DetailPageViewImpl(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public DetailPageViewImpl(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    setId(ID);
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
    if (tag == null) throw new NullPointerException();
    return tag.equals(NavTag.parse(viewTag));
  }

  @NonNull
  @Override
  public NavTag navTag() {
    return tag;
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
            .id(LABEL_ID)
            .wrapContent()
            .text("I am a Detail Page")
        )

        .child(
            TextViews.build()
            .id(BUTTON_ID)
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

  private final View.OnClickListener goBackClicked = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      presenter.goBackClicked();
    }
  };

  @Override
  public void setButtonColor(@ColorInt int color) {
    findViewById(BUTTON_ID).setBackgroundColor(color);
  }

  @Override
  public void setLabelText(@NonNull CharSequence text) {
    ((TextView) findViewById(LABEL_ID)).setText(text);
  }
}
