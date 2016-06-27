package com.awesomeproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.awesomeproject.contract.Navigator;
import com.awesomeproject.util.StrUtil;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.awesomeproject.util.StrUtil.serializableToStr;
import static com.facebook.react.bridge.UiThreadUtil.assertOnUiThread;

public class MyNavigator extends MyReactModule implements Navigator {

  public interface ViewFactory<V extends View & NavigableView> {
    V createView(@NonNull ViewGroup parent, @NonNull NavTag tag);
  }

  public interface NavigableView {
    @NonNull
    NavTag navTag();
  }

  public static final class NavTag {
    @NonNull private final String base;
    @NonNull private final String extras;

    @NonNull
    public static NavTag parse(@NonNull String tag) {
      // View tag may have "extras" that define logical equality with other views for nav and
      // eventing purposes; these are separated from the base tag with a colon delimeter. We do not
      // care about these tags for deciding which view factory to use, so we separate them.
      String base;
      String extras;
      int startOfExtras = tag.indexOf(':');
      if (startOfExtras != -1) {
        base = tag.substring(0, startOfExtras);
        extras = tag.substring(startOfExtras + 1);
      } else {
        base = tag;
        extras = "";
      }
      return new NavTag(base, extras);
    }

    public NavTag(@NonNull String base, @NonNull String extras) {
      if (TextUtils.getTrimmedLength(base) == 0) {
        throw new IllegalArgumentException("base must be non-empty.");
      }
      this.base = base;
      this.extras = extras;
    }

    @NonNull
    public String base() {
      return base;
    }

    @NonNull
    public String extras() {
      return extras;
    }

    @NonNull
    public String tag() {
      return String.format("%s:%s", base, extras);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      NavTag navTag = (NavTag) o;
      return base.equals(navTag.base) && extras.equals(navTag.extras);
    }

    @Override
    public int hashCode() {
      int result = base.hashCode();
      result = 31 * result + extras.hashCode();
      return result;
    }

    @Override
    public String toString() {
      return tag();
    }
  }

  // -------------------

  private static final String TAG = "MyNavigator";

  @NonNull
  private static SharedPreferences prefs(@NonNull Context context) {
    return context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
  }

  // -------------------

  private Map<String, ViewFactory> viewFactories;
  private JSEventDispatcher eventDispatcher;

  private void injectDeps() {
    MyInjector inject = MyApp.injector(getReactApplicationContext());
    viewFactories = inject.viewFactoriesFor(this);
    eventDispatcher = inject.eventDispatcherFor(this);
  }

  // -------------------

  private final LinkedList<String> stack = new LinkedList<>();
  private final List<Animator> destroyAnims = new ArrayList<>();
  private boolean needsApplyStack;
  private boolean needsEmptyTag;
  private String emptyTag;
  private DisableableViewGroup root;

  public MyNavigator(ReactApplicationContext reactAppContext) {
    super(reactAppContext);
    injectDeps();
  }

  /*package*/ void setRoot(@NonNull DisableableViewGroup root) {
    assertOnUiThread();
    if (this.root != null) {
      throw new IllegalStateException("must only set appRoot once.");
    }
    this.root = root;
    if (needsApplyStack) {
      needsApplyStack = false;
      applyStack();
    }
  }

  @Override
  public String getName() {
    return TAG;
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = MapBuilder.newHashMap();
    for (String key : viewFactories.keySet()) {
      constants.put(key, key);
    }
    return constants;
  }

  @ReactMethod
  public void saveState(final String tag, final String state, final Promise p) {
    if (tag == null) {
      Log.e(TAG, "cannot saveState null tag.");
      p.reject(new Throwable("cannot saveState null tag."));
      return;
    }

    handler().post(new Runnable() {
      @Override
      public void run() {
        _saveState(NavTag.parse(tag), state);
        p.resolve(null);
      }
    });
  }

  private void _saveState(@NonNull NavTag tag, @Nullable String state) {
    assertOnUiThread();

    Log.d(TAG, String.format("_saveState %s %s", tag, state));
    prefs(getReactApplicationContext()) //
        .edit() //
        .putString(String.format("%s_state", tag), state) //
        .apply();
  }

  @ReactMethod
  public void restoreState(final String tag, final Promise p) {
    if (tag == null) {
      Log.e(TAG, "cannot restoreState null tag.");
      p.reject(new Throwable("cannot restoreState null tag."));
      return;
    }

    handler().post(new Runnable() {
      @Override
      public void run() {
        p.resolve(_restoreState(NavTag.parse(tag)));
      }
    });
  }

  private String _restoreState(@NonNull NavTag tag) {
    assertOnUiThread();
    MainActivity activity = activity();
    if (activity == null || !activity.isUiInteractable()) {
      throw new RuntimeException("bad _restoreState!");
    }

    Log.d(TAG, String.format("_restoreState %s", tag));
    return prefs(activity).getString(String.format("%s_state", tag), null);
  }

  @Override
  @ReactMethod
  public void navigate(final String target, final String extras, final String meta) {
    if (target == null) {
      Log.e(TAG, "cannot navigate to null target.");
      return;
    }

    final String fullTarget = new NavTag(target, extras == null
        ? ""
        : extras).toString();

    handler().post(new Runnable() {
      @Override
      public void run() {
        _navigate(fullTarget, meta);
      }
    });
  }

  private void _navigate(final String target, final String meta) {
    assertOnUiThread();
    MainActivity activity = activity();
    if (activity == null || !activity.isUiInteractable()) {
      Log.i(TAG, "Aborting navigate: activity dead, dying, or dormant.");
      return;
    }

    if (stack.contains(target)) {
      for (Iterator<String> it = stack.descendingIterator(); it.hasNext(); ) {
        if (it.next().equals(target)) {
          applyStack();
          return;
        }
        it.remove();
      }
      // this is pretty much impossible.
      throw new IllegalStateException("stack contained target, but no target found!");
    }

    stack.add(target);
    applyStack();
  }

  @ReactMethod
  public void goBack(final Promise p) {
    handler().post(new Runnable() {
      @Override
      public void run() {
        p.resolve(goBack());
      }
    });
  }

  @Override
  public boolean goBack() {
    assertOnUiThread();
    MainActivity activity = activity();
    if (activity == null || !activity.isUiInteractable()) {
      Log.i(TAG, "Aborting goBack: activity dead, dying, or dormant.");
      return false;
    }

    if (stack.isEmpty()) {
      Log.i(TAG, "Aborting goBack: empty stack.");
      return false;
    }

    stack.remove(stack.size() - 1);

    if (stack.isEmpty()) {
      dispatchDestroyAllNavigableViews(true);
      handler().post(new Runnable() {
        @Override
        public void run() {
          MainActivity activity = activity();
          if (activity != null) {
            activity.invokeDefaultOnBackPressed();
          }
        }
      });
      return false;
    }

    applyStack();
    return true;
  }

  public void dispatchGoBack() {
    assertOnUiThread();
    eventDispatcher.dispatch("onGoBack", null);
  }

  @ReactMethod
  public void empty(final String tag) {
    handler().post(new Runnable() {
      @Override
      public void run() {
        _empty(tag);
      }
    });
  }

  private void _empty(String tag) {
    assertOnUiThread();
    MainActivity activity = activity();
    if (activity == null || !activity.isUiInteractable()) {
      Log.i(TAG, "Aborting empty: activity dead, dying, or dormant.");
      return;
    }

    emptyTag = tag;
    if (needsEmptyTag) {
      needsEmptyTag = false;
      if (!stack.contains(emptyTag)) {
        stack.add(0, emptyTag);
        applyStack();
      }
    }
  }

  @ReactMethod
  public void stack(final Promise p) {
    handler().post(new Runnable() {
      @Override
      public void run() {
        p.resolve(Arguments.fromArray(stack.toArray(new String[stack.size()])));
      }
    });
  }

  // TODO - just testing, remove this.
  @ReactMethod
  public void recvJson(final ReadableMap json, final Promise p) {
    handler().post(new Runnable() {
      @Override
      public void run() {
        Log.e(TAG, "Got JSON from JS: servicesDisabled = " + //
            json.getMap("APP").getBoolean("servicesDisabled"));
        p.resolve(null);
      }
    });
  }

  @ReactMethod
  public void debugLog(final String dbg) {
    UiThreadUtil.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Log.e(TAG, "DEBUG FROM JS: " + dbg);
      }
    });
  }

  @Override
  public void clear() {
    assertOnUiThread();
    MainActivity activity = activity();
    if (activity == null) {
      Log.e(TAG, "Aborting clear: activity dead, dying, or dormant.");
      return;
    }

    prefs(activity) //
        .edit() //
        .remove("stack") //
        .apply();

    stack.clear();
    if (emptyTag == null) {
      needsEmptyTag = true;
    } else {
      stack.add(emptyTag);
    }

    applyStack();
    Log.e(TAG, "STACK CLEARED");
  }

  @Override
  public void save() {
    assertOnUiThread();
    MainActivity activity = activity();
    if (activity == null) {
      Log.e(TAG, "Aborting save: activity dead, dying, or dormant.");
      return;
    }

    prefs(activity) //
        .edit() //
        .putString("stack", serializableToStr(stack)) //
        .apply();
    Log.e(TAG, "STACK SAVED: " + Arrays.toString(stack.toArray()));
  }

  @Override
  public void restore() {
    assertOnUiThread();
    MainActivity activity = activity();
    if (activity == null) {
      Log.e(TAG, "Aborting restore: activity dead, dying, or dormant.");
      return;
    }

    stack.clear();
    String stackStr = prefs(activity).getString("stack", null);
    if (stackStr != null) {
      stack.addAll(StrUtil.<LinkedList<String>>serializableFromStr(stackStr));
    }

    if (stack.isEmpty()) {
      if (emptyTag == null) {
        needsEmptyTag = true;
      } else {
        stack.add(emptyTag);
      }
    }

    applyStack();
    Log.e(TAG, "STACK RESTORED: " + Arrays.toString(stack.toArray()));
  }

  private void applyStack() {
    assertOnUiThread();

    if (root == null) {
      needsApplyStack = true;
      return;
    }

    if (stack.isEmpty()) {
      // Nothing to see here...
      return;
    }

    // TODO what if the stack changed by more than one item at a time?

    NavTag topTag = NavTag.parse(stack.get(stack.size() - 1));
    final NavigableView oldTopView = findLastNavigableView();
    if (oldTopView != null && oldTopView.navTag().equals(topTag)) {
      // No need to navigate; already there.
      return;
    }

    ViewFactory viewFactory = viewFactories.get(topTag.base());
    if (viewFactory == null) {
      throw new IllegalStateException(String.format("No ViewFactory found for tag %s!", topTag));
    }

    final View newTopView = viewFactory.createView(root.viewGroup(), topTag);

    root.viewGroup().addView( //
        newTopView, -1, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));

    // Dispatch the event. TODO - need before and after anim events?

    dispatchInit(topTag);

    Log.e(TAG, "APPLY STACK: " + Arrays.toString(stack.toArray()));

    if (oldTopView != null) {

      // ANIM!!!!

      final boolean navBack = !isInStack(oldTopView.navTag());
      final View oldView = (View) oldTopView;

      // ----

      // TODO - pull animation logic from injects; don't assume simple crossfade

      newTopView.setAlpha(0f);
      ObjectAnimator oa = ObjectAnimator.ofFloat(newTopView, View.ALPHA, 0f, 1f);
      oa.setDuration(300);
      oa.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator a) {
          // TODO - onDestroyView, does it make sense here?
          destroyAnims.remove(a);
          root.viewGroup().removeView(oldView);
          dispatchDestroy(oldTopView.navTag(), navBack);
          root.enable();

          Log.e(TAG, "ROOT CHILD COUNT " + root.viewGroup().getChildCount());
        }
      });
      for (Animator a : new ArrayList<>(destroyAnims)) {
        a.end();
      }
      root.disable();
      destroyAnims.add(oa);
      oa.start();

      // ----
      // TODO - old synchronous transitions, stable but wrong

      //root.removeView(oldView);
      //
      //String oldTopTag = null;
      //for (String tag : viewFactories.keySet()) {
      //  if (oldTopView.matchesNavTag(tag)) {
      //    oldTopTag = tag;
      //    break;
      //  }
      //}
      //if (oldTopTag == null) {
      //  throw new IllegalStateException("No tag found for old top view!");
      //}
      //
      //dispatchDestroy(oldTopTag);
    }
  }

  @Nullable
  private NavigableView findLastNavigableView() {
    int i = findLastNavigableViewIndex();
    if (i != -1) {
      return (NavigableView) root.viewGroup().getChildAt(i);
    }
    return null;
  }

  private int findLastNavigableViewIndex() {
    for (int i = root.viewGroup().getChildCount() - 1; i >= 0; i--) {
      View child = root.viewGroup().getChildAt(i);
      if (child instanceof NavigableView) {
        return i;
      }
    }
    return -1;
  }

  private int dispatchDestroyAllNavigableViews(boolean permanent) {
    for (int i = 0; i < root.viewGroup().getChildCount(); i++) {
      View child = root.viewGroup().getChildAt(i);
      if (child instanceof NavigableView) {
        dispatchDestroy(((NavigableView) child).navTag(), permanent);
      }
    }
    return -1;
  }

  private boolean isInStack(@NonNull NavTag navTag) {
    for (String tag : stack) {
      if (NavTag.parse(tag).equals(navTag)) {
        return true;
      }
    }
    return false;
  }

  private void dispatchInit(@NonNull NavTag tag) {
    WritableMap args = Arguments.createMap();
    args.putString("tag", tag.toString());
    args.putString("tagBase", tag.base());
    args.putString("tagExtras", tag.extras());
    eventDispatcher.dispatch("onInitView", args);
  }

  private void dispatchDestroy(@NonNull NavTag tag, boolean permanent) {
    WritableMap args = Arguments.createMap();
    args.putString("tag", tag.toString());
    args.putString("tagBase", tag.base());
    args.putString("tagExtras", tag.extras());
    args.putBoolean("permanent", permanent);
    eventDispatcher.dispatch("onDestroyView", args);
  }
}
