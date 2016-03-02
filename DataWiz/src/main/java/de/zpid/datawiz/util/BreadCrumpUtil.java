package de.zpid.datawiz.util;

import java.util.ArrayList;
import java.util.List;

public class BreadCrumpUtil {

  public static List<BreadCrump> generateBC(String position) {
    List<BreadCrump> bcl = new ArrayList<BreadCrump>();
    switch (position) {
    case "panel":
      bcl.add(new BreadCrump("Home", "/"));
      bcl.add(new BreadCrump("Panel", ""));
      break;
    case "project":
      bcl.add(new BreadCrump("Home", "/"));
      bcl.add(new BreadCrump("Panel", "/panel"));
      bcl.add(new BreadCrump("Projekt", ""));
      break;
    case "dmp":
      bcl.add(new BreadCrump("Home", "/"));
      bcl.add(new BreadCrump("Panel", "/panel"));
      bcl.add(new BreadCrump("DMP", ""));
      break;
    default:
      break;
    }
    return bcl;
  }
}
