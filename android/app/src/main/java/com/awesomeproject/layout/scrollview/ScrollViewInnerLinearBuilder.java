package com.awesomeproject.layout.scrollview;

import android.support.annotation.NonNull;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.awesomeproject.layout.ViewBuilder;
import com.awesomeproject.layout.linearlayout.LinearLayoutBuilder;
import com.awesomeproject.layout.linearlayout.LinearLayouts;
import java.util.Arrays;

import static android.widget.LinearLayout.VERTICAL;

public class ScrollViewInnerLinearBuilder
    extends ViewBuilder<ScrollViewInnerLinearBuilder, ScrollView>
    implements ScrollViews.ScrollViewProps<ScrollViewInnerLinearBuilder>,
    LinearLayouts.LinearLayoutProps<ScrollViewInnerLinearBuilder> {

  private static final class OrientationProp extends Prop<ScrollView, Integer> {
    private static final String NAME = "ORIENTATION";

    public OrientationProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull ScrollView ll) {
      Integer val = get();
      if (val != null) {
        //noinspection WrongConstant
        ((LinearLayout) ll.getChildAt(0)).setOrientation(val);
      }
    }
  }

  private static final class GravityProp extends Prop<ScrollView, Integer> {
    private static final String NAME = "GRAVITY";

    public GravityProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull ScrollView ll) {
      Integer val = get();
      if (val != null) {
        ((LinearLayout) ll.getChildAt(0)).setGravity(val);
      }
    }
  }

  private final ScrollViewBuilder composedScrollViewBuilder;
  private final LinearLayoutBuilder composedLinearLayoutViewBuilder;

  public ScrollViewInnerLinearBuilder() {
    composedScrollViewBuilder = new ScrollViewBuilder();
    composedLinearLayoutViewBuilder = new LinearLayoutBuilder();

    composePropsFrom(composedScrollViewBuilder);
    composeCreateViewFrom(composedScrollViewBuilder);
    composeEmptyLayoutParamsFrom(composedLinearLayoutViewBuilder);
    composeLayoutPropsFrom(composedLinearLayoutViewBuilder);

    // @formatter:off
    regProps(Arrays.asList(
        new OrientationProp(),
        new GravityProp()
    ));


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
    setProp(OrientationProp.NAME, orientation);
    return this;
  }

  @Override
  public ScrollViewInnerLinearBuilder gravity(int gravity) {
    setProp(GravityProp.NAME, gravity);
    return this;
  }
}
