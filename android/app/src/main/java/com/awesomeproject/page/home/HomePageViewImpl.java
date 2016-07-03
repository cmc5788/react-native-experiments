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
import android.widget.TextView;
import com.awesomeproject.CustomTextView;
import com.awesomeproject.MyNavigator.NavTag;
import com.awesomeproject.R;
import com.awesomeproject.layout.imageview.ImageViewBuilder;
import com.awesomeproject.layout.imageview.ImageViews;
import com.awesomeproject.layout.linearlayout.LinearLayouts;
import com.awesomeproject.layout.relativelayout.RelativeLayouts;
import com.awesomeproject.layout.scrollview.LateRestoreScrollView;
import com.awesomeproject.layout.scrollview.ScrollViews;
import com.awesomeproject.layout.space.Spaces;
import com.awesomeproject.layout.textview.TextViews;
import com.awesomeproject.util.ViewUtil.BackoffPolicy;
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
import static com.awesomeproject.util.ViewUtil.ViewAction;
import static com.awesomeproject.util.ViewUtil.ViewPredicate;
import static com.awesomeproject.util.ViewUtil.predicatedViewAction;
import static com.facebook.react.bridge.UiThreadUtil.assertOnUiThread;

public class HomePageViewImpl extends LateRestoreScrollView implements HomePageView {

  // -----
  // BOILERPLATE ... stuff we can abstract away later

  private HomePagePresenter presenter;

  /*package*/ void setPresenter(@NonNull HomePagePresenter presenter) {
    this.presenter = presenter;
  }

  public HomePageViewImpl(Context context) {
    super(context);
    init();
  }

  public HomePageViewImpl(Context context, AttributeSet attrs) {
    super(context, attrs);
    if (!isInEditMode()) throw new RuntimeException("no inflated instances.");
    init();
  }

  public HomePageViewImpl(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    if (!isInEditMode()) throw new RuntimeException("no inflated instances.");
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public HomePageViewImpl(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    if (!isInEditMode()) throw new RuntimeException("no inflated instances.");
    init();
  }

  private void init() {
    setId(R.id.home_page);
    buildLayout();
  }

  @Override
  public void setId(int id) {
    assertOnUiThread();
    // Making sure that React isn't setting this. Have to be a bit
    // defensive since it likes to go rogue setting IDs elsewhere.
    if (id != R.id.home_page) throw new IllegalArgumentException("not my id!");
    super.setId(id);
  }

  @Override
  public void receiveViewEvent(@NonNull String viewTag, @Nullable ReadableMap args) {
    presenter.receiveViewEvent(viewTag, args);
  }

  @Override
  public boolean respondsToTag(@NonNull String viewTag) {
    return presenter.respondsToTag(viewTag);
  }

  @NonNull
  @Override
  public NavTag navTag() {
    return presenter.navTag();
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
      presenter.buttonClicked();
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
