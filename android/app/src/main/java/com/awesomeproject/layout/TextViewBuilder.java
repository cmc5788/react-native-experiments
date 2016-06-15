package com.awesomeproject.layout;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TextViewBuilder extends ViewBuilder<TextViewBuilder, TextView> {

  private CharSequence textCharSeq;
  @StringRes private int text = View.NO_ID;

  public TextViewBuilder text(@StringRes int text) {
    this.text = text;
    return this;
  }

  public TextViewBuilder text(CharSequence textCharSeq) {
    this.textCharSeq = textCharSeq;
    return this;
  }

  @NonNull
  @Override
  protected TextView createView(ViewGroup root) {
    TextView tv = new TextView(root.getContext());
    if (text != View.NO_ID) {
      tv.setText(text);
    } else if (textCharSeq != null) {
      tv.setText(textCharSeq);
    }
    return tv;
  }
}
