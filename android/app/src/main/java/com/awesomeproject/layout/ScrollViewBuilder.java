package com.awesomeproject.layout;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class ScrollViewBuilder extends ViewBuilder<ScrollViewBuilder, ScrollView> {

  public static class FillViewportProp extends Prop<ScrollView, Boolean> {
    public static final String NAME = "FILL_VIEWPORT";

    @NonNull
    @Override
    public String name() {
      return NAME;
    }

    @Override
    public void set(Boolean val) {
      this.val = val;
    }

    @Override
    public void apply(@NonNull ScrollView tv) {
      if (val != null) tv.setFillViewport(val);
    }
  }

  public static class ScrollViewParamBuilder
      extends MarginLayoutParamBuilder<ScrollViewParamBuilder, ScrollView.LayoutParams> {

    @NonNull
    @Override
    protected ScrollView.LayoutParams createEmptyLayoutParams() {
      return new ScrollView.LayoutParams(EMPTY, EMPTY);
    }

    public ScrollViewParamBuilder gravity(int g) {
      lps().gravity = g;
      return this;
    }
  }

  public ScrollViewBuilder() {
    regProp(new FillViewportProp());
  }

  public ScrollViewBuilder fillViewport(boolean fillViewport) {
    setProp(FillViewportProp.NAME, fillViewport);
    return this;
  }

  @NonNull
  @Override
  protected ScrollView createView(ViewGroup root) {
    return new ScrollView(root.getContext());
  }
}
