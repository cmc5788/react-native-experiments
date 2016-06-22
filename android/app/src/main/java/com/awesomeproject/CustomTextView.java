package com.awesomeproject;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.ViewGroup;
import android.widget.TextView;
import com.awesomeproject.layout.textview.TextViewBuilder;
import com.awesomeproject.layout.textview.TextViews;
import com.awesomeproject.layout.ViewBuilder;
import java.util.Collections;

public class CustomTextView extends TextView {

  public static Builder build() {
    return new Builder();
  }

  private static class TextClrProp extends ViewBuilder.Prop<CustomTextView, Integer> {
    private static final String NAME = "TEXT_CLR";

    public TextClrProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull CustomTextView tv) {
      Integer val = get();
      if (val != null) tv.setTextClr(val);
    }
  }

  public static class Builder extends ViewBuilder<Builder, CustomTextView>
      implements TextViews.TextViewProps<Builder> {

    private final TextViewBuilder composedBuilder;

    public Builder() {
      composePropsFrom(composedBuilder = new TextViewBuilder());
      regProps(Collections.singleton(new TextClrProp()));
    }

    @Override
    public Builder text(@StringRes int text) {
      composedBuilder.text(text);
      return this;
    }

    @Override
    public Builder text(CharSequence text) {
      composedBuilder.text(text);
      return this;
    }

    @Override
    public Builder gravity(int gravity) {
      composedBuilder.gravity(gravity);
      return this;
    }

    public Builder textClr(@ColorInt int clr) {
      setProp(TextClrProp.NAME, clr);
      return this;
    }

    @NonNull
    @Override
    protected CustomTextView createView(ViewGroup root) {
      return new CustomTextView(root.getContext());
    }
  }

  public CustomTextView(Context context) {
    super(context);
  }

  public void setTextClr(@ColorInt int clr) {
    setTextColor(clr);
  }
}
