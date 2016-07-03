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
import android.widget.TextView;
import com.awesomeproject.MyNavigator.NavTag;
import com.awesomeproject.R;
import com.awesomeproject.layout.scrollview.ProperlyRestoringScrollView;
import com.awesomeproject.layout.scrollview.ScrollViews;
import com.awesomeproject.layout.space.Spaces;
import com.awesomeproject.layout.textview.TextViews;
import com.facebook.react.bridge.ReadableMap;

import static android.graphics.Color.WHITE;
import static android.view.Gravity.CENTER;
import static com.facebook.react.bridge.UiThreadUtil.assertOnUiThread;

public class DetailPageViewImpl extends ProperlyRestoringScrollView implements DetailPageView {

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
    if (!isInEditMode()) throw new RuntimeException("no inflated instances.");
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public DetailPageViewImpl(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    if (!isInEditMode()) throw new RuntimeException("no inflated instances.");
    init();
  }

  private void init() {
    setId(R.id.detail_page);
    buildLayout();
  }

  @Override
  public void setId(int id) {
    assertOnUiThread();
    // Making sure that React isn't setting this. Have to be a bit
    // defensive since it likes to go rogue setting IDs elsewhere.
    if (id != R.id.detail_page) throw new IllegalArgumentException("not my id!");
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
            .id(R.id.detail_page_label)
            .wrapContent()
            .text("I am a Detail Page")
            .onClick(labelClicked)
        )

        .child(
            TextViews.build()
            .id(R.id.detail_page_button)
            .wrapContent()
            .topMarginDp(32)
            .paddingDp(12)
            .bgColorInt(Color.LTGRAY)
            .editMode().text("Edit Mode Button Text")
            .onClick(buttonClicked)
        )

        .child(Spaces.vSpace(1))

    .applyOnto(this);
    // @formatter:on
  }

  @Override
  public void setButtonColor(@ColorInt int color) {
    findViewById(R.id.detail_page_button).setBackgroundColor(color);
  }

  @Override
  public void setButtonText(@NonNull CharSequence text) {
    ((TextView) findViewById(R.id.detail_page_button)).setText(text);
  }

  @Override
  public void setLabelText(@NonNull CharSequence text) {
    ((TextView) findViewById(R.id.detail_page_label)).setText(text);
  }

  private final View.OnClickListener labelClicked = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      presenter.labelClicked();
    }
  };

  private final View.OnClickListener buttonClicked = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      presenter.buttonClicked();
    }
  };
}
