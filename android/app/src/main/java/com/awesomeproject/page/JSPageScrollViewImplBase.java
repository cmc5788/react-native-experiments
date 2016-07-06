package com.awesomeproject.page;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import com.awesomeproject.MyNavigator.NavTag;
import com.awesomeproject.layout.scrollview.LateRestoreScrollView;
import com.facebook.react.bridge.ReadableMap;

import static com.facebook.react.bridge.UiThreadUtil.assertOnUiThread;

public abstract class JSPageScrollViewImplBase<P extends JSPagePresenter> //
    extends LateRestoreScrollView implements JSPageView {

  private P presenter;

  protected JSPageScrollViewImplBase(Context context) {
    super(context);
    setId(rootId());
  }

  protected JSPageScrollViewImplBase(Context context, AttributeSet attrs) {
    super(context, attrs);
    setId(rootId());
  }

  protected JSPageScrollViewImplBase(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setId(rootId());
  }

  protected JSPageScrollViewImplBase(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    setId(rootId());
  }

  @IdRes
  protected abstract int rootId();

  public final void setPresenter(@NonNull P presenter) {
    if (this.presenter != null) {
      throw new IllegalStateException("presenter already set.");
    }
    this.presenter = presenter;
  }

  @NonNull
  protected final P presenter() {
    if (this.presenter == null) {
      throw new IllegalStateException("cannot access null presenter,");
    }
    return this.presenter;
  }

  @CallSuper
  @Override
  public void setId(int id) {
    assertOnUiThread();
    // Making sure that React isn't setting this. Have to be a bit
    // defensive since it likes to go rogue setting IDs elsewhere.
    if (id != rootId()) throw new IllegalArgumentException("not my id!");
    super.setId(id);
  }

  @NonNull
  @Override
  public final Context context() {
    return getContext();
  }

  @Override
  public final void receiveViewEvent(@NonNull String viewTag, @Nullable ReadableMap args) {
    presenter.receiveViewEvent(viewTag, args);
  }

  @Override
  public final boolean respondsToTag(@NonNull String viewTag) {
    return presenter.respondsToTag(viewTag);
  }

  @NonNull
  @Override
  public final NavTag navTag() {
    return presenter.navTag();
  }
}
