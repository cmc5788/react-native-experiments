package com.awesomeproject.layout;

import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;

public class LinearLayouts {

  public interface LinearLayoutProps<VB extends ViewBuilder> {
    VB orientation(int orientation);

    VB gravity(int gravity);
  }

  public static LinearLayoutBuilder build() {
    return new LinearLayoutBuilder();
  }

  public static LinearLayoutBuilder buildRow() {
    return new LinearLayoutBuilder().matchWidth().orientation(HORIZONTAL);
  }

  public static LinearLayoutBuilder buildCol() {
    return new LinearLayoutBuilder().matchHeight().orientation(VERTICAL);
  }

  protected LinearLayouts() {
    throw new UnsupportedOperationException();
  }
}
