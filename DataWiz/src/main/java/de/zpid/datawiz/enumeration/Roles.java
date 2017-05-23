package de.zpid.datawiz.enumeration;

import java.util.HashMap;
import java.util.Map;

public enum Roles {
  REL_ROLE(0),
  USER(1),
  ADMIN(2),
  PROJECT_ADMIN(3),
  PROJECT_READER(4),
  PROJECT_WRITER(5),
  DS_READER(6),
  DS_WRITER(7);

  private final int code;

  private Roles(int code) {
    this.code = code;
  }

  public int toInt() {
    return code;
  }

  public String toString() {
    return String.valueOf(code);
  }

  private static final Map<Integer, Roles> intToTypeMap = new HashMap<Integer, Roles>();
  static {
    for (Roles type : Roles.values()) {
      intToTypeMap.put(type.code, type);
    }
  }

  public static Roles fromInt(int i) {
    Roles type = intToTypeMap.get(Integer.valueOf(i));
    if (type == null)
      return null;
    return type;
  }
}
