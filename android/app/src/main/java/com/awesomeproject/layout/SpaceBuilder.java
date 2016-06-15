package com.awesomeproject.layout;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.Space;

public class SpaceBuilder extends ViewBuilder<SpaceBuilder, Space> {

  @NonNull
  @Override
  protected Space createView(ViewGroup root) {
    return new Space(root.getContext());
  }
}
