package com.awesomeproject.contract;

public interface Navigator {
  /**
   * Navigate TO target, IN direction (-1 = backwards, 0 = replace, 1 = forwards), WITH meta.
   */
  void navigate(String target, int direction, String meta);
}
