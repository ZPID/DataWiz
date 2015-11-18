package de.zpid.datawiz.util;

enum UserRoles {
  USER(1), ADMIN(2);
  private final int value;

  private UserRoles(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}