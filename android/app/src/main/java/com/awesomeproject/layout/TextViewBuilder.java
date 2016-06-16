package com.awesomeproject.layout;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.ViewGroup;
import android.widget.TextView;

public class TextViewBuilder extends ViewBuilder<TextViewBuilder, TextView>
    implements TextViews.TextViewProps<TextViewBuilder> {

  public static class TextResProp extends Prop<TextView, Integer> {
    public static final String NAME = "TEXT_RES";

    @NonNull
    @Override
    public String name() {
      return NAME;
    }

    @Override
    public void set(Integer val) {
      this.val = val;
    }

    @Override
    public void apply(@NonNull TextView tv) {
      if (val != null) tv.setText(val);
    }
  }

  public static class TextCharSequenceProp extends Prop<TextView, CharSequence> {
    public static final String NAME = "TEXT_CHARSEQUENCE";

    @NonNull
    @Override
    public String name() {
      return NAME;
    }

    @Override
    public void set(CharSequence val) {
      this.val = val;
    }

    @Override
    public void apply(@NonNull TextView tv) {
      if (val != null) tv.setText(val);
    }
  }

  public TextViewBuilder() {
    regProp(new TextResProp());
    regProp(new TextCharSequenceProp());
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

  @NonNull
  @Override
  protected TextView createView(ViewGroup root) {
    return new TextView(root.getContext());
  }
}
