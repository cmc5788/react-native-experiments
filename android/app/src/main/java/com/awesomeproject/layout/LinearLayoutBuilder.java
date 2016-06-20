package com.awesomeproject.layout;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import java.util.Arrays;
import java.util.Set;

public class LinearLayoutBuilder extends ViewBuilder<LinearLayoutBuilder, LinearLayout>
    implements LinearLayouts.LinearLayoutProps<LinearLayoutBuilder> {

  public static final class OrientationProp extends Prop<LinearLayout, Integer> {
    private static final String NAME = "ORIENTATION";

    public OrientationProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull LinearLayout ll) {
      Integer val = get();
      if (val != null) ll.setOrientation(val);
    }
  }

  public static final class GravityProp extends Prop<LinearLayout, Integer> {
    private static final String NAME = "GRAVITY";

    public GravityProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull LinearLayout ll) {
      Integer val = get();
      if (val != null) ll.setGravity(val);
    }
  }

  public static class LayoutGravityProp extends LayoutProp<LinearLayout.LayoutParams, Integer> {

    public LayoutGravityProp() {
      super(LayoutParams.GRAVITY);
    }

    @Override
    public void apply(@NonNull LinearLayout.LayoutParams lp, Integer val) {
      if (val != null) lp.gravity = val;
    }
  }

  public static class LayoutWeightProp extends LayoutProp<LinearLayout.LayoutParams, Float> {

    public LayoutWeightProp() {
      super(LayoutParams.WEIGHT);
    }

    @Override
    public void apply(@NonNull LinearLayout.LayoutParams lp, Float val) {
      if (val != null) lp.weight = val;
    }
  }

  public LinearLayoutBuilder() {
    // @formatter:off
    regProps(Arrays.asList(
        new OrientationProp(),
        new GravityProp()
    ));
    // @formatter:on
  }

  public LinearLayoutBuilder orientation(int orientation) {
    setProp(OrientationProp.NAME, orientation);
    return this;
  }

  public LinearLayoutBuilder gravity(int gravity) {
    setProp(GravityProp.NAME, gravity);
    return this;
  }

  @NonNull
  @Override
  protected LinearLayout createView(ViewGroup root) {
    return new LinearLayout(root.getContext());
  }

  @NonNull
  @Override
  protected ViewGroup.LayoutParams createEmptyLayoutParamsForChild() {
    return new LinearLayout.LayoutParams(0, 0);
  }

  @Override
  protected void provideLayoutPropsToChild(@NonNull Set<LayoutProp> layoutProps) {
    // @formatter:off
    layoutProps.addAll(Arrays.asList(
        new LayoutGravityProp(),
        new LayoutWeightProp()
    ));
    // @formatter:on
    super.provideLayoutPropsToChild(layoutProps);
  }
}
