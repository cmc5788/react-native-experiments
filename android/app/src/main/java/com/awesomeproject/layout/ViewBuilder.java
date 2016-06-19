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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.util.TypedValue.COMPLEX_UNIT_PX;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public abstract class ViewBuilder<VB extends ViewBuilder<?, V>, V extends View> {

  private static final int LAYOUT_DIM_EMPTY = Integer.MIN_VALUE;

  public static abstract class Prop<V extends View, T> {
    @NonNull public final String name;
    protected T val;
    private boolean onlyInEditMode;

    public Prop(@NonNull String name) {
      this.name = name;
    }

    public void set(T val) {
      this.val = val;
    }

    public abstract void apply(@NonNull V v);

    public void setOnlyInEditMode() {
      onlyInEditMode = true;
    }

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

  public static abstract class DimProp<V extends View> extends Prop<V, Integer> {
    private final int type;

    public static String makeName(String name, int type) {
      return name + "_" + type;
    }

    public DimProp(@NonNull String name, int type) {
      super(makeName(name, type));
      this.type = type;
    }

    @CallSuper
    @Override
    public void apply(@NonNull V v) {
      if (val != null) val = dimToPx(val, type);
    }
  }

  public static abstract class LayoutProp<LP extends ViewGroup.LayoutParams, T> {
    @NonNull public final String name;
    private boolean onlyInEditMode;

    public LayoutProp(@NonNull String name) {
      this.name = name;
    }

    public abstract void apply(@NonNull LP lp, T val);

    public void setOnlyInEditMode() {
      onlyInEditMode = true;
    }

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

  public static abstract class LayoutDimProp<LP extends ViewGroup.LayoutParams>
      extends LayoutProp<LP, Integer> {
    private final int type;

    public static String makeName(String name, int type) {
      return name + "_" + type;
    }

    public LayoutDimProp(@NonNull String name, int type) {
      super(makeName(name, type));
      this.type = type;
    }

    public abstract void applyDim(@NonNull LP lp, int val);

    @Override
    public final void apply(@NonNull LP lp, Integer val) {
      if (val != null) applyDim(lp, dimToPx(val, type));
    }
  }

  public static class IdProp extends Prop<View, Integer> {
    private static final String NAME = "ID";

    public IdProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull View v) {
      if (val != null) v.setId(val);
    }
  }

  public static class BgColorResProp extends Prop<View, Integer> {
    private static final String NAME = "BG_COLOR_RES";

    public BgColorResProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull View v) {
      if (val != null) {
        //noinspection deprecation
        v.setBackgroundColor(v.getResources().getColor(val));
      }
    }
  }

  public static class BgColorIntProp extends Prop<View, Integer> {
    private static final String NAME = "BG_COLOR_INT";

    public BgColorIntProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull View v) {
      if (val != null) v.setBackgroundColor(val);
    }
  }

  public static class PaddingLeftProp extends DimProp<View> {
    private static final String NAME = "PADDING_LEFT";

    public PaddingLeftProp(int type) {
      super(NAME, type);
    }

    @Override
    public void apply(@NonNull View v) {
      super.apply(v);
      if (val != null) {
        v.setPadding(val, v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
      }
    }
  }

  public static class PaddingTopProp extends DimProp<View> {
    private static final String NAME = "PADDING_TOP";

    public PaddingTopProp(int type) {
      super(NAME, type);
    }

    @Override
    public void apply(@NonNull View v) {
      super.apply(v);
      if (val != null) {
        v.setPadding(v.getPaddingLeft(), val, v.getPaddingRight(), v.getPaddingBottom());
      }
    }
  }

  public static class PaddingRightProp extends DimProp<View> {
    private static final String NAME = "PADDING_RIGHT";

    public PaddingRightProp(int type) {
      super(NAME, type);
    }

    @Override
    public void apply(@NonNull View v) {
      super.apply(v);
      if (val != null) {
        v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), val, v.getPaddingBottom());
      }
    }
  }

  public static class PaddingBottomProp extends DimProp<View> {
    private static final String NAME = "PADDING_BOTTOM";

    public PaddingBottomProp(int type) {
      super(NAME, type);
    }

    @Override
    public void apply(@NonNull View v) {
      super.apply(v);
      if (val != null) {
        v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), val);
      }
    }
  }

  public static class OnClickProp extends Prop<View, View.OnClickListener> {
    private static final String NAME = "ON_CLICK";

    public OnClickProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull View v) {
      if (val != null) v.setOnClickListener(val);
    }
  }

  public static class LayoutWidthProp extends LayoutDimProp<ViewGroup.LayoutParams> {

    public LayoutWidthProp(int type) {
      super(LayoutParams.WIDTH, type);
    }

    @Override
    public void applyDim(@NonNull ViewGroup.LayoutParams lp, int val) {
      lp.width = val;
    }
  }

  public static class LayoutHeightProp extends LayoutDimProp<ViewGroup.LayoutParams> {

    public LayoutHeightProp(int type) {
      super(LayoutParams.HEIGHT, type);
    }

    @Override
    public void applyDim(@NonNull ViewGroup.LayoutParams lp, int val) {
      lp.height = val;
    }
  }

  public static class MarginLeftProp extends LayoutDimProp<ViewGroup.MarginLayoutParams> {

    public MarginLeftProp(int type) {
      super(LayoutParams.MARGIN_LEFT, type);
    }

    @Override
    public void applyDim(@NonNull ViewGroup.MarginLayoutParams lp, int val) {
      lp.leftMargin = val;
    }
  }

  public static class MarginTopProp extends LayoutDimProp<ViewGroup.MarginLayoutParams> {

    public MarginTopProp(int type) {
      super(LayoutParams.MARGIN_TOP, type);
    }

    @Override
    public void applyDim(@NonNull ViewGroup.MarginLayoutParams lp, int val) {
      lp.topMargin = val;
    }
  }

  public static class MarginRightProp extends LayoutDimProp<ViewGroup.MarginLayoutParams> {

    public MarginRightProp(int type) {
      super(LayoutParams.MARGIN_RIGHT, type);
    }

    @Override
    public void applyDim(@NonNull ViewGroup.MarginLayoutParams lp, int val) {
      lp.rightMargin = val;
    }
  }

  public static class MarginBottomProp extends LayoutDimProp<ViewGroup.MarginLayoutParams> {

    public MarginBottomProp(int type) {
      super(LayoutParams.MARGIN_BOTTOM, type);
    }

    @Override
    public void applyDim(@NonNull ViewGroup.MarginLayoutParams lp, int val) {
      lp.bottomMargin = val;
    }
  }

  private List<ViewBuilder> children;

  private boolean nextPropOnlyInEditMode;

  private Map<String, Prop<? super V, ?>> props;
  private Map<String, Object> layoutPropMap;
  private Map<String, Boolean> layoutPropEditModes;
  private Set<LayoutProp> layoutProps;

  private ViewBuilder parent;

  public ViewBuilder() {
    // @formatter:off
    regProps(Arrays.asList(
        new IdProp(),
        new BgColorResProp(),
        new BgColorIntProp(),
        new PaddingLeftProp(COMPLEX_UNIT_PX),
        new PaddingLeftProp(COMPLEX_UNIT_DIP),
        new PaddingTopProp(COMPLEX_UNIT_PX),
        new PaddingTopProp(COMPLEX_UNIT_DIP),
        new PaddingRightProp(COMPLEX_UNIT_PX),
        new PaddingRightProp(COMPLEX_UNIT_DIP),
        new PaddingBottomProp(COMPLEX_UNIT_PX),
        new PaddingBottomProp(COMPLEX_UNIT_DIP),
        new OnClickProp()
    ));
    (layoutProps = new HashSet<>()).addAll(Arrays.asList(
        new LayoutWidthProp(COMPLEX_UNIT_PX),
        new LayoutWidthProp(COMPLEX_UNIT_DIP),
        new LayoutHeightProp(COMPLEX_UNIT_PX),
        new LayoutHeightProp(COMPLEX_UNIT_DIP),
        new MarginLeftProp(COMPLEX_UNIT_PX),
        new MarginLeftProp(COMPLEX_UNIT_DIP),
        new MarginTopProp(COMPLEX_UNIT_PX),
        new MarginTopProp(COMPLEX_UNIT_DIP),
        new MarginRightProp(COMPLEX_UNIT_PX),
        new MarginRightProp(COMPLEX_UNIT_DIP),
        new MarginBottomProp(COMPLEX_UNIT_PX),
        new MarginBottomProp(COMPLEX_UNIT_DIP)
    ));
    // @formatter:on
  }

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
    Prop<? super V, T> prop = (Prop<? super V, T>) props.get(name);
    if (nextPropOnlyInEditMode) {
      nextPropOnlyInEditMode = false;
      prop.setOnlyInEditMode();
    }
    prop.set(val);
    //noinspection unchecked
    return (VB) this;
  }

  public <T> VB layout(@NonNull String layoutPropName, T val) {
    if (layoutPropMap == null) layoutPropMap = new HashMap<>();
    if (nextPropOnlyInEditMode) {
      nextPropOnlyInEditMode = false;
      if (layoutPropEditModes == null) layoutPropEditModes = new HashMap<>();
      layoutPropEditModes.put(layoutPropName, true);
    }
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

  public VB editMode() {
    this.nextPropOnlyInEditMode = true;
    //noinspection unchecked
    return (VB) this;
  }

  public VB id(@IdRes int id) {
    setProp(IdProp.NAME, id);
    //noinspection unchecked
    return (VB) this;
  }

  public VB bgColor(@ColorRes int bgColorResId) {
    setProp(BgColorResProp.NAME, bgColorResId);
    //noinspection unchecked
    return (VB) this;
  }

  public VB bgColorInt(@ColorInt int bgColorInt) {
    setProp(BgColorIntProp.NAME, bgColorInt);
    //noinspection unchecked
    return (VB) this;
  }

  public VB padding(int l, int t, int r, int b) {
    setProp(DimProp.makeName(PaddingLeftProp.NAME, COMPLEX_UNIT_PX), l);
    setProp(DimProp.makeName(PaddingTopProp.NAME, COMPLEX_UNIT_PX), t);
    setProp(DimProp.makeName(PaddingRightProp.NAME, COMPLEX_UNIT_PX), r);
    setProp(DimProp.makeName(PaddingBottomProp.NAME, COMPLEX_UNIT_PX), b);
    //noinspection unchecked
    return (VB) this;
  }

  public VB paddingDp(int l, int t, int r, int b) {
    setProp(DimProp.makeName(PaddingLeftProp.NAME, COMPLEX_UNIT_DIP), l);
    setProp(DimProp.makeName(PaddingTopProp.NAME, COMPLEX_UNIT_DIP), t);
    setProp(DimProp.makeName(PaddingRightProp.NAME, COMPLEX_UNIT_DIP), r);
    setProp(DimProp.makeName(PaddingBottomProp.NAME, COMPLEX_UNIT_DIP), b);
    //noinspection unchecked
    return (VB) this;
  }

  public VB padding(int all) {
    return this.padding(all, all, all, all);
  }

  public VB paddingDp(int all) {
    return this.paddingDp(all, all, all, all);
  }

  public VB vPadding(int all) {
    setProp(DimProp.makeName(PaddingTopProp.NAME, COMPLEX_UNIT_PX), all);
    setProp(DimProp.makeName(PaddingBottomProp.NAME, COMPLEX_UNIT_PX), all);
    //noinspection unchecked
    return (VB) this;
  }

  public VB vPaddingDp(int all) {
    setProp(DimProp.makeName(PaddingTopProp.NAME, COMPLEX_UNIT_DIP), all);
    setProp(DimProp.makeName(PaddingBottomProp.NAME, COMPLEX_UNIT_DIP), all);
    //noinspection unchecked
    return (VB) this;
  }

  public VB hPadding(int all) {
    setProp(DimProp.makeName(PaddingLeftProp.NAME, COMPLEX_UNIT_PX), all);
    setProp(DimProp.makeName(PaddingRightProp.NAME, COMPLEX_UNIT_PX), all);
    //noinspection unchecked
    return (VB) this;
  }

  public VB hPaddingDp(int all) {
    setProp(DimProp.makeName(PaddingLeftProp.NAME, COMPLEX_UNIT_DIP), all);
    setProp(DimProp.makeName(PaddingRightProp.NAME, COMPLEX_UNIT_DIP), all);
    //noinspection unchecked
    return (VB) this;
  }

  public VB leftPadding(int l) {
    setProp(DimProp.makeName(PaddingLeftProp.NAME, COMPLEX_UNIT_PX), l);
    //noinspection unchecked
    return (VB) this;
  }

  public VB leftPaddingDp(int l) {
    setProp(DimProp.makeName(PaddingLeftProp.NAME, COMPLEX_UNIT_DIP), l);
    //noinspection unchecked
    return (VB) this;
  }

  public VB topPadding(int t) {
    setProp(DimProp.makeName(PaddingTopProp.NAME, COMPLEX_UNIT_PX), t);
    //noinspection unchecked
    return (VB) this;
  }

  public VB topPaddingDp(int t) {
    setProp(DimProp.makeName(PaddingTopProp.NAME, COMPLEX_UNIT_DIP), t);
    //noinspection unchecked
    return (VB) this;
  }

  public VB rightPadding(int r) {
    setProp(DimProp.makeName(PaddingRightProp.NAME, COMPLEX_UNIT_PX), r);
    //noinspection unchecked
    return (VB) this;
  }

  public VB rightPaddingDp(int r) {
    setProp(DimProp.makeName(PaddingRightProp.NAME, COMPLEX_UNIT_DIP), r);
    //noinspection unchecked
    return (VB) this;
  }

  public VB bottomPadding(int b) {
    setProp(DimProp.makeName(PaddingBottomProp.NAME, COMPLEX_UNIT_PX), b);
    //noinspection unchecked
    return (VB) this;
  }

  public VB bottomPaddingDp(int b) {
    setProp(DimProp.makeName(PaddingBottomProp.NAME, COMPLEX_UNIT_DIP), b);
    //noinspection unchecked
    return (VB) this;
  }

  public VB onClick(View.OnClickListener onClick) {
    setProp(OnClickProp.NAME, onClick);
    //noinspection unchecked
    return (VB) this;
  }

  // -----------
  // LAYOUT PARAMS

  public VB width(int w) {
    return layout(LayoutDimProp.makeName(LayoutParams.WIDTH, COMPLEX_UNIT_PX), w);
  }

  public VB widthDp(int w) {
    return layout(LayoutDimProp.makeName(LayoutParams.WIDTH, COMPLEX_UNIT_DIP), w);
  }

  public VB height(int h) {
    return layout(LayoutDimProp.makeName(LayoutParams.HEIGHT, COMPLEX_UNIT_PX), h);
  }

  public VB heightDp(int h) {
    return layout(LayoutDimProp.makeName(LayoutParams.HEIGHT, COMPLEX_UNIT_DIP), h);
  }

  public VB dims(int w, int h) {
    width(w);
    return height(h);
  }

  public VB dimsDp(int w, int h) {
    widthDp(w);
    return heightDp(h);
  }

  public VB dims(int wh) {
    width(wh);
    return height(wh);
  }

  public VB dimsDp(int wh) {
    widthDp(wh);
    return heightDp(wh);
  }

  public VB wrapContent() {
    width(WRAP_CONTENT);
    return height(WRAP_CONTENT);
  }

  public VB matchParent() {
    width(MATCH_PARENT);
    return height(MATCH_PARENT);
  }

  public VB matchWidth() {
    width(MATCH_PARENT);
    return height(WRAP_CONTENT);
  }

  public VB matchHeight() {
    width(WRAP_CONTENT);
    return height(MATCH_PARENT);
  }

  // -----------

  public VB leftMargin(int l) {
    return layout(LayoutDimProp.makeName(LayoutParams.MARGIN_LEFT, COMPLEX_UNIT_PX), l);
  }

  public VB leftMarginDp(int l) {
    return layout(LayoutDimProp.makeName(LayoutParams.MARGIN_LEFT, COMPLEX_UNIT_DIP), l);
  }

  public VB topMargin(int t) {
    return layout(LayoutDimProp.makeName(LayoutParams.MARGIN_TOP, COMPLEX_UNIT_PX), t);
  }

  public VB topMarginDp(int t) {
    return layout(LayoutDimProp.makeName(LayoutParams.MARGIN_TOP, COMPLEX_UNIT_DIP), t);
  }

  public VB rightMargin(int r) {
    return layout(LayoutDimProp.makeName(LayoutParams.MARGIN_RIGHT, COMPLEX_UNIT_PX), r);
  }

  public VB rightMarginDp(int r) {
    return layout(LayoutDimProp.makeName(LayoutParams.MARGIN_RIGHT, COMPLEX_UNIT_DIP), r);
  }

  public VB bottomMargin(int b) {
    return layout(LayoutDimProp.makeName(LayoutParams.MARGIN_BOTTOM, COMPLEX_UNIT_PX), b);
  }

  public VB bottomMarginDp(int b) {
    return layout(LayoutDimProp.makeName(LayoutParams.MARGIN_BOTTOM, COMPLEX_UNIT_DIP), b);
  }

  public VB margins(int l, int t, int r, int b) {
    leftMargin(l);
    topMargin(t);
    rightMargin(r);
    return bottomMargin(r);
  }

  public VB marginsDp(int l, int t, int r, int b) {
    leftMarginDp(l);
    topMarginDp(t);
    rightMarginDp(r);
    return bottomMarginDp(r);
  }

  public VB margins(int all) {
    return margins(all, all, all, all);
  }

  public VB marginsDp(int all) {
    return marginsDp(all, all, all, all);
  }

  public VB vMargins(int all) {
    topMargin(all);
    return bottomMargin(all);
  }

  public VB vMarginsDp(int all) {
    topMarginDp(all);
    return bottomMarginDp(all);
  }

  public VB hMargins(int all) {
    leftMargin(all);
    return rightMargin(all);
  }

  public VB hMarginsDp(int all) {
    leftMarginDp(all);
    return rightMarginDp(all);
  }

  // -----------

  public void buildInto(ViewGroup root) {
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

    applyLayoutProps(v);

    root.addView(v);
  }

  public void applyOnto(V v) {
    if (!(v instanceof ViewGroup)) throw reqArg("applyOnto: ViewGroup");

    ViewGroup vg = (ViewGroup) v;
    applyProps(v);

    buildChildren(vg);

    applyLayoutProps(v);
  }

  @SuppressWarnings("deprecation")
  private void applyProps(@NonNull V v) {
    if (props != null) {
      for (Prop<? super V, ?> p : props.values()) {
        if (!p.onlyInEditMode || v.isInEditMode()) {
          p.apply(v);
        }
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

    if (layoutPropMap != null && layoutProps != null) {
      for (Map.Entry<String, Object> e : layoutPropMap.entrySet()) {
        for (LayoutProp lp : layoutProps) {
          if (lp.name.equals(e.getKey())) {
            boolean onlyInEditMode = layoutPropEditModes != null && //
                layoutPropEditModes.get(lp.name) != null && //
                layoutPropEditModes.get(lp.name);
            if (!onlyInEditMode || v.isInEditMode()) {
              //noinspection unchecked
              lp.apply(plps, e.getValue());
            }
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

  private static int dimToPx(float dim, int type) {
    return (int) TypedValue.applyDimension(type, dim, Resources.getSystem().getDisplayMetrics());
  }
}
