package com.awesomeproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.awesomeproject.util.StrUtil.serializableToStr;
import static com.facebook.react.bridge.UiThreadUtil.assertOnUiThread;

public class MyNavigator extends MyReactModule implements Navigator {

  public interface ViewFactory<V extends View & NavigableView> {
    V createView(ViewGroup parent);
  }

  public interface NavigableView {
    boolean matchesNavTag(String tag);
  }

  // -------------------

  // TODO - actually use these constants, or drop them if they prove to not be useful.
  private static final String TAG = "MyNavigator";
  private static final String FORWARD = "FORWARD";
  private static final String REPLACE = "REPLACE";
  private static final String BACKWARD = "BACKWARD";

  @NonNull
  private static SharedPreferences prefs(@NonNull MainActivity activity) {
    return activity.getSharedPreferences(TAG, Context.MODE_PRIVATE);
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
    constants.put(FORWARD, 1);
    constants.put(REPLACE, 0);
    constants.put(BACKWARD, -1);
    return constants;
  }

  @Override
  @ReactMethod
  public void navigate(final String target, final int direction, final String meta) {
    if (target == null) {
      Log.e(TAG, "cannot navigate to null target.");
      return;
    }

    handler().post(new Runnable() {
      @Override
      public void run() {
        _navigate(target, direction, meta);
      }
    });
  }

  private void _navigate(final String target, final int direction, final String meta) {
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

    String poppedTag = stack.remove(stack.size() - 1);

    if (stack.isEmpty()) {
      NavigableView oldTopView = findFirstNavigableView();
      if (oldTopView != null && oldTopView.matchesNavTag(poppedTag)) {
        dispatchDestroy(poppedTag);
      }
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

    String topTag = stack.get(stack.size() - 1);
    final NavigableView oldTopView = findFirstNavigableView();
    if (oldTopView != null && oldTopView.matchesNavTag(topTag)) {
      // No need to navigate; already there.
      return;
    }

    ViewFactory viewFactory = viewFactories.get(topTag);
    if (viewFactory == null) {
      throw new IllegalStateException(String.format("No ViewFactory found for tag %s!", topTag));
    }

    // TODO :
    // this should be asynchronous, allow for some time where both views are attached so we can
    // have some in-between animations, etc.

    final View newTopView = viewFactory.createView(root.viewGroup());

    root.viewGroup().addView( //
        newTopView, -1, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));

    // Dispatch the event. TODO - need before and after anim events?

    dispatchInit(topTag);

    Log.e(TAG, "APPLY STACK: " + Arrays.toString(stack.toArray()));

    if (oldTopView != null) {

      // ANIM!!!!

      final View oldView = (View) oldTopView;

      // ----

      // TODO - pull animation logic from injects; don't assume simple crossfade

      root.disable();
      newTopView.setAlpha(0f);
      newTopView.animate().alpha(1f).setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          newTopView.animate().setListener(null);

          root.viewGroup().removeView(oldView);

          String oldTopTag = null;
          for (String tag : viewFactories.keySet()) {
            if (oldTopView.matchesNavTag(tag)) {
              oldTopTag = tag;
              break;
            }
          }
          if (oldTopTag == null) {
            throw new IllegalStateException("No tag found for old top view!");
          }

          // TODO - onDestroyView, does it make sense here?
          dispatchDestroy(oldTopTag);
          root.enable();
        }
      });

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
  private NavigableView findFirstNavigableView() {
    for (int i = 0; i < root.viewGroup().getChildCount(); i++) {
      View child = root.viewGroup().getChildAt(i);
      if (child instanceof NavigableView) {
        return (NavigableView) child;
      }
    }
    return null;
  }

  private void dispatchInit(String tag) {
    WritableMap args = Arguments.createMap();
    args.putString("tag", tag);
    eventDispatcher.dispatch("onInitView", args);
  }

  private void dispatchDestroy(String tag) {
    WritableMap args = Arguments.createMap();
    args.putString("tag", tag);
    eventDispatcher.dispatch("onDestroyView", args);
  }
}
