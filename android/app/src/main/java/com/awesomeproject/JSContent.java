package com.awesomeproject;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.common.MapBuilder;
import java.lang.reflect.Field;
import java.util.Map;

public class JSContent extends MyReactModule {

  private static final String TAG = "JSContent";

  public JSContent(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return TAG;
  }

  @Override
  public Map<String, Object> getConstants() {
    //long tm = System.nanoTime();
    final Map<String, Object> constants = MapBuilder.newHashMap();
    // TODO - see how often this is called, does it perform terribly?
    for (Field f : R.string.class.getDeclaredFields()) {
      try {
        f.setAccessible(true);
        constants.put(f.getName(), f.get(null));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    //tm = System.nanoTime() - tm;
    //Log.e(TAG, "getConstants " + TimeUnit.NANOSECONDS.toMillis(tm) + "ms");
    return constants;
  }
}
