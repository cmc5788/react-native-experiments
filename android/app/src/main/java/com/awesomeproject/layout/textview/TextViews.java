package com.awesomeproject.layout.textview;

import android.support.annotation.StringRes;
import com.awesomeproject.layout.ViewBuilder;

public class TextViews {

  public interface TextViewProps<VB extends ViewBuilder> {
    VB text(@StringRes int text);

    VB text(CharSequence textCharSeq);

    VB gravity(int gravity);
  }

  public static TextViewBuilder build() {
    return new TextViewBuilder();
  }

  protected TextViews() {
    throw new UnsupportedOperationException();
  }
}
