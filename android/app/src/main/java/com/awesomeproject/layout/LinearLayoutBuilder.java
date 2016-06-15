package com.awesomeproject.layout;

import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class LinearLayoutBuilder extends ViewBuilder<LinearLayoutBuilder, LinearLayout> {

  public static class LinearLayoutParamBuilder
      extends LayoutParamBuilder<LinearLayoutParamBuilder, LinearLayout.LayoutParams> {

    @NonNull
    @Override
    protected LinearLayout.LayoutParams createEmptyLayoutParams() {
      return new LinearLayout.LayoutParams(0, 0);
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

  private int orientation = LinearLayout.HORIZONTAL;
  private int gravity = Gravity.NO_GRAVITY;

  public LinearLayoutBuilder orientation(int orientation) {
    this.orientation = orientation;
    return this;
  }

  public LinearLayoutBuilder gravity(int gravity) {
    this.gravity = gravity;
    return this;
  }

  @NonNull
  @Override
  protected LinearLayout createView(ViewGroup root) {
    LinearLayout ll = new LinearLayout(root.getContext());
    ll.setOrientation(orientation);
    ll.setGravity(gravity);
    return ll;
  }
}
