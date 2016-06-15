package com.awesomeproject.layout;

public final class FrameLayouts {

  public static FrameLayoutBuilder build() {
    return new FrameLayoutBuilder();
  }

  public static FrameLayoutBuilder.FrameLayoutParamBuilder params() {
    return new FrameLayoutBuilder.FrameLayoutParamBuilder();
  }
}
