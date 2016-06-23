package com.awesomeproject.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapUtil {

  public interface StringMapBuilder<T> {

    @NonNull
    StringMapBuilder<T> put(@NonNull String key, @Nullable T val);

    @NonNull
    Map<String, T> map();

    @NonNull
    Map<String, T> immutableMap();
  }

  @NonNull
  public static <T> StringMapBuilder<T> buildStringMap() {
    return new StringMapBuilderImpl<>();
  }

  private static class StringMapBuilderImpl<T> implements StringMapBuilder<T> {

    private final Map<String, T> map = new HashMap<>();

    private StringMapBuilderImpl() {
    }

    @NonNull
    public StringMapBuilder<T> put(@NonNull String key, @Nullable T val) {
      map.put(key, val);
      return this;
    }

    @NonNull
    public Map<String, T> map() {
      return map;
    }

    @NonNull
    @Override
    public Map<String, T> immutableMap() {
      return Collections.unmodifiableMap(map());
    }
  }

  protected MapUtil() {
    throw new UnsupportedOperationException();
  }
}
