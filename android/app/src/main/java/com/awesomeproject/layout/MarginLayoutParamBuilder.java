package com.awesomeproject.layout;

import android.view.ViewGroup;

public abstract class MarginLayoutParamBuilder< //
    LPB extends MarginLayoutParamBuilder, LP extends ViewGroup.MarginLayoutParams>
    extends LayoutParamBuilder<LPB, LP> {

  public LPB margins(int l, int t, int r, int b) {
    lps().setMargins(l, t, r, b);
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB leftMargin(int l) {
    lps().leftMargin = l;
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB topMargin(int t) {
    lps().topMargin = t;
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB rightMargin(int r) {
    lps().rightMargin = r;
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB bottomMargin(int b) {
    lps().bottomMargin = b;
    //noinspection unchecked
    return (LPB) this;
  }
}
