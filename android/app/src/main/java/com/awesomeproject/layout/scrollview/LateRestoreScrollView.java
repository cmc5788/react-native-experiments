package com.awesomeproject.layout.scrollview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.ScrollView;

/** ScrollView modified to restore scroll state if being restored after the first layout pass. */
public class LateRestoreScrollView extends ScrollView {

  public LateRestoreScrollView(Context context) {
    super(context);
  }

  public LateRestoreScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public LateRestoreScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public LateRestoreScrollView(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override
  protected void onRestoreInstanceState(Parcelable state) {
    SavedState ss = (SavedState) state;
    super.onRestoreInstanceState(ss.getSuperState());
    setScrollY(ss.scrollPosition);
  }

  @Override
  protected Parcelable onSaveInstanceState() {
    Parcelable superState = super.onSaveInstanceState();
    SavedState ss = new SavedState(superState);
    ss.scrollPosition = getScrollY();
    return ss;
  }

  static class SavedState extends BaseSavedState {
    public int scrollPosition;

    SavedState(Parcelable superState) {
      super(superState);
    }

    public SavedState(Parcel source) {
      super(source);
      scrollPosition = source.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeInt(scrollPosition);
    }

    @Override
    public String toString() {
      return "LateRestoreScrollView.SavedState{" + Integer.toHexString(
          System.identityHashCode(this)) + " scrollPosition=" + scrollPosition + "}";
    }

    public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
      public SavedState createFromParcel(Parcel in) {
        return new SavedState(in);
      }

      public SavedState[] newArray(int size) {
        return new SavedState[size];
      }
    };
  }
}
