package de.zpid.datawiz.enumeration;

public enum Roles {
  REL_ROLE(0), USER(1), ADMIN(2), PROJECT_ADMIN(3), PROJECT_READER(4), PROJECT_WRITER(5), DS_READER(6), DS_WRITER(7);
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
}
