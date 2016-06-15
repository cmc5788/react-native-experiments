package com.awesomeproject.layout;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TextViewBuilder extends ViewBuilder<TextViewBuilder, TextView> {

  private CharSequence text;
  @StringRes private int textResId = View.NO_ID;

  public TextViewBuilder text(CharSequence text) {
    this.text = text;
    return this;
  }

  public TextViewBuilder text(@StringRes int textResId) {
    this.textResId = textResId;
    return this;
  }

  @NonNull
  @Override
  protected TextView createView(ViewGroup root) {
    TextView tv = new TextView(root.getContext());
    if (text != null) {
      tv.setText(text);
    } else if (textResId != View.NO_ID) {
      tv.setText(textResId);
    }
    return tv;
  }
}
