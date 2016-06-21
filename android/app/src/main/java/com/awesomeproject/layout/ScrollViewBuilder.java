package com.awesomeproject.layout;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ScrollView;
import java.util.Collections;
import java.util.Set;

public class ScrollViewBuilder extends ViewBuilder<ScrollViewBuilder, ScrollView> implements
    ScrollViews.ScrollViewProps<ScrollViewBuilder> {

  public static class FillViewportProp extends Prop<ScrollView, Boolean> {
    private static final String NAME = "FILL_VIEWPORT";

    public FillViewportProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull ScrollView tv) {
      Boolean val = get();
      if (val != null) tv.setFillViewport(val);
    }
  }

  public static class LayoutGravityProp extends LayoutProp<ScrollView.LayoutParams, Integer> {

    public LayoutGravityProp() {
      super(LayoutParams.GRAVITY);
    }

    @Override
    public void apply(@NonNull ScrollView.LayoutParams lp, Integer val) {
      if (val != null) lp.gravity = val;
    }
  }

  public ScrollViewBuilder() {
    regProps(Collections.singleton(new FillViewportProp()));
  }

  @Override
  public ScrollViewBuilder fillViewport(boolean fillViewport) {
    setProp(FillViewportProp.NAME, fillViewport);
    return this;
  }

  @NonNull
  @Override
  protected ScrollView createView(ViewGroup root) {
    return new ScrollView(root.getContext());
  }

  @NonNull
  @Override
  protected ViewGroup.LayoutParams createEmptyLayoutParamsForChild() {
    return new ScrollView.LayoutParams(0, 0);
  }

  @Override
  protected void provideLayoutPropsToChild(@NonNull Set<LayoutProp> layoutProps) {
    layoutProps.add(new LayoutGravityProp());
    super.provideLayoutPropsToChild(layoutProps);
  }
}
