package com.awesomeproject.layout;

import android.content.res.Resources;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public abstract class ViewBuilder<VB extends ViewBuilder<?, V>, V extends View> {

  private static final int LAYOUT_DIM_EMPTY = Integer.MIN_VALUE;

  public static abstract class Prop<V extends View, T> {
    @NonNull public final String name;
    protected T val;

    public Prop(@NonNull String name) {
      this.name = name;
    }

    public abstract void set(T val);

    public abstract void apply(@NonNull V v);

    @Override
    public final boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Prop<?, ?> prop = (Prop<?, ?>) o;
      return name.equals(prop.name);
    }

    @Override
    public final int hashCode() {
      return name.hashCode();
    }
  }

  public static abstract class LayoutProp<LP extends ViewGroup.LayoutParams, T> {
    @NonNull public final String name;

    public LayoutProp(@NonNull String name) {
      this.name = name;
    }

    public abstract void apply(@NonNull LP lp, T val);

    @Override
    public final boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      LayoutProp<?, ?> that = (LayoutProp<?, ?>) o;
      return name.equals(that.name);
    }

    @Override
    public final int hashCode() {
      return name.hashCode();
    }
  }

  private List<ViewBuilder> children;

  private int editModeBgColorInt = Integer.MIN_VALUE;

  private int id = View.NO_ID;
  private int bgColorResId = View.NO_ID;
  private int bgColorInt = Integer.MIN_VALUE;
  private int paddingL, paddingT, paddingR, paddingB;
  private View.OnClickListener onClick;

  private int layoutWidth = LAYOUT_DIM_EMPTY, layoutHeight = LAYOUT_DIM_EMPTY;
  private int marginL, marginT, marginR, marginB;

  private Map<String, Prop<? super V, ?>> props;
  private Map<String, Object> layoutPropMap;
  private Set<LayoutProp> layoutProps;

  private ViewBuilder parent;

  @NonNull
  protected abstract V createView(ViewGroup root);

  @NonNull
  protected ViewGroup.LayoutParams createEmptyLayoutParamsForChild() {
    throw new UnsupportedOperationException();
  }

  @CallSuper
  protected void provideLayoutPropsToChild(@NonNull Set<LayoutProp> layoutProps) {
    // no-op
  }

  protected void composePropsFrom(@NonNull ViewBuilder<?, ? super V> other) {
    if (other.props != null) {
      if (props == null) props = new HashMap<>();
      props.putAll(other.props);
    }
  }

  protected void regProps(@NonNull Collection<? extends Prop<? super V, ?>> propsToReg) {
    if (props == null) props = new HashMap<>();
    for (Prop<? super V, ?> prop : propsToReg) {
      props.put(prop.name, prop);
    }
  }

  protected <T> VB setProp(@NonNull String name, T val) {
    //noinspection unchecked
    ((Prop<? super V, T>) props.get(name)).set(val);
    //noinspection unchecked
    return (VB) this;
  }

  public <T> VB layout(@NonNull String layoutPropName, T val) {
    if (layoutPropMap == null) layoutPropMap = new HashMap<>();
    layoutPropMap.put(layoutPropName, val);
    //noinspection unchecked
    return (VB) this;
  }

  public VB child(ViewBuilder child) {

    child.parent = this;
    if (child.layoutProps == null) child.layoutProps = new HashSet<>();
    //noinspection unchecked
    provideLayoutPropsToChild(child.layoutProps);

    children().add(child);
    //noinspection unchecked
    return (VB) this;
  }

  public VB children(ViewBuilder... children) {
    for (ViewBuilder child : children) {

      child.parent = this;
      if (child.layoutProps == null) child.layoutProps = new HashSet<>();
      //noinspection unchecked
      provideLayoutPropsToChild(child.layoutProps);

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

  public VB editModeBgColorInt(@ColorInt int bgColorInt) {
    this.editModeBgColorInt = bgColorInt;
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

  // -----------
  // LAYOUT PARAMS

  public VB width(int w) {
    this.layoutWidth = w;
    //noinspection unchecked
    return (VB) this;
  }

  public VB widthDp(int w) {
    this.layoutWidth = dpToPx(w);
    //noinspection unchecked
    return (VB) this;
  }

  public VB height(int h) {
    this.layoutHeight = h;
    //noinspection unchecked
    return (VB) this;
  }

  public VB heightDp(int h) {
    this.layoutHeight = dpToPx(h);
    //noinspection unchecked
    return (VB) this;
  }

  public VB dims(int w, int h) {
    this.layoutWidth = w;
    this.layoutHeight = h;
    //noinspection unchecked
    return (VB) this;
  }

  public VB dimsDp(int w, int h) {
    this.layoutWidth = dpToPx(w);
    this.layoutHeight = dpToPx(h);
    //noinspection unchecked
    return (VB) this;
  }

  public VB dims(int wh) {
    this.layoutWidth = wh;
    this.layoutHeight = wh;
    //noinspection unchecked
    return (VB) this;
  }

  public VB dimsDp(int wh) {
    this.layoutWidth = dpToPx(wh);
    this.layoutHeight = dpToPx(wh);
    //noinspection unchecked
    return (VB) this;
  }

  public VB wrapContent() {
    this.layoutWidth = WRAP_CONTENT;
    this.layoutHeight = WRAP_CONTENT;
    //noinspection unchecked
    return (VB) this;
  }

  public VB matchParent() {
    this.layoutWidth = MATCH_PARENT;
    this.layoutHeight = MATCH_PARENT;
    //noinspection unchecked
    return (VB) this;
  }

  public VB matchWidth() {
    this.layoutWidth = MATCH_PARENT;
    this.layoutHeight = WRAP_CONTENT;
    //noinspection unchecked
    return (VB) this;
  }

  public VB matchHeight() {
    this.layoutWidth = WRAP_CONTENT;
    this.layoutHeight = MATCH_PARENT;
    //noinspection unchecked
    return (VB) this;
  }

  // -----------

  public VB margins(int l, int t, int r, int b) {
    this.marginL = l;
    this.marginT = t;
    this.marginR = r;
    this.marginB = b;
    //noinspection unchecked
    return (VB) this;
  }

  public VB marginsDp(int l, int t, int r, int b) {
    return this.margins(dpToPx(l), dpToPx(t), dpToPx(r), dpToPx(b));
  }

  public VB margins(int all) {
    this.marginL = this.marginT = this.marginR = this.marginB = all;
    //noinspection unchecked
    return (VB) this;
  }

  public VB marginsDp(int all) {
    return this.margins(dpToPx(all));
  }

  public VB vMargins(int all) {
    this.marginT = this.marginB = all;
    //noinspection unchecked
    return (VB) this;
  }

  public VB vMarginsDp(int all) {
    return this.vMargins(dpToPx(all));
  }

  public VB hMargins(int all) {
    this.marginL = this.marginR = all;
    //noinspection unchecked
    return (VB) this;
  }

  public VB hMarginsDp(int all) {
    return this.hMargins(dpToPx(all));
  }

  public VB leftMargin(int l) {
    this.marginL = l;
    //noinspection unchecked
    return (VB) this;
  }

  public VB leftMarginDp(int l) {
    return this.leftMargin(dpToPx(l));
  }

  public VB topMargin(int t) {
    this.marginT = t;
    //noinspection unchecked
    return (VB) this;
  }

  public VB topMarginDp(int t) {
    return this.topMargin(dpToPx(t));
  }

  public VB rightMargin(int r) {
    this.marginR = r;
    //noinspection unchecked
    return (VB) this;
  }

  public VB rightMarginDp(int r) {
    return this.rightMargin(dpToPx(r));
  }

  public VB bottomMargin(int b) {
    this.marginB = b;
    //noinspection unchecked
    return (VB) this;
  }

  public VB bottomMarginDp(int b) {
    return this.bottomMargin(dpToPx(b));
  }

  // -----------

  public void buildInto(ViewGroup root) {
    //if (lpb == null) throw reqArg("layout params");

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

    //v.setLayoutParams(lpb.build());
    applyLayoutProps(v);

    root.addView(v);
  }

  public void applyOnto(V v) {
    if (!(v instanceof ViewGroup)) throw reqArg("applyOnto: ViewGroup");
    //if (lpb == null) throw reqArg("layout params");

    ViewGroup vg = (ViewGroup) v;
    applyProps(v);

    buildChildren(vg);

    //vg.setLayoutParams(lpb.build());
    applyLayoutProps(v);
  }

  @SuppressWarnings("deprecation")
  private void applyProps(@NonNull V v) {
    Resources res = v.getResources();

    if (id != View.NO_ID) v.setId(id);

    if (editModeBgColorInt != Integer.MIN_VALUE && v.isInEditMode()) {
      v.setBackgroundColor(editModeBgColorInt);
    } else if (bgColorResId != View.NO_ID) {
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

  private void applyLayoutProps(@NonNull V v) {
    ViewGroup.LayoutParams plps;
    if (parent == null) {
      plps = new ViewGroup.LayoutParams(0, 0);
    } else {
      plps = parent.createEmptyLayoutParamsForChild();
    }

    plps.width = layoutWidth;
    plps.height = layoutHeight;

    if (plps instanceof ViewGroup.MarginLayoutParams) {
      ViewGroup.MarginLayoutParams mlps = (ViewGroup.MarginLayoutParams) plps;
      mlps.leftMargin = marginL;
      mlps.topMargin = marginT;
      mlps.rightMargin = marginR;
      mlps.bottomMargin = marginB;
    }

    if (layoutPropMap != null && layoutProps != null) {
      for (Map.Entry<String, Object> e : layoutPropMap.entrySet()) {
        for (LayoutProp lp : layoutProps) {
          if (lp.name.equals(e.getKey())) {
            //noinspection unchecked
            lp.apply(plps, e.getValue());
            break;
          }
        }
      }
    }

    if (plps.width == LAYOUT_DIM_EMPTY || plps.height == LAYOUT_DIM_EMPTY) {
      throw reqArg("layout width & height");
    }

    v.setLayoutParams(plps);
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
