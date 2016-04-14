package de.zpid.datawiz.util;

import java.util.ArrayList;
import java.util.List;

public class BreadCrumpUtil {

  public static List<BreadCrump> generateBC(final String position, final String[] name, final long id) {
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
    case "access":
      bcl.add(new BreadCrump("Home", "/"));
      bcl.add(new BreadCrump("Panel", "/panel"));
      bcl.add(new BreadCrump("Access", ""));
      break;
    case "study":
      bcl.add(new BreadCrump("Home", "/"));
      bcl.add(new BreadCrump("Panel", "/panel"));
      bcl.add(new BreadCrump(name[0], "/project/" + id));
      bcl.add(new BreadCrump(name[1], ""));
      break;
    default:
      break;
    }
    return bcl;
  }
}
