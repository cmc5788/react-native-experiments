package com.awesomeproject.page.detail;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.awesomeproject.R;
import com.awesomeproject.layout.scrollview.ScrollViews;
import com.awesomeproject.layout.space.Spaces;
import com.awesomeproject.layout.textview.TextViews;
import com.awesomeproject.page.JSPageScrollViewImplBase;

import static android.graphics.Color.WHITE;
import static android.view.Gravity.CENTER;

public class DetailPageViewImpl //
    extends JSPageScrollViewImplBase<DetailPagePresenter> implements DetailPageView {

  public DetailPageViewImpl(Context context) {
    super(context);
    init();
  }

  public DetailPageViewImpl(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  @IdRes
  @Override
  protected int rootId() {
    return R.id.detail_page;
  }

  private void init() {
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
  public void setButtonText(@NonNull String text) {
    ((TextView) findViewById(R.id.detail_page_button)).setText(text);
  }

  @Override
  public void setLabelText(@NonNull String text) {
    ((TextView) findViewById(R.id.detail_page_label)).setText(text);
  }

  private final View.OnClickListener labelClicked = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      presenter().labelClicked();
    }
  };

  private final View.OnClickListener buttonClicked = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      presenter().buttonClicked();
    }
  };
}
