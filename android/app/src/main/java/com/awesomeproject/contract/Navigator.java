package com.awesomeproject.contract;

public interface Navigator {
  void navigate(String target, String extras, String meta);

  // TODO set(target, dir, meta)

  boolean goBack();

  void clear();

  void save();

  void restore();
}
