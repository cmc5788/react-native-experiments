package com.awesomeproject.layout;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class FrameLayoutBuilder extends ViewBuilder<FrameLayoutBuilder, FrameLayout> {

  public static class FrameLayoutParamBuilder
      extends LayoutParamBuilder<FrameLayoutParamBuilder, FrameLayout.LayoutParams> {

    @NonNull
    @Override
    protected FrameLayout.LayoutParams createEmptyLayoutParams() {
      return new FrameLayout.LayoutParams(EMPTY, EMPTY);
    }

    public FrameLayoutParamBuilder gravity(int g) {
      lps().gravity = g;
      return this;
    }
  }

  @NonNull
  @Override
  protected FrameLayout createView(ViewGroup root) {
    return new FrameLayout(root.getContext());
  }
}
