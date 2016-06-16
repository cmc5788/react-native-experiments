package com.awesomeproject.layout;

public final class LinearLayouts {

  public interface LinearLayoutProps<VB extends ViewBuilder> {
    VB orientation(int orientation);

    VB gravity(int gravity);
  }

  public static LinearLayoutBuilder build() {
    return new LinearLayoutBuilder();
  }

  public static SpaceBuilder vSpace(float weight) {
    return new SpaceBuilder().layoutParams(
        LinearLayouts.params().height(0).width(1).weight(weight));
  }

  public static SpaceBuilder hSpace(float weight) {
    return new SpaceBuilder().layoutParams(
        LinearLayouts.params().height(1).width(0).weight(weight));
  }

  public static LinearLayoutBuilder.LinearLayoutParamBuilder params() {
    return new LinearLayoutBuilder.LinearLayoutParamBuilder();
  }
}
