package com.awesomeproject.layout;

import android.view.ViewGroup;

import static com.awesomeproject.layout.ViewBuilder.dpToPx;

public abstract class MarginLayoutParamBuilder< //
    LPB extends MarginLayoutParamBuilder, LP extends ViewGroup.MarginLayoutParams>
    extends LayoutParamBuilder<LPB, LP> {

  public LPB margins(int l, int t, int r, int b) {
    lps().setMargins(l, t, r, b);
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB marginsDp(int l, int t, int r, int b) {
    lps().setMargins(dpToPx(l), dpToPx(t), dpToPx(r), dpToPx(b));
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB margins(int all) {
    lps().setMargins(all, all, all, all);
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB marginsDp(int all) {
    all = dpToPx(all);
    lps().setMargins(all, all, all, all);
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB vMargins(int all) {
    topMargin(all);
    bottomMargin(all);
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB vMarginsDp(int all) {
    all = dpToPx(all);
    topMargin(all);
    bottomMargin(all);
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB hMargins(int all) {
    leftMargin(all);
    rightMargin(all);
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB hMarginsDp(int all) {
    all = dpToPx(all);
    leftMargin(all);
    rightMargin(all);
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB leftMargin(int l) {
    lps().leftMargin = l;
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB leftMarginDp(int l) {
    lps().leftMargin = dpToPx(l);
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB topMargin(int t) {
    lps().topMargin = t;
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB topMarginDp(int t) {
    lps().topMargin = dpToPx(t);
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB rightMargin(int r) {
    lps().rightMargin = r;
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB rightMarginDp(int r) {
    lps().rightMargin = dpToPx(r);
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB bottomMargin(int b) {
    lps().bottomMargin = b;
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB bottomMarginDp(int b) {
    lps().bottomMargin = dpToPx(b);
    //noinspection unchecked
    return (LPB) this;
  }
}
