package com.awesomeproject.layout;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class LinearLayoutBuilder extends ViewBuilder<LinearLayoutBuilder, LinearLayout>
    implements LinearLayouts.LinearLayoutProps<LinearLayoutBuilder> {

  public static final class OrientationProp extends Prop<LinearLayout, Integer> {
    public static final String NAME = "ORIENTATION";

    @NonNull
    @Override
    public String name() {
      return NAME;
    }

    @Override
    public void set(Integer val) {
      this.val = val;
    }

    @Override
    public void apply(@NonNull LinearLayout ll) {
      if (val != null) ll.setOrientation(val);
    }
  }

  public static final class GravityProp extends Prop<LinearLayout, Integer> {
    public static final String NAME = "GRAVITY";

    @NonNull
    @Override
    public String name() {
      return NAME;
    }

    @Override
    public void set(Integer val) {
      this.val = val;
    }

    @Override
    public void apply(@NonNull LinearLayout ll) {
      if (val != null) ll.setGravity(val);
    }
  }

  public static class LinearLayoutParamBuilder
      extends MarginLayoutParamBuilder<LinearLayoutParamBuilder, LinearLayout.LayoutParams> {

    @NonNull
    @Override
    protected LinearLayout.LayoutParams createEmptyLayoutParams() {
      return new LinearLayout.LayoutParams(EMPTY, EMPTY);
    }

    public LinearLayoutParamBuilder gravity(int g) {
      lps().gravity = g;
      return this;
    }

    public LinearLayoutParamBuilder weight(float w) {
      lps().weight = w;
      return this;
    }
  }

  public LinearLayoutBuilder() {
    regProp(new OrientationProp());
    regProp(new GravityProp());
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
}
