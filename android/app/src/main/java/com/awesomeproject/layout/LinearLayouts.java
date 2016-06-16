package com.awesomeproject.layout;

public final class LinearLayouts {

  public interface LinearLayoutProps<VB extends ViewBuilder> {
    VB orientation(int orientation);

    VB gravity(int gravity);
  }

  public static LinearLayoutBuilder build() {
    return new LinearLayoutBuilder();
  }

  public static LinearLayoutBuilder.LinearLayoutParamBuilder params() {
    return new LinearLayoutBuilder.LinearLayoutParamBuilder();
  }
}
