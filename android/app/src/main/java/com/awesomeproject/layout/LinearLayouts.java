package com.awesomeproject.layout;

public class LinearLayouts {

  public interface LinearLayoutProps<VB extends ViewBuilder> {
    VB orientation(int orientation);

    VB gravity(int gravity);
  }

  public static LinearLayoutBuilder build() {
    return new LinearLayoutBuilder();
  }

  protected LinearLayouts() {
    throw new UnsupportedOperationException();
  }
}
