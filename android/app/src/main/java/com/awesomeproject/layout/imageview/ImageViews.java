package com.awesomeproject.layout.imageview;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import com.awesomeproject.layout.ViewBuilder;

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
