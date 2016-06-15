package com.awesomeproject.layout;

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

  public VB endChild() {
    // no-op, just for prettiness
    //noinspection unchecked
    return (VB) this;
  }

  public VB id(@IdRes int id) {
    this.id = id;
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

    if (id != View.NO_ID) v.setId(id);

    if (onClick != null) v.setOnClickListener(onClick);

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

    if (id != View.NO_ID) vg.setId(id);

    if (onClick != null) vg.setOnClickListener(onClick);

    for (ViewBuilder child : children) {
      child.buildInto(vg);
    }

    vg.setLayoutParams(lpb.build());
  }

  private List<ViewBuilder> children() {
    if (children == null) children = new ArrayList<>();
    return children;
  }

  protected RuntimeException reqArg(String arg) {
    return new IllegalArgumentException(String.format("%s required.", arg));
  }
}
