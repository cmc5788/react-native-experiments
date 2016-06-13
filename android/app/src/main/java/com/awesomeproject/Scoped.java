package com.awesomeproject;

public class Scoped<T> {

  public final T val;
  public final int scope;

  public Scoped(T val, int scope) {
    this.val = val;
    this.scope = scope;
  }
}
