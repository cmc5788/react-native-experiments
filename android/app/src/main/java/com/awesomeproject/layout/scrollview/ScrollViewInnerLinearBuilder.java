package com.awesomeproject.layout.scrollview;

import android.support.annotation.NonNull;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.awesomeproject.layout.ViewBuilder;
import com.awesomeproject.layout.linearlayout.LinearLayoutBuilder;
import com.awesomeproject.layout.linearlayout.LinearLayouts;

import static android.widget.LinearLayout.VERTICAL;

public class ScrollViewInnerLinearBuilder
    extends ViewBuilder<ScrollViewInnerLinearBuilder, ScrollView>
    implements ScrollViews.ScrollViewProps<ScrollViewInnerLinearBuilder>,
    LinearLayouts.LinearLayoutProps<ScrollViewInnerLinearBuilder> {

  private static final ViewConverter<ScrollView, LinearLayout> CONVERTER = new ViewConverter< //
      ScrollView, LinearLayout>() {
    @NonNull
    @Override
    public LinearLayout convert(@NonNull ScrollView sv) {
      return (LinearLayout) sv.getChildAt(0);
    }
  };

  private final ScrollViewBuilder composedScrollViewBuilder;
  private final LinearLayoutBuilder composedLinearLayoutViewBuilder;

  public ScrollViewInnerLinearBuilder() {
    composedScrollViewBuilder = new ScrollViewBuilder();
    composedLinearLayoutViewBuilder = new LinearLayoutBuilder();

    composePropsFrom(composedScrollViewBuilder);
    composePropsFrom(composedLinearLayoutViewBuilder, CONVERTER);
    composeCreateViewFrom(composedScrollViewBuilder);
    composeEmptyLayoutParamsFrom(composedLinearLayoutViewBuilder);
    composeLayoutPropsFrom(composedLinearLayoutViewBuilder);

    // @formatter:off
    super.child(
        composedLinearLayoutViewBuilder
        .matchParent()
        .orientation(VERTICAL)
    );
    // @formatter:on

    fillViewport(true);
  }

  @Override
  public ScrollViewInnerLinearBuilder child(ViewBuilder child) {
    composedLinearLayoutViewBuilder.child(child);
    return this;
  }

  @Override
  public ScrollViewInnerLinearBuilder fillViewport(boolean fillViewport) {
    composedScrollViewBuilder.fillViewport(fillViewport);
    return this;
  }

  @Override
  public ScrollViewInnerLinearBuilder orientation(int orientation) {
    composedLinearLayoutViewBuilder.orientation(orientation);
    return this;
  }

  @Override
  public ScrollViewInnerLinearBuilder gravity(int gravity) {
    composedLinearLayoutViewBuilder.gravity(gravity);
    return this;
  }
}
