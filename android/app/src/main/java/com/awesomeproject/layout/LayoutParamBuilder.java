package com.awesomeproject.layout;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public abstract class LayoutParamBuilder< //
    LPB extends LayoutParamBuilder, LP extends ViewGroup.LayoutParams> {

  protected static final int EMPTY = Integer.MIN_VALUE;

  private LP lps;

  @NonNull
  protected LP lps() {
    if (lps == null) {
      lps = createEmptyLayoutParams();
      lps.width = EMPTY;
      lps.height = EMPTY;
    }
    return lps;
  }

  @NonNull
  protected abstract LP createEmptyLayoutParams();

  public LPB width(int w) {
    lps().width = w;
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB height(int h) {
    lps().height = h;
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB dims(int w, int h) {
    lps().width = w;
    lps().height = h;
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB dims(int wh) {
    lps().width = wh;
    lps().height = wh;
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB wrapContent() {
    lps().width = WRAP_CONTENT;
    lps().height = WRAP_CONTENT;
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB matchParent() {
    lps().width = MATCH_PARENT;
    lps().height = MATCH_PARENT;
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB matchWidth() {
    lps().width = MATCH_PARENT;
    lps().height = WRAP_CONTENT;
    //noinspection unchecked
    return (LPB) this;
  }

  public LPB matchHeight() {
    lps().width = WRAP_CONTENT;
    lps().height = MATCH_PARENT;
    //noinspection unchecked
    return (LPB) this;
  }

  @NonNull
  public LP build() {
    if (lps().width == EMPTY || lps().height == EMPTY) {
      throw new IllegalArgumentException("Requires width and height.");
    }
    return lps();
  }
}
