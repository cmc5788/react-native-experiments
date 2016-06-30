package com.awesomeproject.layout.imageview;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.awesomeproject.layout.ViewBuilder;
import java.util.Arrays;

public class ImageViewBuilder extends ViewBuilder<ImageViewBuilder, ImageView>
    implements ImageViews.ImageViewProps<ImageViewBuilder> {

  private static class ImageResProp extends Prop<ImageView, Integer> {
    private static final String NAME = "IMAGE_RES";

    public ImageResProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull ImageView iv) {
      Integer val = get();
      if (val != null) iv.setImageResource(val);
    }
  }

  private static class ImageDrawableProp extends Prop<ImageView, Drawable> {
    private static final String NAME = "IMAGE_DRAWABLE";

    public ImageDrawableProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull ImageView iv) {
      Drawable val = get();
      if (val != null) iv.setImageDrawable(val);
    }
  }

  private static class ImageBitmapProp extends Prop<ImageView, Bitmap> {
    private static final String NAME = "IMAGE_BITMAP";

    public ImageBitmapProp() {
      super(NAME);
    }

    @Override
    public void apply(@NonNull ImageView iv) {
      Bitmap val = get();
      if (val != null) iv.setImageBitmap(val);
    }
  }

  public ImageViewBuilder() {
    // @formatter:off
    regProps(Arrays.asList(
        new ImageResProp(),
        new ImageDrawableProp(),
        new ImageBitmapProp()
    ));
    // @formatter:on
  }

  @Override
  public ImageViewBuilder image(@DrawableRes int image) {
    setProp(ImageResProp.NAME, image);
    return this;
  }

  @Override
  public ImageViewBuilder image(Drawable image) {
    setProp(ImageDrawableProp.NAME, image);
    return this;
  }

  @Override
  public ImageViewBuilder image(Bitmap image) {
    setProp(ImageBitmapProp.NAME, image);
    return this;
  }

  @NonNull
  @Override
  protected ImageView createView(ViewGroup root) {
    return new AppCompatImageView(root.getContext());
  }
}
