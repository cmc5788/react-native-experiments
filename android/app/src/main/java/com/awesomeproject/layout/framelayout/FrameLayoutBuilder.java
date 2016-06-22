package com.awesomeproject.layout.framelayout;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.awesomeproject.layout.LayoutParams;
import com.awesomeproject.layout.ViewBuilder;
import java.util.Set;

public class FrameLayoutBuilder extends ViewBuilder<FrameLayoutBuilder, FrameLayout> {

  private static class LayoutGravityProp extends LayoutProp<FrameLayout.LayoutParams, Integer> {

    public LayoutGravityProp() {
      super(LayoutParams.GRAVITY);
    }

    @Override
    public void apply(@NonNull FrameLayout.LayoutParams lp, Integer val) {
      if (val != null) lp.gravity = val;
    }
  }

  @NonNull
  @Override
  protected FrameLayout createView(ViewGroup root) {
    return new FrameLayout(root.getContext());
  }

  @NonNull
  @Override
  protected ViewGroup.LayoutParams createEmptyLayoutParamsForChild() {
    return new FrameLayout.LayoutParams(0, 0);
  }

  @Override
  protected void provideLayoutPropsToChild(@NonNull Set<LayoutProp> layoutProps) {
    layoutProps.add(new LayoutGravityProp());
    super.provideLayoutPropsToChild(layoutProps);
  }
}
