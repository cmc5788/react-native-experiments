package com.awesomeproject.layout;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import java.util.Arrays;
import java.util.Set;

public class RelativeLayoutBuilder extends ViewBuilder<RelativeLayoutBuilder, RelativeLayout> {

  public static class AlignParentProp extends LayoutProp<RelativeLayout.LayoutParams, Object> {

    private final int rule;

    public AlignParentProp(@NonNull String name, int rule) {
      super(name);
      this.rule = rule;
    }

    @Override
    public void apply(@NonNull RelativeLayout.LayoutParams lp, Object val) {
      if (val instanceof Boolean) {
        lp.addRule(rule, (Boolean) val
            ? RelativeLayout.TRUE
            : 0);
      } else if (val instanceof Integer) {
        lp.addRule(rule, (Integer) val);
      }
    }
  }

  @NonNull
  @Override
  protected RelativeLayout createView(ViewGroup root) {
    return new RelativeLayout(root.getContext());
  }

  @NonNull
  @Override
  protected ViewGroup.LayoutParams createEmptyLayoutParamsForChild() {
    return new RelativeLayout.LayoutParams(0, 0);
  }

  @Override
  protected void provideLayoutPropsToChild(@NonNull Set<LayoutProp> layoutProps) {
    // @formatter:off
    layoutProps.addAll(Arrays.asList(
        new AlignParentProp(LayoutParams.CENTER_IN_PARENT, RelativeLayout.CENTER_IN_PARENT),
        new AlignParentProp(LayoutParams.CENTER_HORIZONTAL, RelativeLayout.CENTER_HORIZONTAL),
        new AlignParentProp(LayoutParams.CENTER_VERTICAL, RelativeLayout.CENTER_VERTICAL),
        new AlignParentProp(LayoutParams.ALIGN_PARENT_LEFT, RelativeLayout.ALIGN_PARENT_LEFT),
        new AlignParentProp(LayoutParams.ALIGN_PARENT_TOP, RelativeLayout.ALIGN_PARENT_TOP),
        new AlignParentProp(LayoutParams.ALIGN_PARENT_RIGHT, RelativeLayout.ALIGN_PARENT_RIGHT),
        new AlignParentProp(LayoutParams.ALIGN_PARENT_BOTTOM, RelativeLayout.ALIGN_PARENT_BOTTOM),
        new AlignParentProp(LayoutParams.ALIGN_BASELINE, RelativeLayout.ALIGN_BASELINE),
        new AlignParentProp(LayoutParams.ALIGN_LEFT, RelativeLayout.ALIGN_LEFT),
        new AlignParentProp(LayoutParams.ALIGN_TOP, RelativeLayout.ALIGN_TOP),
        new AlignParentProp(LayoutParams.ALIGN_RIGHT, RelativeLayout.ALIGN_RIGHT),
        new AlignParentProp(LayoutParams.ALIGN_BOTTOM, RelativeLayout.ALIGN_BOTTOM),
        new AlignParentProp(LayoutParams.LEFT_OF, RelativeLayout.LEFT_OF),
        new AlignParentProp(LayoutParams.ABOVE, RelativeLayout.ABOVE),
        new AlignParentProp(LayoutParams.RIGHT_OF, RelativeLayout.RIGHT_OF),
        new AlignParentProp(LayoutParams.BELOW, RelativeLayout.BELOW)
    ));
    // @formatter:on
    super.provideLayoutPropsToChild(layoutProps);
  }
}
