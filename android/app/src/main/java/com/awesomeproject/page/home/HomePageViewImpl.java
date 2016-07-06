package com.awesomeproject.page.home;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.awesomeproject.CustomTextView;
import com.awesomeproject.R;
import com.awesomeproject.layout.imageview.ImageViewBuilder;
import com.awesomeproject.layout.imageview.ImageViews;
import com.awesomeproject.layout.linearlayout.LinearLayouts;
import com.awesomeproject.layout.relativelayout.RelativeLayouts;
import com.awesomeproject.layout.scrollview.ScrollViews;
import com.awesomeproject.layout.space.Spaces;
import com.awesomeproject.layout.textview.TextViews;
import com.awesomeproject.page.JSPageScrollViewImplBase;
import com.awesomeproject.util.ViewUtil.BackoffPolicy;
import com.squareup.picasso.Picasso;

import static android.graphics.Color.WHITE;
import static android.view.Gravity.CENTER;
import static com.awesomeproject.layout.LayoutParams.ALIGN_PARENT_LEFT;
import static com.awesomeproject.layout.LayoutParams.ALIGN_PARENT_RIGHT;
import static com.awesomeproject.layout.LayoutParams.CENTER_IN_PARENT;
import static com.awesomeproject.layout.LayoutParams.LEFT_OF;
import static com.awesomeproject.layout.LayoutParams.RIGHT_OF;
import static com.awesomeproject.layout.LayoutParams.WEIGHT;
import static com.awesomeproject.util.ViewUtil.ViewAction;
import static com.awesomeproject.util.ViewUtil.ViewPredicate;
import static com.awesomeproject.util.ViewUtil.predicatedViewAction;

public class HomePageViewImpl //
    extends JSPageScrollViewImplBase<HomePagePresenter> implements HomePageView {

  public HomePageViewImpl(Context context) {
    super(context);
    init();
  }

  public HomePageViewImpl(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  @IdRes
  @Override
  protected int rootId() {
    return R.id.home_page;
  }

  private void init() {
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

          .child(imageCell(Color.MAGENTA).id(R.id.home_page_image))
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
          LinearLayouts.buildRow()
          .gravity(CENTER)

          .child(Spaces.hSpace(1))

          .child(
              TextViews.build()
              .id(R.id.home_page_text)
              .wrapWidth()
              .heightFlexPct(1f).editMode().heightDp(128)
              .gravity(CENTER)
              .vPaddingDp(14).hPaddingDp(10)
              .bgColorInt(Color.LTGRAY)
              .text("I am just some text")
              .onClick(onBtnClick)
          )

          .child(Spaces.hSpace(1))
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
                .layout(LEFT_OF, R.id.home_page_text_left_anchor)
                .text("COL ??")
            )

            .child(
                TextViews.build()
                .id(R.id.home_page_text_left_anchor)
                .wrapContent()
                .layout(LEFT_OF, R.id.home_page_text_center_anchor)
                .text("COL A")
            )

            .child(
                TextViews.build()
                .id(R.id.home_page_text_center_anchor)
                .editMode().bgColorInt(Color.MAGENTA)
                .bgColorInt(Color.CYAN)
                .wrapContent()
                .hMarginsDp(16)
                .layout(CENTER_IN_PARENT, true)
                .text("COL B")
            )

            .child(
                TextViews.build()
                .id(R.id.home_page_text_right_anchor)
                .wrapContent()
                .layout(RIGHT_OF, R.id.home_page_text_center_anchor)
                .text("COL C")
            )

            .child(
                TextViews.build()
                .wrapContent()
                .gravity(CENTER)
                .layout(ALIGN_PARENT_RIGHT, true)
                .layout(RIGHT_OF, R.id.home_page_text_right_anchor)
                .text("COL !!")
            )
        )

        .child(Spaces.vSpace(1).bottomMarginDp(768))

    .applyOnto(this);
    // @formatter:on
  }

  private ImageViewBuilder imageCell(@ColorInt int color) {
    // @formatter:off
    return ImageViews.build()
        .bgColorInt(color)
        .width(0).editMode().heightDp(64).layout(WEIGHT, 1f)
        .heightFlexPct(1f);
    // @formatter:on
  }

  @Override
  public void setButtonColor(@ColorInt int color) {
    TextView tv = (TextView) findViewById(R.id.home_page_text);
    tv.setBackgroundColor(color);
    // @formatter:off
    TextViews.build()
        .parent(LinearLayouts.build())
        .width(0).layout(WEIGHT, 3f)
        .height(tv.getHeight())
        .applyOnto(tv);
    // @formatter:on
  }

  @Override
  public void setImageUrl(@NonNull final String url) {
    predicatedViewAction((ImageView) findViewById(R.id.home_page_image), //
        LoadUrlAction.class, new LoadUrlAction(url), new LoadUrlPredicate(), //
        BackoffPolicy.EXPONENTIAL, 10, 10);
  }

  private final OnClickListener onBtnClick = new OnClickListener() {
    @Override
    public void onClick(View v) {
      presenter().buttonClicked();
    }
  };

  private static final class LoadUrlPredicate implements ViewPredicate<ImageView> {
    @Override
    public boolean isViewPredicateTrue(@NonNull ImageView iv) {
      return iv.getWidth() > 5 && iv.getHeight() > 5;
    }
  }

  private static class LoadUrlAction implements ViewAction<ImageView> {
    @NonNull private final String url;

    private LoadUrlAction(@NonNull String url) {
      this.url = url;
    }

    @Override
    public void performViewAction(@NonNull ImageView iv) {
      Picasso.with(iv.getContext()) //
          .load(url) //
          .fit() //
          .centerInside() //
          .into(iv);
    }
  }
}
