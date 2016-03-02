package de.zpid.datawiz.util;

import java.io.Serializable;

public class BreadCrump implements Serializable {

  private static final long serialVersionUID = 4912624314815586654L;
  private String name;
  private String uri;

  public String getName() {
    return name;
  }

  public String getUri() {
    return uri;
  }

  public void setName(String name) {
    this.name = name;

  }

  public void setUri(String uri) {
    this.uri = uri;

  }

  public BreadCrump() {
    super();
  }

  public BreadCrump(String name, String uri) {
    super();
    this.name = name;
    this.uri = uri;
  }

  @Override
  public String toString() {
    return "BreadCrump [name=" + name + ", uri=" + uri + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((uri == null) ? 0 : uri.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BreadCrump other = (BreadCrump) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (uri == null) {
      if (other.uri != null)
        return false;
    } else if (!uri.equals(other.uri))
      return false;
    return true;
  }

}
