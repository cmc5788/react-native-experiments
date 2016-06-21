package com.awesomeproject.layout;

public class ScrollViews {

  public interface ScrollViewProps<VB extends ViewBuilder> {
    VB fillViewport(boolean fillViewport);
  }

  public static ScrollViewBuilder build() {
    return new ScrollViewBuilder();
  }

  public static ScrollViewInnerLinearBuilder buildWithInnerLinear() {
    return new ScrollViewInnerLinearBuilder();
  }

  protected ScrollViews() {
    throw new UnsupportedOperationException();
  }
}
