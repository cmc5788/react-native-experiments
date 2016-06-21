package com.awesomeproject.layout;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import java.util.Arrays;
import java.util.Set;

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
    composePropsFrom(composedScrollViewBuilder = new ScrollViewBuilder());

    // @formatter:off
    regProps(Arrays.asList(
        new OrientationProp(),
        new GravityProp()
    ));

    super.child(
        composedLinearLayoutViewBuilder = new LinearLayoutBuilder()
        .matchParent()
        .orientation(VERTICAL)
    );
    // @formatter:on

    fillViewport(true);
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

  @NonNull
  @Override
  protected ScrollView createView(ViewGroup root) {
    return composedScrollViewBuilder.createView(root);
  }

  @Override
  public ScrollViewInnerLinearBuilder child(ViewBuilder child) {
    composedLinearLayoutViewBuilder.child(child);
    return this;
  }

  @NonNull
  @Override
  protected ViewGroup.LayoutParams createEmptyLayoutParamsForChild() {
    return composedLinearLayoutViewBuilder.createEmptyLayoutParamsForChild();
  }

  @Override
  protected void provideLayoutPropsToChild(@NonNull Set<LayoutProp> layoutProps) {
    composedLinearLayoutViewBuilder.provideLayoutPropsToChild(layoutProps);
    super.provideLayoutPropsToChild(layoutProps);
  }
}
