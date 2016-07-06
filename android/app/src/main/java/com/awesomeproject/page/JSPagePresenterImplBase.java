package com.awesomeproject.page;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import com.awesomeproject.JSEventDispatcher;
import com.awesomeproject.MyNavigator;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class JSPagePresenterImplBase<V extends JSPageView> implements JSPagePresenter {

  private final JSEventDispatcher eventDispatcher;
  private final V view;
  private final MyNavigator.NavTag tag;
  private final List<JsSetter> setters = new ArrayList<>();

  protected JSPagePresenterImplBase( //
      @NonNull JSEventDispatcher eventDispatcher, //
      @NonNull V view, //
      @NonNull MyNavigator.NavTag tag) {
    this.eventDispatcher = eventDispatcher;
    this.view = view;
    this.tag = tag;
  }

  protected final V view() {
    return view;
  }

  protected final void registerSetters(Collection<? extends JsSetter> jsSetters) {
    setters.addAll(jsSetters);
  }

  protected final void dispatchToJs(@NonNull String event) {
    dispatchToJs(event, null);
  }

  protected final void dispatchToJs(@NonNull String event, @Nullable Object data) {
    eventDispatcher.dispatch(String.format("%s.%s", view.navTag(), event), data);
  }

  @Override
  public final void receiveViewEvent(@NonNull String viewTag, @Nullable ReadableMap args) {
    if (args == null) return;
    for (JsSetter setter : setters) {
      if (setter.tryInvoke(args)) {
        return;
      }
    }
  }

  @Override
  public final boolean respondsToTag(@NonNull String viewTag) {
    return tag.equals(MyNavigator.NavTag.parse(viewTag));
  }

  @NonNull
  @Override
  public final MyNavigator.NavTag navTag() {
    return tag;
  }

  protected abstract class JsSetter {
    public final String name;

    protected JsSetter(String name) {
      this.name = name;
    }

    public abstract void invoke(@NonNull ReadableMap rm);

    public final boolean tryInvoke(@NonNull ReadableMap rm) {
      if (!rm.hasKey(name)) return false;
      invoke(rm);
      return true;
    }
  }

  protected abstract class JsSetter0 extends JsSetter {
    protected JsSetter0(String name) {
      super(name);
    }

    public abstract void set();

    @Override
    public void invoke(@NonNull ReadableMap rm) {
      set();
    }
  }

  protected abstract class JsSetter1<T> extends JsSetter {
    private final JsToNative<T> js2n;

    protected JsSetter1(String name, //
        JsToNative<T> js2n) {
      super(name);
      this.js2n = js2n;
    }

    public abstract void set(T t);

    @Override
    public void invoke(@NonNull ReadableMap rm) {
      set(js2n.toNative(rm, name, view.context()));
    }
  }

  protected abstract class JsSetter2<T1, T2> extends JsSetter {
    private final JsToNative<T1> js2n1;
    private final JsToNative<T2> js2n2;

    protected JsSetter2(String name, //
        JsToNative<T1> js2n1, //
        JsToNative<T2> js2n2) {
      super(name);
      this.js2n1 = js2n1;
      this.js2n2 = js2n2;
    }

    public abstract void set(T1 t1, T2 t2);

    @Override
    public void invoke(@NonNull ReadableMap rm) {
      rm = fromArr(rm.getArray(name));
      set(js2n1.toNative(rm, "0", view.context()), //
          js2n2.toNative(rm, "1", view.context()));
    }
  }

  protected abstract class JsSetter3<T1, T2, T3> extends JsSetter {
    private final JsToNative<T1> js2n1;
    private final JsToNative<T2> js2n2;
    private final JsToNative<T3> js2n3;

    protected JsSetter3(String name, //
        JsToNative<T1> js2n1, //
        JsToNative<T2> js2n2, //
        JsToNative<T3> js2n3) {
      super(name);
      this.js2n1 = js2n1;
      this.js2n2 = js2n2;
      this.js2n3 = js2n3;
    }

    public abstract void set(T1 t1, T2 t2, T3 t3);

    @Override
    public void invoke(@NonNull ReadableMap rm) {
      rm = fromArr(rm.getArray(name));
      set(js2n1.toNative(rm, "0", view.context()), //
          js2n2.toNative(rm, "1", view.context()), //
          js2n3.toNative(rm, "2", view.context()));
    }
  }

  protected abstract class JsSetter4<T1, T2, T3, T4> extends JsSetter {
    private final JsToNative<T1> js2n1;
    private final JsToNative<T2> js2n2;
    private final JsToNative<T3> js2n3;
    private final JsToNative<T4> js2n4;

    protected JsSetter4(String name, //
        JsToNative<T1> js2n1, //
        JsToNative<T2> js2n2, //
        JsToNative<T3> js2n3, //
        JsToNative<T4> js2n4) {
      super(name);
      this.js2n1 = js2n1;
      this.js2n2 = js2n2;
      this.js2n3 = js2n3;
      this.js2n4 = js2n4;
    }

    public abstract void set(T1 t1, T2 t2, T3 t3, T4 t4);

    @Override
    public void invoke(@NonNull ReadableMap rm) {
      rm = fromArr(rm.getArray(name));
      set(js2n1.toNative(rm, "0", view.context()), //
          js2n2.toNative(rm, "1", view.context()), //
          js2n3.toNative(rm, "2", view.context()), //
          js2n4.toNative(rm, "3", view.context()));
    }
  }

  protected interface JsToNative<N> {
    @Nullable
    N toNative(@NonNull ReadableMap rm, String k, Context ctx);
  }

  protected static final JsToNative<String> JS_TO_STR = new JsToNative<String>() {
    @Nullable
    @Override
    public String toNative(@NonNull ReadableMap rm, String k, Context ctx) {
      ReadableType rt = rm.getType(k);

      if (rt == ReadableType.String) {
        return rm.getString(k);
      }

      if (rt == ReadableType.Number) {
        try {
          return String.valueOf(rm.getInt(k));
        } catch (Exception ignored) {
        }
        return String.valueOf(rm.getDouble(k));
      }

      if (rt == ReadableType.Map) {
        ReadableMap map = rm.getMap(k);
        if (map.hasKey("id") && map.getType("id") == ReadableType.Number) {
          if (map.hasKey("args") && map.getType("args") == ReadableType.Array) {
            ReadableArray arr = map.getArray("args");
            String[] strArgs = new String[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
              strArgs[i] = toNative(fromArr(arr), String.valueOf(i), ctx);
            }
            return ctx.getString(map.getInt("id"), strArgs);
          }
          return ctx.getString(map.getInt("id"));
        }
        if (map.hasKey("id") && map.getType("id") == ReadableType.String) {
          if (map.hasKey("args") && map.getType("args") == ReadableType.Array) {
            ReadableArray arr = map.getArray("args");
            String[] strArgs = new String[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
              strArgs[i] = toNative(fromArr(arr), String.valueOf(i), ctx);
            }
            int strId = ctx.getResources().getIdentifier( //
                map.getString("id"), "string", ctx.getPackageName());
            return ctx.getString(strId, strArgs);
          }
          int strId = ctx.getResources().getIdentifier( //
              map.getString("id"), "string", ctx.getPackageName());
          return ctx.getString(strId);
        }
      }

      if (rt == ReadableType.Array) {
        ReadableArray arr = rm.getArray(k);
        if (arr.size() > 0) {
          return toNative(fromArr(arr), "0", ctx);
        }
      }

      return null;
    }
  };

  protected static final JsToNative<Integer> JS_TO_INT = new JsToNative<Integer>() {
    @Nullable
    @Override
    public Integer toNative(@NonNull ReadableMap rm, String k, Context ctx) {
      ReadableType rt = rm.getType(k);

      if (rt == ReadableType.String) {
        return Integer.valueOf(rm.getString(k));
      }

      if (rt == ReadableType.Number) {
        return rm.getInt(k);
      }

      if (rt == ReadableType.Map) {
        ReadableMap map = rm.getMap(k);
        if (map.hasKey("id") && map.getType("id") == ReadableType.Number) {
          return ctx.getResources().getInteger(map.getInt("id"));
        }
      }

      if (rt == ReadableType.Array) {
        ReadableArray arr = rm.getArray(k);
        if (arr.size() > 0) {
          return toNative(fromArr(arr), "0", ctx);
        }
      }

      return null;
    }
  };

  protected static final JsToNative<Double> JS_TO_DBL = new JsToNative<Double>() {
    @Nullable
    @Override
    public Double toNative(@NonNull ReadableMap rm, String k, Context ctx) {
      ReadableType rt = rm.getType(k);

      if (rt == ReadableType.String) {
        return Double.valueOf(rm.getString(k));
      }

      if (rt == ReadableType.Number) {
        return rm.getDouble(k);
      }

      if (rt == ReadableType.Array) {
        ReadableArray arr = rm.getArray(k);
        if (arr.size() > 0) {
          return toNative(fromArr(arr), "0", ctx);
        }
      }

      return null;
    }
  };

  protected static final JsToNative<Float> JS_TO_FLT = new JsToNative<Float>() {
    @Nullable
    @Override
    public Float toNative(@NonNull ReadableMap rm, String k, Context ctx) {
      Double dbl = JS_TO_DBL.toNative(rm, k, ctx);
      if (dbl != null) return dbl.floatValue();
      return null;
    }
  };

  protected static final JsToNative<Boolean> JS_TO_BOOL = new JsToNative<Boolean>() {
    @Nullable
    @Override
    public Boolean toNative(@NonNull ReadableMap rm, String k, Context ctx) {
      ReadableType rt = rm.getType(k);

      if (rt == ReadableType.String) {
        return Boolean.valueOf(rm.getString(k));
      }

      if (rt == ReadableType.Map) {
        ReadableMap map = rm.getMap(k);
        if (map.hasKey("id") && map.getType("id") == ReadableType.Number) {
          return ctx.getResources().getBoolean(map.getInt("id"));
        }
      }

      if (rt == ReadableType.Array) {
        ReadableArray arr = rm.getArray(k);
        if (arr.size() > 0) {
          return toNative(fromArr(arr), "0", ctx);
        }
      }

      return null;
    }
  };

  protected static final JsToNative<Integer> JS_TO_CLR = new JsToNative<Integer>() {
    @Nullable
    @Override
    public Integer toNative(@NonNull ReadableMap rm, String k, Context ctx) {
      ReadableType rt = rm.getType(k);

      if (rt == ReadableType.String) {
        return Color.parseColor(rm.getString(k));
      }

      if (rt == ReadableType.Number) {
        return rm.getInt(k);
      }

      if (rt == ReadableType.Map) {
        ReadableMap map = rm.getMap(k);
        if (map.hasKey("id") && map.getType("id") == ReadableType.Number) {
          return ContextCompat.getColor(ctx, map.getInt("id"));
        }
      }

      if (rt == ReadableType.Array) {
        ReadableArray arr = rm.getArray(k);
        if (arr.size() > 0) {
          return toNative(fromArr(arr), "0", ctx);
        }
      }

      return null;
    }
  };

  protected static ReadableMap fromArr(final ReadableArray arr) {
    return new ReadableMap() {
      @Override
      public boolean hasKey(String name) {
        int i = Integer.parseInt(name);
        return i >= 0 && i < arr.size();
      }

      @Override
      public boolean isNull(String name) {
        return arr.isNull(Integer.parseInt(name));
      }

      @Override
      public boolean getBoolean(String name) {
        return arr.getBoolean(Integer.parseInt(name));
      }

      @Override
      public double getDouble(String name) {
        return arr.getDouble(Integer.parseInt(name));
      }

      @Override
      public int getInt(String name) {
        return arr.getInt(Integer.parseInt(name));
      }

      @Override
      public String getString(String name) {
        return arr.getString(Integer.parseInt(name));
      }

      @Override
      public ReadableArray getArray(String name) {
        return arr.getArray(Integer.parseInt(name));
      }

      @Override
      public ReadableMap getMap(String name) {
        return arr.getMap(Integer.parseInt(name));
      }

      @Override
      public ReadableType getType(String name) {
        return arr.getType(Integer.parseInt(name));
      }

      @Override
      public ReadableMapKeySetIterator keySetIterator() {
        throw new UnsupportedOperationException();
      }
    };
  }
}
