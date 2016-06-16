package com.awesomeproject.layout;

import android.support.annotation.StringRes;

public final class TextViews {

  public interface TextViewProps<VB extends ViewBuilder> {
    VB text(@StringRes int text);

    VB text(CharSequence textCharSeq);
  }

  public static TextViewBuilder build() {
    return new TextViewBuilder();
  }
}
