package com.awesomeproject.layout;

public final class ScrollViews {

  public static ScrollViewBuilder build() {
    return new ScrollViewBuilder();
  }

  public static ScrollViewBuilder.ScrollViewParamBuilder params() {
    return new ScrollViewBuilder.ScrollViewParamBuilder();
  }
}
