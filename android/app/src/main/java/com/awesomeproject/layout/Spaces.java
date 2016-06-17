package com.awesomeproject.layout;

import static com.awesomeproject.layout.LayoutParams.WEIGHT;

public class Spaces {

  public static SpaceBuilder build() {
    return new SpaceBuilder();
  }

  public static SpaceBuilder vSpace(float weight) {
    return new SpaceBuilder().height(0).width(1).layout(WEIGHT, weight);
  }

  public static SpaceBuilder hSpace(float weight) {
    return new SpaceBuilder().height(1).width(0).layout(WEIGHT, weight);
  }

  protected Spaces() {
    throw new UnsupportedOperationException();
  }
}
