package com.awesomeproject.layout;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;

public class ImageViews {

  public interface ImageViewProps<VB extends ViewBuilder> {
    VB image(@DrawableRes int image);

    VB image(Drawable image);

    VB image(Bitmap image);
  }

  public static ImageViewBuilder build() {
    return new ImageViewBuilder();
  }

  protected ImageViews() {
    throw new UnsupportedOperationException();
  }
}
