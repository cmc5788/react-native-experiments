package com.awesomeproject.layout;

public final class Spaces {

  public static SpaceBuilder build() {
    return new SpaceBuilder();
  }

  public static SpaceBuilder buildVSpace(float weight) {
    return new SpaceBuilder().layoutParams(
        LinearLayouts.params().height(0).width(1).weight(weight));
  }

  public static SpaceBuilder buildHSpace(float weight) {
    return new SpaceBuilder().layoutParams(
        LinearLayouts.params().height(1).width(0).weight(weight));
  }
}
