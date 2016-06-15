package com.awesomeproject.layout;

import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public abstract class ViewBuilder<VB extends ViewBuilder, V extends View> {

  private LayoutParamBuilder lpb;
  private List<ViewBuilder> children;

  private int id = View.NO_ID;

  private int bgColorResId = View.NO_ID;
  private int bgColorInt = Integer.MIN_VALUE;

  private View.OnClickListener onClick;

  @NonNull
  protected abstract V createView(ViewGroup root);

  public VB layoutParams(LayoutParamBuilder lpb) {
    this.lpb = lpb;
    //noinspection unchecked
    return (VB) this;
  }

  public VB child(ViewBuilder child) {
    children().add(child);
    //noinspection unchecked
    return (VB) this;
  }

  public VB id(@IdRes int id) {
    this.id = id;
    //noinspection unchecked
    return (VB) this;
  }

  public VB bgColor(@ColorRes int bgColorResId) {
    this.bgColorResId = bgColorResId;
    //noinspection unchecked
    return (VB) this;
  }

  public VB bgColorInt(@ColorInt int bgColorInt) {
    this.bgColorInt = bgColorInt;
    //noinspection unchecked
    return (VB) this;
  }

  public VB onClick(View.OnClickListener onClick) {
    this.onClick = onClick;
    //noinspection unchecked
    return (VB) this;
  }

  public void buildInto(ViewGroup root) {
    if (lpb == null) throw reqArg("layout params");

    V v = createView(root);
    applyProps(v);

    boolean isViewGroup = (v instanceof ViewGroup);

    if (!isViewGroup && //
        (children != null && !children.isEmpty())) {
      throw new IllegalArgumentException("non-ViewGroup may not have children.");
    }

    if (isViewGroup) {
      ViewGroup vg = (ViewGroup) v;
      for (ViewBuilder child : children) {
        child.buildInto(vg);
      }
    }

    root.addView(v);
    v.setLayoutParams(lpb.build());
  }

  public void applyOnto(V v) {
    if (!(v instanceof ViewGroup)) throw reqArg("applyOnto: ViewGroup");
    if (lpb == null) throw reqArg("layout params");

    ViewGroup vg = (ViewGroup) v;
    applyProps(v);

    for (ViewBuilder child : children) {
      child.buildInto(vg);
    }

    vg.setLayoutParams(lpb.build());
  }

  @SuppressWarnings("deprecation")
  private void applyProps(View v) {
    Resources res = v.getResources();

    if (id != View.NO_ID) v.setId(id);

    if (bgColorResId != View.NO_ID) {
      v.setBackgroundColor(res.getColor(bgColorResId));
    } else if (bgColorInt != Integer.MIN_VALUE) {
      v.setBackgroundColor(bgColorInt);
    }

    if (onClick != null) {
      v.setOnClickListener(onClick);
    }
  }

  private List<ViewBuilder> children() {
    if (children == null) children = new ArrayList<>();
    return children;
  }

  protected RuntimeException reqArg(String arg) {
    return new IllegalArgumentException(String.format("%s required.", arg));
  }
}
