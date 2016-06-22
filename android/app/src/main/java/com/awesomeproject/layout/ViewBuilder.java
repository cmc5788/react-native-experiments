package com.awesomeproject.layout;

import android.content.res.Resources;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.util.TypedValue.COMPLEX_UNIT_PX;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public abstract class ViewBuilder<VB extends ViewBuilder<?, V>, V extends View> {

  private static final int LAYOUT_DIM_EMPTY = Integer.MIN_VALUE;

  private static final String EDIT_MODE_TAG = "ONLY_IN_EDIT_MODE.";

  public static <V extends View> ViewBuilder<ViewBuilder<?, V>, V> fromXml(
      @LayoutRes final int layoutResId) {
    return new ViewBuilder<ViewBuilder<?, V>, V>() {
      @NonNull
      @Override
      protected V createView(ViewGroup root) {
        //noinspection unchecked
        return (V) LayoutInflater.from(root.getContext()).inflate(layoutResId, root, false);
      }
    };
  }

  private static <V extends View, T> Prop<V, T> simpleSyncPropCopy(final Prop<V, T> prop) {
    return new Prop<V, T>(prop.name) {
      @Override
      public void apply(@NonNull V v) {
        T t = prop.get();
        prop.set(get());
        prop.apply(v);
        prop.set(t);
      }
    };
  }

  public static abstract class Prop<V extends View, T> {
    @NonNull public final String name;
    private T val;

    public Prop(@NonNull String name) {
      this.name = name;
    }

    public void set(@Nullable T val) {
      this.val = val;
    }

    @Nullable
    public T get() {
      return val;
    }

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

  private static class IdProp extends Prop<View, Integer> {
    private static final String NAME = "ID";

    public IdProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull View v) {
      Integer val = get();
      if (val != null) v.setId(val);
    }
  }

  private static class BgColorResProp extends Prop<View, Integer> {
    private static final String NAME = "BG_COLOR_RES";

    public BgColorResProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull View v) {
      Integer val = get();
      if (val != null) {
        //noinspection deprecation
        v.setBackgroundColor(v.getResources().getColor(val));
      }
    }
  }

  private static class BgColorIntProp extends Prop<View, Integer> {
    private static final String NAME = "BG_COLOR_INT";

    public BgColorIntProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull View v) {
      Integer val = get();
      if (val != null) v.setBackgroundColor(val);
    }
  }

  private static class PaddingLeftProp extends Prop<View, DimVal> {
    private static final String NAME = "PADDING_LEFT";

    public PaddingLeftProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull View v) {
      DimVal val = get();
      if (val != null) {
        v.setPadding(val.px(), v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
      }
    }
  }

  private static class PaddingTopProp extends Prop<View, DimVal> {
    private static final String NAME = "PADDING_TOP";

    public PaddingTopProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull View v) {
      DimVal val = get();
      if (val != null) {
        v.setPadding(v.getPaddingLeft(), val.px(), v.getPaddingRight(), v.getPaddingBottom());
      }
    }
  }

  private static class PaddingRightProp extends Prop<View, DimVal> {
    private static final String NAME = "PADDING_RIGHT";

    public PaddingRightProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull View v) {
      DimVal val = get();
      if (val != null) {
        v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), val.px(), v.getPaddingBottom());
      }
    }
  }

  private static class PaddingBottomProp extends Prop<View, DimVal> {
    private static final String NAME = "PADDING_BOTTOM";

    public PaddingBottomProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull View v) {
      DimVal val = get();
      if (val != null) {
        v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), val.px());
      }
    }
  }

  private static class OnClickProp extends Prop<View, View.OnClickListener> {
    private static final String NAME = "ON_CLICK";

    public OnClickProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull View v) {
      View.OnClickListener val = get();
      if (val != null) v.setOnClickListener(val);
    }
  }

  private static class WidthFlexMinProp extends Prop<View, DimVal> {
    private static final String NAME = "WIDTH_FLEX_MIN";

    public WidthFlexMinProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull View view) {
      // no-op
    }
  }

  private static class WidthFlexMaxProp extends Prop<View, DimVal> {
    private static final String NAME = "WIDTH_FLEX_MAX";

    public WidthFlexMaxProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull View view) {
      // no-op
    }
  }

  private static class WidthFlexPctProp extends Prop<View, Float> {
    private static final String NAME = "WIDTH_FLEX_PCT";

    public WidthFlexPctProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull View view) {
      // no-op
    }
  }

  private static class HeightFlexMinProp extends Prop<View, DimVal> {
    private static final String NAME = "HEIGHT_FLEX_MIN";

    public HeightFlexMinProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull View view) {
      // no-op
    }
  }

  private static class HeightFlexMaxProp extends Prop<View, DimVal> {
    private static final String NAME = "HEIGHT_FLEX_MAX";

    public HeightFlexMaxProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull View view) {
      // no-op
    }
  }

  private static class HeightFlexPctProp extends Prop<View, Float> {
    private static final String NAME = "HEIGHT_FLEX_PCT";

    public HeightFlexPctProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull View view) {
      // no-op
    }
  }

  private static class LayoutWidthProp extends LayoutProp<ViewGroup.LayoutParams, DimVal> {

    public LayoutWidthProp() {
      super(LayoutParams.WIDTH);
    }

    @Override
    public void apply(@NonNull ViewGroup.LayoutParams lp, DimVal val) {
      if (val != null) lp.width = val.px();
    }
  }

  private static class LayoutHeightProp extends LayoutProp<ViewGroup.LayoutParams, DimVal> {

    public LayoutHeightProp() {
      super(LayoutParams.HEIGHT);
    }

    @Override
    public void apply(@NonNull ViewGroup.LayoutParams lp, DimVal val) {
      if (val != null) lp.height = val.px();
    }
  }

  private static class MarginLeftProp extends LayoutProp<ViewGroup.MarginLayoutParams, DimVal> {

    public MarginLeftProp() {
      super(LayoutParams.MARGIN_LEFT);
    }

    @Override
    public void apply(@NonNull ViewGroup.MarginLayoutParams lp, DimVal val) {
      if (val != null) lp.leftMargin = val.px();
    }
  }

  private static class MarginTopProp extends LayoutProp<ViewGroup.MarginLayoutParams, DimVal> {

    public MarginTopProp() {
      super(LayoutParams.MARGIN_TOP);
    }

    @Override
    public void apply(@NonNull ViewGroup.MarginLayoutParams lp, DimVal val) {
      if (val != null) lp.topMargin = val.px();
    }
  }

  private static class MarginRightProp extends LayoutProp<ViewGroup.MarginLayoutParams, DimVal> {

    public MarginRightProp() {
      super(LayoutParams.MARGIN_RIGHT);
    }

    @Override
    public void apply(@NonNull ViewGroup.MarginLayoutParams lp, DimVal val) {
      if (val != null) lp.rightMargin = val.px();
    }
  }

  private static class MarginBottomProp extends LayoutProp<ViewGroup.MarginLayoutParams, DimVal> {

    public MarginBottomProp() {
      super(LayoutParams.MARGIN_BOTTOM);
    }

    @Override
    public void apply(@NonNull ViewGroup.MarginLayoutParams lp, DimVal val) {
      if (val != null) lp.bottomMargin = val.px();
    }
  }

  private String tag;

  private List<ViewBuilder> children;

  private boolean nextPropOnlyInEditMode;

  private Map<String, Prop<? super V, ?>> props;
  private Map<String, Object> layoutPropMap;
  private Set<LayoutProp> layoutProps;

  private ViewBuilder parent;

  public ViewBuilder() {
    // @formatter:off
    regProps(Arrays.asList(
        new IdProp(),
        new BgColorResProp(),
        new BgColorIntProp(),
        new PaddingLeftProp(),
        new PaddingTopProp(),
        new PaddingRightProp(),
        new PaddingBottomProp(),
        new OnClickProp(),
        new WidthFlexMinProp(),
        new WidthFlexMaxProp(),
        new WidthFlexPctProp(),
        new HeightFlexMinProp(),
        new HeightFlexMaxProp(),
        new HeightFlexPctProp()
    ));
    (layoutProps = new HashSet<>()).addAll(Arrays.asList(
        new LayoutWidthProp(),
        new LayoutHeightProp(),
        new MarginLeftProp(),
        new MarginTopProp(),
        new MarginRightProp(),
        new MarginBottomProp()
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
      Prop<? super V, T> editModeCopy = simpleSyncPropCopy(prop);
      props.put(EDIT_MODE_TAG + prop.name, editModeCopy);
      editModeCopy.set(val);
    } else {
      prop.set(val);
    }
    //noinspection unchecked
    return (VB) this;
  }

  @NonNull
  public ViewBuilder tagged(@NonNull String tag) {
    //noinspection ConstantConditions
    return tagged(tag, true);
  }

  @Nullable
  public ViewBuilder tagged(@NonNull String tag, boolean throwIfNotFound) {
    if (tag.equals(this.tag)) {
      return this;
    }
    for (ViewBuilder child : children()) {
      ViewBuilder vb = child.tagged(tag, false);
      if (vb != null) return vb;
    }
    if (throwIfNotFound) {
      throw new RuntimeException("tagged not found.");
    }
    return null;
  }

  public VB tag(@NonNull String tag) {
    this.tag = tag;
    //noinspection unchecked
    return (VB) this;
  }

  public <T> VB layout(@NonNull String layoutPropName, T val) {
    if (layoutPropMap == null) layoutPropMap = new HashMap<>();
    if (nextPropOnlyInEditMode) {
      nextPropOnlyInEditMode = false;
      layoutPropMap.put(EDIT_MODE_TAG + layoutPropName, val);
    } else {
      layoutPropMap.put(layoutPropName, val);
    }
    //noinspection unchecked
    return (VB) this;
  }

  public <VV extends View> VB childXml(@LayoutRes int layoutResId) {
    return child(ViewBuilder.<VV>fromXml(layoutResId));
  }

  public VB children(ViewBuilder... children) {
    for (ViewBuilder child : children) {
      child(child);
    }
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
    setProp(PaddingLeftProp.NAME, new DimVal(l, COMPLEX_UNIT_PX));
    setProp(PaddingTopProp.NAME, new DimVal(t, COMPLEX_UNIT_PX));
    setProp(PaddingRightProp.NAME, new DimVal(r, COMPLEX_UNIT_PX));
    setProp(PaddingBottomProp.NAME, new DimVal(b, COMPLEX_UNIT_PX));
    //noinspection unchecked
    return (VB) this;
  }

  public VB paddingDp(int l, int t, int r, int b) {
    setProp(PaddingLeftProp.NAME, new DimVal(l, COMPLEX_UNIT_DIP));
    setProp(PaddingTopProp.NAME, new DimVal(t, COMPLEX_UNIT_DIP));
    setProp(PaddingRightProp.NAME, new DimVal(r, COMPLEX_UNIT_DIP));
    setProp(PaddingBottomProp.NAME, new DimVal(b, COMPLEX_UNIT_DIP));
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
    setProp(PaddingTopProp.NAME, new DimVal(all, COMPLEX_UNIT_PX));
    setProp(PaddingBottomProp.NAME, new DimVal(all, COMPLEX_UNIT_PX));
    //noinspection unchecked
    return (VB) this;
  }

  public VB vPaddingDp(int all) {
    setProp(PaddingTopProp.NAME, new DimVal(all, COMPLEX_UNIT_DIP));
    setProp(PaddingBottomProp.NAME, new DimVal(all, COMPLEX_UNIT_DIP));
    //noinspection unchecked
    return (VB) this;
  }

  public VB hPadding(int all) {
    setProp(PaddingLeftProp.NAME, new DimVal(all, COMPLEX_UNIT_PX));
    setProp(PaddingRightProp.NAME, new DimVal(all, COMPLEX_UNIT_PX));
    //noinspection unchecked
    return (VB) this;
  }

  public VB hPaddingDp(int all) {
    setProp(PaddingLeftProp.NAME, new DimVal(all, COMPLEX_UNIT_DIP));
    setProp(PaddingRightProp.NAME, new DimVal(all, COMPLEX_UNIT_DIP));
    //noinspection unchecked
    return (VB) this;
  }

  public VB leftPadding(int l) {
    setProp(PaddingLeftProp.NAME, new DimVal(l, COMPLEX_UNIT_PX));
    //noinspection unchecked
    return (VB) this;
  }

  public VB leftPaddingDp(int l) {
    setProp(PaddingLeftProp.NAME, new DimVal(l, COMPLEX_UNIT_DIP));
    //noinspection unchecked
    return (VB) this;
  }

  public VB topPadding(int t) {
    setProp(PaddingTopProp.NAME, new DimVal(t, COMPLEX_UNIT_PX));
    //noinspection unchecked
    return (VB) this;
  }

  public VB topPaddingDp(int t) {
    setProp(PaddingTopProp.NAME, new DimVal(t, COMPLEX_UNIT_DIP));
    //noinspection unchecked
    return (VB) this;
  }

  public VB rightPadding(int r) {
    setProp(PaddingRightProp.NAME, new DimVal(r, COMPLEX_UNIT_PX));
    //noinspection unchecked
    return (VB) this;
  }

  public VB rightPaddingDp(int r) {
    setProp(PaddingRightProp.NAME, new DimVal(r, COMPLEX_UNIT_DIP));
    //noinspection unchecked
    return (VB) this;
  }

  public VB bottomPadding(int b) {
    setProp(PaddingBottomProp.NAME, new DimVal(b, COMPLEX_UNIT_PX));
    //noinspection unchecked
    return (VB) this;
  }

  public VB bottomPaddingDp(int b) {
    setProp(PaddingBottomProp.NAME, new DimVal(b, COMPLEX_UNIT_DIP));
    //noinspection unchecked
    return (VB) this;
  }

  public VB onClick(View.OnClickListener onClick) {
    setProp(OnClickProp.NAME, onClick);
    //noinspection unchecked
    return (VB) this;
  }

  public VB widthFlexMin(int f) {
    setProp(WidthFlexMinProp.NAME, new DimVal(f, COMPLEX_UNIT_PX));
    widthOneIfNotSet();
    //noinspection unchecked
    return (VB) this;
  }

  public VB widthFlexMinDp(int f) {
    setProp(WidthFlexMinProp.NAME, new DimVal(f, COMPLEX_UNIT_DIP));
    widthOneIfNotSet();
    //noinspection unchecked
    return (VB) this;
  }

  public VB widthFlexMax(int f) {
    setProp(WidthFlexMaxProp.NAME, new DimVal(f, COMPLEX_UNIT_PX));
    widthOneIfNotSet();
    //noinspection unchecked
    return (VB) this;
  }

  public VB widthFlexMaxDp(int f) {
    setProp(WidthFlexMaxProp.NAME, new DimVal(f, COMPLEX_UNIT_DIP));
    widthOneIfNotSet();
    //noinspection unchecked
    return (VB) this;
  }

  public VB widthFlexPct(float f) {
    setProp(WidthFlexPctProp.NAME, f);
    widthOneIfNotSet();
    //noinspection unchecked
    return (VB) this;
  }

  public VB heightFlexMin(int f) {
    setProp(HeightFlexMinProp.NAME, new DimVal(f, COMPLEX_UNIT_PX));
    heightOneIfNotSet();
    //noinspection unchecked
    return (VB) this;
  }

  public VB heightFlexMinDp(int f) {
    setProp(HeightFlexMinProp.NAME, new DimVal(f, COMPLEX_UNIT_DIP));
    heightOneIfNotSet();
    //noinspection unchecked
    return (VB) this;
  }

  public VB heightFlexMax(int f) {
    setProp(HeightFlexMaxProp.NAME, new DimVal(f, COMPLEX_UNIT_PX));
    heightOneIfNotSet();
    //noinspection unchecked
    return (VB) this;
  }

  public VB heightFlexMaxDp(int f) {
    setProp(HeightFlexMaxProp.NAME, new DimVal(f, COMPLEX_UNIT_DIP));
    heightOneIfNotSet();
    //noinspection unchecked
    return (VB) this;
  }

  public VB heightFlexPct(float f) {
    setProp(HeightFlexPctProp.NAME, f);
    heightOneIfNotSet();
    //noinspection unchecked
    return (VB) this;
  }

  private void widthOneIfNotSet() {
    if (!layoutPropMap.keySet().contains(LayoutParams.WIDTH)) {
      width(1);
    }
  }

  private void heightOneIfNotSet() {
    if (!layoutPropMap.keySet().contains(LayoutParams.HEIGHT)) {
      height(1);
    }
  }

  // -----------
  // LAYOUT PARAMS

  public VB width(int w) {
    return layout(LayoutParams.WIDTH, new DimVal(w, COMPLEX_UNIT_PX));
  }

  public VB widthDp(int w) {
    return layout(LayoutParams.WIDTH, new DimVal(w, COMPLEX_UNIT_DIP));
  }

  public VB height(int h) {
    return layout(LayoutParams.HEIGHT, new DimVal(h, COMPLEX_UNIT_PX));
  }

  public VB heightDp(int h) {
    return layout(LayoutParams.HEIGHT, new DimVal(h, COMPLEX_UNIT_DIP));
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
    return layout(LayoutParams.MARGIN_LEFT, new DimVal(l, COMPLEX_UNIT_PX));
  }

  public VB leftMarginDp(int l) {
    return layout(LayoutParams.MARGIN_LEFT, new DimVal(l, COMPLEX_UNIT_DIP));
  }

  public VB topMargin(int t) {
    return layout(LayoutParams.MARGIN_TOP, new DimVal(t, COMPLEX_UNIT_PX));
  }

  public VB topMarginDp(int t) {
    return layout(LayoutParams.MARGIN_TOP, new DimVal(t, COMPLEX_UNIT_DIP));
  }

  public VB rightMargin(int r) {
    return layout(LayoutParams.MARGIN_RIGHT, new DimVal(r, COMPLEX_UNIT_PX));
  }

  public VB rightMarginDp(int r) {
    return layout(LayoutParams.MARGIN_RIGHT, new DimVal(r, COMPLEX_UNIT_DIP));
  }

  public VB bottomMargin(int b) {
    return layout(LayoutParams.MARGIN_BOTTOM, new DimVal(b, COMPLEX_UNIT_PX));
  }

  public VB bottomMarginDp(int b) {
    return layout(LayoutParams.MARGIN_BOTTOM, new DimVal(b, COMPLEX_UNIT_DIP));
  }

  public VB margins(int l, int t, int r, int b) {
    leftMargin(l);
    topMargin(t);
    rightMargin(r);
    return bottomMargin(b);
  }

  public VB marginsDp(int l, int t, int r, int b) {
    leftMarginDp(l);
    topMarginDp(t);
    rightMarginDp(r);
    return bottomMarginDp(b);
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

    boolean isViewGroup = (v instanceof ViewGroup);

    if (!isViewGroup && //
        (children != null && !children.isEmpty())) {
      throw new IllegalArgumentException("non-ViewGroup may not have children.");
    }

    if (isViewGroup) {
      buildChildren((ViewGroup) v);
    }

    applyProps(v);

    applyLayoutProps(v);

    root.addView(v);

    applyFlex(v);
  }

  public void applyOnto(V v) {
    boolean isViewGroup = (v instanceof ViewGroup);

    if (!isViewGroup && //
        (children != null && !children.isEmpty())) {
      throw new IllegalArgumentException("non-ViewGroup may not have children.");
    }

    if (isViewGroup) {
      buildChildren((ViewGroup) v);
    }

    applyProps(v);

    applyLayoutProps(v);

    applyFlex(v);
  }

  private void buildChildren(ViewGroup vg) {
    for (ViewBuilder child : children()) {
      child.buildInto(vg);
    }
  }

  private void applyFlex(@NonNull V v) {
    if (props == null) return;

    int widthFlexMin = Integer.MIN_VALUE,
        widthFlexMax = Integer.MIN_VALUE,
        heightFlexMin = Integer.MIN_VALUE,
        heightFlexMax = Integer.MIN_VALUE;
    float widthFlexPct = Integer.MIN_VALUE,
        heightFlexPct = Integer.MIN_VALUE;

    for (Prop<? super V, ?> p : props.values()) {
      if (p instanceof WidthFlexMinProp) {
        DimVal val = ((WidthFlexMinProp) p).get();
        if (val != null) widthFlexMin = val.px();
      } else if (p instanceof WidthFlexMaxProp) {
        DimVal val = ((WidthFlexMaxProp) p).get();
        if (val != null) widthFlexMax = val.px();
      } else if (p instanceof WidthFlexPctProp) {
        Float val = ((WidthFlexPctProp) p).get();
        if (val != null) widthFlexPct = val;
      } else if (p instanceof HeightFlexMinProp) {
        DimVal val = ((HeightFlexMinProp) p).get();
        if (val != null) heightFlexMin = val.px();
      } else if (p instanceof HeightFlexMaxProp) {
        DimVal val = ((HeightFlexMaxProp) p).get();
        if (val != null) heightFlexMax = val.px();
      } else if (p instanceof HeightFlexPctProp) {
        Float val = ((HeightFlexPctProp) p).get();
        if (val != null) heightFlexPct = val;
      }
    }

    boolean widthFlex = widthFlexMin != Integer.MIN_VALUE ||
        widthFlexMax != Integer.MIN_VALUE ||
        widthFlexPct != Integer.MIN_VALUE;

    boolean heightFlex = heightFlexMin != Integer.MIN_VALUE ||
        heightFlexMax != Integer.MIN_VALUE ||
        heightFlexPct != Integer.MIN_VALUE;

    if (widthFlex && heightFlex) {
      throw reqArg("width flex XOR height flex");
    }

    if (widthFlex || heightFlex) {
      final ViewTreeObserver vto = v.getViewTreeObserver();
      if (!vto.isAlive()) {
        throw reqArg("alive ViewTreeObserver");
      }
      if (widthFlex) {
        vto.addOnGlobalLayoutListener(new SameDimGlobalLayoutListener(vto, v, //
            true, widthFlexMin, widthFlexMax, widthFlexPct));
      }
      if (heightFlex) {
        vto.addOnGlobalLayoutListener(new SameDimGlobalLayoutListener(vto, v, //
            false, heightFlexMin, heightFlexMax, heightFlexPct));
      }
    }
  }

  @SuppressWarnings("deprecation")
  private void applyProps(@NonNull V v) {
    if (props != null) {

      if (v.isInEditMode()) {
        Set<String> toRemove = new HashSet<>();
        for (Map.Entry<String, Prop<? super V, ?>> p : props.entrySet()) {
          if (p.getKey().startsWith(EDIT_MODE_TAG)) {
            p.getValue().apply(v);
            toRemove.add(p.getKey());
            toRemove.add(p.getKey().replace(EDIT_MODE_TAG, ""));
          }
        }
        for (String keyToRemove : toRemove) {
          props.remove(keyToRemove);
        }
      } else {
        for (Iterator<Map.Entry<String, Prop<? super V, ?>>> i = props.entrySet().iterator();
            i.hasNext(); ) {
          if (i.next().getKey().startsWith(EDIT_MODE_TAG)) {
            i.remove();
          }
        }
      }

      for (Prop<? super V, ?> p : props.values()) {
        p.apply(v);
      }
    }
  }

  private void applyLayoutProps(@NonNull V v) {
    ViewGroup.LayoutParams plps;
    if (parent == null) {
      plps = v.getLayoutParams();
      if (plps == null) {
        plps = new ViewGroup.LayoutParams(LAYOUT_DIM_EMPTY, LAYOUT_DIM_EMPTY);
      }
    } else {
      plps = parent.createEmptyLayoutParamsForChild();
      plps.width = plps.height = LAYOUT_DIM_EMPTY;
    }

    if (layoutPropMap != null && layoutProps != null) {

      if (v.isInEditMode()) {
        Set<String> toRemove = new HashSet<>();
        for (Map.Entry<String, Object> e : layoutPropMap.entrySet()) {
          if (e.getKey().startsWith(EDIT_MODE_TAG)) {
            for (LayoutProp lp : layoutProps) {
              if (lp.name.equals(e.getKey().replace(EDIT_MODE_TAG, ""))) {
                //noinspection unchecked
                lp.apply(plps, e.getValue());
                break;
              }
            }
            toRemove.add(e.getKey());
            toRemove.add(e.getKey().replace(EDIT_MODE_TAG, ""));
          }
        }
        for (String keyToRemove : toRemove) {
          layoutPropMap.remove(keyToRemove);
        }
      } else {
        for (Iterator<Map.Entry<String, Object>> i = layoutPropMap.entrySet().iterator();
            i.hasNext(); ) {
          if (i.next().getKey().startsWith(EDIT_MODE_TAG)) {
            i.remove();
          }
        }
      }

      for (Map.Entry<String, Object> e : layoutPropMap.entrySet()) {
        for (LayoutProp lp : layoutProps) {
          if (lp.name.equals(e.getKey().replace(EDIT_MODE_TAG, ""))) {
            //noinspection unchecked
            lp.apply(plps, e.getValue());
            break;
          }
        }
      }
    }

    if (plps.width == LAYOUT_DIM_EMPTY || plps.height == LAYOUT_DIM_EMPTY) {
      if (v.getLayoutParams() == null) {
        throw reqArg("layout width & height");
      } else {
        plps = v.getLayoutParams();
      }
    }

    v.setLayoutParams(plps);
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

  private static final class DimVal {
    final int dim;
    final int type;

    private DimVal(int dim, int type) {
      this.dim = dim;
      this.type = type;
    }

    int px() {
      return dimToPx(dim, type);
    }
  }

  private static final class SameDimGlobalLayoutListener
      implements ViewTreeObserver.OnGlobalLayoutListener {

    private final WeakReference<ViewTreeObserver> vtoRef;
    private final WeakReference<View> vRef;
    private final boolean widthFlex;
    private final int min;
    private final int max;
    private final float pct;

    SameDimGlobalLayoutListener(ViewTreeObserver vto, View v, //
        boolean widthFlex, int min, int max, float pct) {
      vtoRef = new WeakReference<>(vto);
      vRef = new WeakReference<>(v);
      this.widthFlex = widthFlex;
      if (pct < 0) pct = 1;
      if (min < 0) min = 0;
      if (max < 0) max = 0;
      this.min = min;
      this.max = max;
      this.pct = pct;
    }

    private void cleanup() {
      try {
        vRef.get().getViewTreeObserver().removeOnGlobalLayoutListener(this);
      } catch (Exception ignored) {
      }
      try {
        vtoRef.get().removeOnGlobalLayoutListener(this);
      } catch (Exception ignored) {
      }
    }

    @Nullable
    private View aliveView() {
      View v = vRef.get();
      if (v == null) return null;
      ViewTreeObserver vto = v.getViewTreeObserver();
      if (vto != null && vto.isAlive()) return v;
      return null;
    }

    @Override
    public void onGlobalLayout() {
      View v = aliveView();
      if (v == null) {
        cleanup();
        return;
      }

      ViewGroup.LayoutParams lp = v.getLayoutParams();
      if (lp != null) {
        boolean heightFlex = !widthFlex;
        if (widthFlex && v.getHeight() != 0) {
          int w = Math.round(v.getHeight() * pct);
          if (min != 0) w = Math.max(w, min);
          if (max != 0) w = Math.min(w, max);
          lp.width = w;
        } else if (heightFlex && v.getWidth() != 0) {
          int h = Math.round(v.getWidth() * pct);
          if (min != 0) h = Math.max(h, min);
          if (max != 0) h = Math.min(h, max);
          lp.height = h;
        }
      }

      v.requestLayout();
    }
  }
}
