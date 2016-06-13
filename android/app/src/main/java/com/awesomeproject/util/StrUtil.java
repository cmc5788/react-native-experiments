package com.awesomeproject.util;

import android.support.annotation.NonNull;
import android.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class StrUtil {

  @NonNull
  public static String serializableToStr(@NonNull Serializable ser) {
    ObjectOutputStream oos = null;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      oos = new ObjectOutputStream(baos);
      oos.writeObject(ser);
      return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP | Base64.NO_PADDING);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (oos != null) {
        try {
          oos.close();
        } catch (Exception ignored) {
        }
      }
    }
  }

  @NonNull
  public static <T extends Serializable> T serializableFromStr(@NonNull String str) {
    ObjectInputStream ois = null;
    try {
      byte[] data = Base64.decode(str, Base64.NO_WRAP | Base64.NO_PADDING);
      ois = new ObjectInputStream(new ByteArrayInputStream(data));
      Object o = ois.readObject();
      //noinspection unchecked
      return (T) o;
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (ois != null) {
        try {
          ois.close();
        } catch (Exception ignored) {
        }
      }
    }
  }

  private StrUtil() {
  }
}
