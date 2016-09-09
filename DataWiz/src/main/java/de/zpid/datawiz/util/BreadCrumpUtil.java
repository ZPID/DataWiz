package de.zpid.datawiz.util;

import java.util.ArrayList;
import java.util.List;

import de.zpid.datawiz.enumeration.PageState;

public class BreadCrumpUtil {

  public static List<BreadCrump> generateBC(final PageState position, final String[] name, final long id) {
    List<BreadCrump> bcl = new ArrayList<BreadCrump>();
    switch (position) {
    case PANEL:
      bcl.add(new BreadCrump("Home", "/"));
      bcl.add(new BreadCrump("Panel", ""));
      break;
    case PROJECT:
      bcl.add(new BreadCrump("Home", "/"));
      bcl.add(new BreadCrump("Panel", "/panel"));
      bcl.add(new BreadCrump(name[0], ""));
      break;
    case DMP:
      bcl.add(new BreadCrump("Home", "/"));
      bcl.add(new BreadCrump("Panel", "/panel"));
      bcl.add(new BreadCrump("DMP", ""));
      break;
    case ACCESS:
      bcl.add(new BreadCrump("Home", "/"));
      bcl.add(new BreadCrump("Panel", "/panel"));
      bcl.add(new BreadCrump("Access", ""));
      break;
    case STUDY:
      bcl.add(new BreadCrump("Home", "/"));
      bcl.add(new BreadCrump("Panel", "/panel"));
      bcl.add(new BreadCrump(name[0], "/project/" + id));
      bcl.add(new BreadCrump(name[1], ""));
      break;
    case EXPORT:
      bcl.add(new BreadCrump("Home", "/"));
      bcl.add(new BreadCrump("Panel", "/panel"));
      bcl.add(new BreadCrump("Export", ""));
      break;
    case USERSETTING:
      bcl.add(new BreadCrump("Home", "/"));
      bcl.add(new BreadCrump("User Settings", ""));
      break;
    default:
      break;
    }
    return bcl;
  }
}
