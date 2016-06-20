package com.awesomeproject.layout;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatTextView;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Arrays;

public class TextViewBuilder extends ViewBuilder<TextViewBuilder, TextView>
    implements TextViews.TextViewProps<TextViewBuilder> {

  public static class TextResProp extends Prop<TextView, Integer> {
    private static final String NAME = "TEXT_RES";

    public TextResProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull TextView tv) {
      Integer val = get();
      if (val != null) tv.setText(val);
    }
  }

  public static class TextCharSequenceProp extends Prop<TextView, CharSequence> {
    private static final String NAME = "TEXT_CHARSEQUENCE";

    public TextCharSequenceProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull TextView tv) {
      CharSequence val = get();
      if (val != null) tv.setText(val);
    }
  }

  public static class TextGravityProp extends Prop<TextView, Integer> {
    private static final String NAME = "TEXT_GRAVITY";

    public TextGravityProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull TextView tv) {
      Integer val = get();
      if (val != null) tv.setGravity(val);
    }
  }

  public TextViewBuilder() {
    // @formatter:off
    regProps(Arrays.asList(
        new TextResProp(),
        new TextCharSequenceProp(),
        new TextGravityProp()
    ));
    // @formatter:on
  }

  @Override
  public TextViewBuilder text(@StringRes int text) {
    setProp(TextResProp.NAME, text);
    return this;
  }

  @Override
  public TextViewBuilder text(CharSequence text) {
    setProp(TextCharSequenceProp.NAME, text);
    return this;
  }

  @Override
  public TextViewBuilder gravity(int gravity) {
    setProp(TextGravityProp.NAME, gravity);
    return this;
  }

  @NonNull
  @Override
  protected TextView createView(ViewGroup root) {
    return new AppCompatTextView(root.getContext());
  }
}
