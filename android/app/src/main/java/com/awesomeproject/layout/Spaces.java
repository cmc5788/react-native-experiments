package com.awesomeproject.layout;

public final class Spaces {

  public static SpaceBuilder builder() {
    return new SpaceBuilder();
  }

  public static SpaceBuilder buildVertSpace(float weight) {
    return new SpaceBuilder().layoutParams(
        LinearLayouts.params().height(0).width(1).weight(weight));
  }

  public static SpaceBuilder buildHorizSpace(float weight) {
    return new SpaceBuilder().layoutParams(
        LinearLayouts.params().height(1).width(0).weight(weight));
  }
}
