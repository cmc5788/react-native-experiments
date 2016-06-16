package com.awesomeproject.layout;

import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ViewBuilder<VB extends ViewBuilder<?, V>, V extends View> {

  public static abstract class Prop<V extends View, T> {
    protected T val;

    @NonNull
    public abstract String name();

    public abstract void set(T val);

    public abstract void apply(@NonNull V v);
  }

  private LayoutParamBuilder lpb;
  private List<ViewBuilder> children;

  private int id = View.NO_ID;
  private int bgColorResId = View.NO_ID;
  private int bgColorInt = Integer.MIN_VALUE;
  private int paddingL, paddingT, paddingR, paddingB;
  private View.OnClickListener onClick;

  private Map<String, Prop<? super V, ?>> props;

  @NonNull
  protected abstract V createView(ViewGroup root);

  protected void composePropsFrom(@NonNull ViewBuilder<?, ? super V> other) {
    if (other.props == null) return;
    if (props == null) props = new HashMap<>();
    props.putAll(other.props);
  }

  protected void regProp(@NonNull Prop<? super V, ?> prop) {
    if (props == null) props = new HashMap<>();
    props.put(prop.name(), prop);
  }

  protected <T> VB setProp(@NonNull String name, T val) {
    //noinspection unchecked
    ((Prop<? super V, T>) props.get(name)).set(val);
    //noinspection unchecked
    return (VB) this;
  }

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

  public VB children(ViewBuilder... children) {
    for (ViewBuilder child : children) {
      children().add(child);
    }
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

  public VB padding(int l, int t, int r, int b) {
    this.paddingL = l;
    this.paddingT = t;
    this.paddingR = r;
    this.paddingB = b;
    //noinspection unchecked
    return (VB) this;
  }

  public VB paddingDp(int l, int t, int r, int b) {
    this.paddingL = dpToPx(l);
    this.paddingT = dpToPx(t);
    this.paddingR = dpToPx(r);
    this.paddingB = dpToPx(b);
    //noinspection unchecked
    return (VB) this;
  }

  public VB padding(int all) {
    this.paddingL = this.paddingT = this.paddingR = this.paddingB = all;
    //noinspection unchecked
    return (VB) this;
  }

  public VB paddingDp(int all) {
    this.paddingL = this.paddingT = this.paddingR = this.paddingB = dpToPx(all);
    //noinspection unchecked
    return (VB) this;
  }

  public VB vPadding(int all) {
    this.paddingT = this.paddingB = all;
    //noinspection unchecked
    return (VB) this;
  }

  public VB vPaddingDp(int all) {
    this.paddingT = this.paddingB = dpToPx(all);
    //noinspection unchecked
    return (VB) this;
  }

  public VB hPadding(int all) {
    this.paddingL = this.paddingR = all;
    //noinspection unchecked
    return (VB) this;
  }

  public VB hPaddingDp(int all) {
    this.paddingL = this.paddingR = dpToPx(all);
    //noinspection unchecked
    return (VB) this;
  }

  public VB leftPadding(int l) {
    this.paddingL = l;
    //noinspection unchecked
    return (VB) this;
  }

  public VB leftPaddingDp(int l) {
    this.paddingL = dpToPx(l);
    //noinspection unchecked
    return (VB) this;
  }

  public VB topPadding(int t) {
    this.paddingT = t;
    //noinspection unchecked
    return (VB) this;
  }

  public VB topPaddingDp(int t) {
    this.paddingT = dpToPx(t);
    //noinspection unchecked
    return (VB) this;
  }

  public VB rightPadding(int r) {
    this.paddingR = r;
    //noinspection unchecked
    return (VB) this;
  }

  public VB rightPaddingDp(int r) {
    this.paddingR = dpToPx(r);
    //noinspection unchecked
    return (VB) this;
  }

  public VB bottomPadding(int b) {
    this.paddingB = b;
    //noinspection unchecked
    return (VB) this;
  }

  public VB bottomPaddingDp(int b) {
    this.paddingB = dpToPx(b);
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
      buildChildren((ViewGroup) v);
    }

    root.addView(v);
    v.setLayoutParams(lpb.build());
  }

  public void applyOnto(V v) {
    if (!(v instanceof ViewGroup)) throw reqArg("applyOnto: ViewGroup");
    if (lpb == null) throw reqArg("layout params");

    ViewGroup vg = (ViewGroup) v;
    applyProps(v);

    buildChildren(vg);

    vg.setLayoutParams(lpb.build());
  }

  @SuppressWarnings("deprecation")
  private void applyProps(@NonNull V v) {
    Resources res = v.getResources();

    if (id != View.NO_ID) v.setId(id);

    if (bgColorResId != View.NO_ID) {
      v.setBackgroundColor(res.getColor(bgColorResId));
    } else if (bgColorInt != Integer.MIN_VALUE) {
      v.setBackgroundColor(bgColorInt);
    }

    v.setPadding(paddingL, paddingT, paddingR, paddingB);

    if (onClick != null) {
      v.setOnClickListener(onClick);
    }

    if (props != null) {
      for (Prop<? super V, ?> p : props.values()) {
        p.apply(v);
      }
    }
  }

  private void buildChildren(ViewGroup vg) {
    for (ViewBuilder child : children) {
      child.buildInto(vg);
    }
  }

  private List<ViewBuilder> children() {
    if (children == null) children = new ArrayList<>();
    return children;
  }

  protected RuntimeException reqArg(String arg) {
    return new IllegalArgumentException(String.format("%s required.", arg));
  }

  /*package*/
  static int dpToPx(float dp) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
        Resources.getSystem().getDisplayMetrics());
  }
}
