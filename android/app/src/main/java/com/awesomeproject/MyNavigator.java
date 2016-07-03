package com.awesomeproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import com.awesomeproject.contract.Navigator;
import com.awesomeproject.util.StrUtil;
import com.awesomeproject.util.ViewUtil.ViewAction;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.awesomeproject.util.StrUtil.serializableToStr;
import static com.awesomeproject.util.ViewUtil.onNextLayout;
import static com.facebook.react.bridge.UiThreadUtil.assertOnUiThread;

public class MyNavigator extends MyReactModule implements Navigator {

  public interface ViewFactory<V extends View & NavTaggable> {
    V createView(@NonNull ViewGroup parent, @NonNull NavTag tag);
  }

  public interface NavTaggable {
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
  private final Map<String, SparseArray<Parcelable>> hierarchyStates = new HashMap<>();
  private final List<Animator> destroyAnims = new ArrayList<>();
  private boolean needsApplyStack;
  private boolean needsEmptyTag;
  private String emptyTag;
  private JSRoot root;

  public MyNavigator(ReactApplicationContext reactAppContext) {
    super(reactAppContext);
    injectDeps();
  }

  /*package*/ void setRoot(@NonNull JSRoot root) {
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

  void saveHierarchy(@NonNull Bundle b) {
    assertOnUiThread();

    if (!stack.isEmpty()) {
      NavTag topTag = NavTag.parse(stack.get(stack.size() - 1));
      for (int i = root.viewGroup().getChildCount() - 1; i >= 0; i--) {
        View child = root.viewGroup().getChildAt(i);
        if (child instanceof NavTaggable && ((NavTaggable) child).navTag().equals(topTag)) {
          SparseArray<Parcelable> state = new SparseArray<>();
          child.saveHierarchyState(state);
          hierarchyStates.put(topTag.toString(), state);
        }
      }
    }

    Bundle bb = new Bundle();
    for (Map.Entry<String, SparseArray<Parcelable>> e : hierarchyStates.entrySet()) {
      bb.putSparseParcelableArray(e.getKey(), e.getValue());
    }
    b.putBundle(String.format("%s_hierarchy", TAG), bb);
  }

  void restoreHierarchy(@NonNull Bundle b) {
    assertOnUiThread();
    Bundle bb = b.getBundle(String.format("%s_hierarchy", TAG));
    if (bb != null) {
      for (String k : bb.keySet()) {
        hierarchyStates.put(k, bb.getSparseParcelableArray(k));
      }
    }
  }

  public void dispatchAppInit() {
    assertOnUiThread();
    MainActivity activity = activity();
    if (activity == null) {
      Log.e(TAG, "Aborting init: activity dead, dying, or dormant.");
      return;
    }

    WritableMap wm = Arguments.createMap();
    for (Map.Entry<String, ?> e : prefs(activity).getAll().entrySet()) {
      String k = e.getKey();
      if (k.endsWith("_state")) {
        wm.putString(k.substring(0, k.length() - "_state".length()), (String) e.getValue());
      }
    }

    eventDispatcher.dispatch("onAppInit", wm);
  }

  @ReactMethod
  public void saveViewStates(final ReadableMap states, final Promise p) {
    if (states == null) {
      Log.e(TAG, "cannot saveViewStates null states.");
      p.reject(new Throwable("cannot saveViewStates null states."));
      return;
    }

    handler().post(new Runnable() {
      @Override
      public void run() {
        _saveViewStates(states);
        p.resolve(null);
      }
    });
  }

  private void _saveViewStates(@NonNull ReadableMap states) {
    assertOnUiThread();

    Log.d(TAG, "_saveViewStates");

    SharedPreferences.Editor edit = prefs(getReactApplicationContext()).edit();
    for (ReadableMapKeySetIterator i = states.keySetIterator(); i.hasNextKey(); ) {
      String k = i.nextKey();
      edit = edit.putString(String.format("%s_state", k), states.getString(k));
    }
    edit.apply();
  }

  @Override
  @ReactMethod
  public void navigate(final String target, final String extras) {
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
        _navigate(fullTarget);
      }
    });
  }

  private void _navigate(final String target) {
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
  public void setStack(final ReadableArray stack) {
    if (stack == null) {
      Log.e(TAG, "cannot setStack to null.");
      return;
    }

    final List<String> newStack = new ArrayList<>();
    for (int i = 0; i < stack.size(); i++) {
      ReadableType rt = stack.getType(i);
      if (rt == ReadableType.Array) {
        ReadableArray arr = stack.getArray(i);
        if (arr.size() == 1) {
          newStack.add(NavTag.parse(arr.getString(0)).toString());
        } else if (arr.size() == 2) {
          String target = arr.getString(0);
          String extras = arr.getString(1);
          newStack.add(new NavTag(target, extras == null
              ? ""
              : extras).toString());
        }
      } else if (rt == ReadableType.String) {
        newStack.add(NavTag.parse(stack.getString(i)).toString());
      }
    }

    handler().post(new Runnable() {
      @Override
      public void run() {
        _setStack(newStack);
      }
    });
  }

  private void _setStack(final List<String> newStack) {
    assertOnUiThread();

    if (newStack.isEmpty()) {
      throw new IllegalStateException("cannot set empty nav stack.");
    }

    MainActivity activity = activity();
    if (activity == null || !activity.isUiInteractable()) {
      Log.i(TAG, "Aborting setStack: activity dead, dying, or dormant.");
      return;
    }

    stack.clear();
    stack.addAll(newStack);
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
        .clear() //
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
    final NavTaggable oldTopView = findLastNavigableView();
    if (oldTopView != null && oldTopView.navTag().equals(topTag)) {
      // No need to navigate; already there.
      return;
    }

    ViewFactory viewFactory = viewFactories.get(topTag.base());
    if (viewFactory == null) {
      throw new IllegalStateException(String.format("No ViewFactory found for tag %s!", topTag));
    }

    final View newTopView = viewFactory.createView(root.viewGroup(), topTag);

    newTopView.setVisibility(View.INVISIBLE);
    List<ViewAction<View>> actions = new ArrayList<>();
    actions.add(new MakeVisibleAction());
    SparseArray<Parcelable> state = hierarchyStates.get(topTag.toString());
    if (state != null) {
      hierarchyStates.remove(topTag.toString());
      actions.add(new RestoreHierarchyOnceAction(state));
    }
    root.setAckInitListener(topTag.toString(), new AckInitActionTrigger(newTopView, actions));

    root.viewGroup().addView( //
        newTopView, -1, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));

    // Dispatch the event. TODO - need before and after anim events?

    dispatchInit(topTag);

    Log.e(TAG, "APPLY STACK: " + Arrays.toString(stack.toArray()));

    if (oldTopView != null) {

      // ANIM!!!!

      final boolean navBack = !isInStack(oldTopView.navTag());
      final View oldView = (View) oldTopView;

      final Runnable saveOldHierarchy = new Runnable() {
        @Override
        public void run() {
          if (navBack) {
            hierarchyStates.remove(oldTopView.navTag().toString());
          } else {
            SparseArray<Parcelable> oldState = new SparseArray<>();
            oldView.saveHierarchyState(oldState);
            hierarchyStates.put(oldTopView.navTag().toString(), oldState);
          }
        }
      };
      saveOldHierarchy.run();

      // ----

      // TODO - pull animation logic from injects; don't assume simple crossfade

      newTopView.setAlpha(0f);
      ObjectAnimator oa = ObjectAnimator.ofFloat(newTopView, View.ALPHA, 0f, 1f);
      oa.setDuration(300);
      oa.setInterpolator(new DecelerateInterpolator());
      oa.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator a) {
          // TODO - onDestroyView, does it make sense here?
          destroyAnims.remove(a);

          saveOldHierarchy.run();

          root.viewGroup().removeView(oldView);
          dispatchDestroy(oldTopView.navTag(), navBack);
          root.enable();

          Log.e(TAG, "ROOT CHILD COUNT " + root.viewGroup().getChildCount());
        }
      });
      for (Animator a : new ArrayList<>(destroyAnims)) {
        a.end();
      }
      if (!destroyAnims.isEmpty()) throw new RuntimeException("not all anims ended.");
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
    } else {
      newTopView.setAlpha(0f);
      ObjectAnimator oa = ObjectAnimator.ofFloat(newTopView, View.ALPHA, 0f, 1f);
      oa.setDuration(500);
      oa.setInterpolator(new DecelerateInterpolator());
      oa.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator a) {
          destroyAnims.remove(a);
          root.enable();
        }
      });
      for (Animator a : new ArrayList<>(destroyAnims)) {
        a.end();
      }
      if (!destroyAnims.isEmpty()) throw new RuntimeException("not all anims ended.");
      root.disable();
      destroyAnims.add(oa);
      oa.start();
    }
  }

  @Nullable
  private NavTaggable findLastNavigableView() {
    int i = findLastNavigableViewIndex();
    if (i != -1) {
      return (NavTaggable) root.viewGroup().getChildAt(i);
    }
    return null;
  }

  private int findLastNavigableViewIndex() {
    for (int i = root.viewGroup().getChildCount() - 1; i >= 0; i--) {
      View child = root.viewGroup().getChildAt(i);
      if (child instanceof NavTaggable) {
        return i;
      }
    }
    return -1;
  }

  private int dispatchDestroyAllNavigableViews(boolean permanent) {
    for (int i = 0; i < root.viewGroup().getChildCount(); i++) {
      View child = root.viewGroup().getChildAt(i);
      if (child instanceof NavTaggable) {
        dispatchDestroy(((NavTaggable) child).navTag(), permanent);
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

  private static class RestoreHierarchyOnceAction implements ViewAction<View> {
    SparseArray<Parcelable> state;

    RestoreHierarchyOnceAction(@NonNull SparseArray<Parcelable> state) {
      this.state = state;
    }

    @Override
    public void performViewAction(@NonNull View v) {
      if (state != null) {
        v.restoreHierarchyState(state);
        state = null;
      }
    }
  }

  private static class MakeVisibleAction implements ViewAction<View> {
    @Override
    public void performViewAction(@NonNull View v) {
      if (v.getVisibility() != View.VISIBLE) {
        v.setVisibility(View.VISIBLE);
      }
    }
  }

  private static class AckInitActionTrigger implements AckInitListener {
    final WeakReference<View> vRef;
    final Collection<ViewAction<View>> actions;

    private AckInitActionTrigger(@NonNull View v, @NonNull Collection<ViewAction<View>> actions) {
      this.vRef = new WeakReference<>(v);
      this.actions = actions;
    }

    @Override
    public void onAckInit(@NonNull AckInitDispatcher dispatcher) {
      dispatcher.removeCurrentAckInitListener();
      View v = vRef.get();
      if (v != null) onNextLayout(v, actions);
    }
  }
}
