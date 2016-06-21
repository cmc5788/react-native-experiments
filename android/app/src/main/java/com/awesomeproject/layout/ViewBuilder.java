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
      Integer val = get();
      if (val != null) set(dimToPx(val, type));
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
      Integer val = get();
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
      Integer val = get();
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
      Integer val = get();
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
      Integer val = get();
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
      Integer val = get();
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
      Integer val = get();
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
      Integer val = get();
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
      View.OnClickListener val = get();
      if (val != null) v.setOnClickListener(val);
    }
  }

  public static class WidthFlexMinProp extends DimProp<View> {
    private static final String NAME = "WIDTH_FLEX_MIN";

    public WidthFlexMinProp(int type) {
      super(NAME, type);
    }
  }

  public static class WidthFlexMaxProp extends DimProp<View> {
    private static final String NAME = "WIDTH_FLEX_MAX";

    public WidthFlexMaxProp(int type) {
      super(NAME, type);
    }
  }

  public static class WidthFlexPctProp extends Prop<View, Float> {
    private static final String NAME = "WIDTH_FLEX_PCT";

    public WidthFlexPctProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull View view) {
      // no-op
    }
  }

  public static class HeightFlexMinProp extends DimProp<View> {
    private static final String NAME = "HEIGHT_FLEX_MIN";

    public HeightFlexMinProp(int type) {
      super(NAME, type);
    }
  }

  public static class HeightFlexMaxProp extends DimProp<View> {
    private static final String NAME = "HEIGHT_FLEX_MAX";

    public HeightFlexMaxProp(int type) {
      super(NAME, type);
    }
  }

  public static class HeightFlexPctProp extends Prop<View, Float> {
    private static final String NAME = "HEIGHT_FLEX_PCT";

    public HeightFlexPctProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull View view) {
      // no-op
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
        new PaddingLeftProp(COMPLEX_UNIT_PX),
        new PaddingLeftProp(COMPLEX_UNIT_DIP),
        new PaddingTopProp(COMPLEX_UNIT_PX),
        new PaddingTopProp(COMPLEX_UNIT_DIP),
        new PaddingRightProp(COMPLEX_UNIT_PX),
        new PaddingRightProp(COMPLEX_UNIT_DIP),
        new PaddingBottomProp(COMPLEX_UNIT_PX),
        new PaddingBottomProp(COMPLEX_UNIT_DIP),
        new OnClickProp(),
        new WidthFlexMinProp(COMPLEX_UNIT_PX),
        new WidthFlexMinProp(COMPLEX_UNIT_DIP),
        new WidthFlexMaxProp(COMPLEX_UNIT_PX),
        new WidthFlexMaxProp(COMPLEX_UNIT_DIP),
        new WidthFlexPctProp(),
        new HeightFlexMinProp(COMPLEX_UNIT_PX),
        new HeightFlexMinProp(COMPLEX_UNIT_DIP),
        new HeightFlexMaxProp(COMPLEX_UNIT_PX),
        new HeightFlexMaxProp(COMPLEX_UNIT_DIP),
        new HeightFlexPctProp()
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

  public VB widthFlexMin(int f) {
    setProp(DimProp.makeName(WidthFlexMinProp.NAME, COMPLEX_UNIT_PX), f);
    //noinspection unchecked
    return (VB) this;
  }

  public VB widthFlexMinDp(int f) {
    setProp(DimProp.makeName(WidthFlexMinProp.NAME, COMPLEX_UNIT_DIP), f);
    //noinspection unchecked
    return (VB) this;
  }

  public VB widthFlexMax(int f) {
    setProp(DimProp.makeName(WidthFlexMaxProp.NAME, COMPLEX_UNIT_PX), f);
    //noinspection unchecked
    return (VB) this;
  }

  public VB widthFlexMaxDp(int f) {
    setProp(DimProp.makeName(WidthFlexMaxProp.NAME, COMPLEX_UNIT_DIP), f);
    //noinspection unchecked
    return (VB) this;
  }

  public VB widthFlexPct(float f) {
    setProp(WidthFlexPctProp.NAME, f);
    //noinspection unchecked
    return (VB) this;
  }

  public VB heightFlexMin(int f) {
    setProp(DimProp.makeName(HeightFlexMinProp.NAME, COMPLEX_UNIT_PX), f);
    //noinspection unchecked
    return (VB) this;
  }

  public VB heightFlexMinDp(int f) {
    setProp(DimProp.makeName(HeightFlexMinProp.NAME, COMPLEX_UNIT_DIP), f);
    //noinspection unchecked
    return (VB) this;
  }

  public VB heightFlexMax(int f) {
    setProp(DimProp.makeName(HeightFlexMaxProp.NAME, COMPLEX_UNIT_PX), f);
    //noinspection unchecked
    return (VB) this;
  }

  public VB heightFlexMaxDp(int f) {
    setProp(DimProp.makeName(HeightFlexMaxProp.NAME, COMPLEX_UNIT_DIP), f);
    //noinspection unchecked
    return (VB) this;
  }

  public VB heightFlexPct(float f) {
    setProp(HeightFlexPctProp.NAME, f);
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
    if (!(v instanceof ViewGroup)) throw reqArg("applyOnto: ViewGroup");

    ViewGroup vg = (ViewGroup) v;

    buildChildren(vg);

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
        Integer val = ((WidthFlexMinProp) p).get();
        if (val != null) widthFlexMin = val;
      } else if (p instanceof WidthFlexMaxProp) {
        Integer val = ((WidthFlexMaxProp) p).get();
        if (val != null) widthFlexMax = val;
      } else if (p instanceof WidthFlexPctProp) {
        Float val = ((WidthFlexPctProp) p).get();
        if (val != null) widthFlexPct = val;
      } else if (p instanceof HeightFlexMinProp) {
        Integer val = ((HeightFlexMinProp) p).get();
        if (val != null) heightFlexMin = val;
      } else if (p instanceof HeightFlexMaxProp) {
        Integer val = ((HeightFlexMaxProp) p).get();
        if (val != null) heightFlexMax = val;
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
      plps = new ViewGroup.LayoutParams(0, 0);
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
          if (lp.name.equals(e.getKey())) {
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
