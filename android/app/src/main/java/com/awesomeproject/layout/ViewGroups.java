package com.awesomeproject.layout;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

public final class ViewGroups {

  public static ViewGroupParamBuilder baseParams() {
    return new ViewGroupParamBuilder();
  }

  public static class ViewGroupParamBuilder
      extends LayoutParamBuilder<ViewGroupParamBuilder, ViewGroup.LayoutParams> {

    @NonNull
    @Override
    protected ViewGroup.LayoutParams createEmptyLayoutParams() {
      return new ViewGroup.LayoutParams(EMPTY, EMPTY);
    }
  }
}
