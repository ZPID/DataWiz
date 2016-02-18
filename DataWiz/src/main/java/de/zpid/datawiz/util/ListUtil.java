package de.zpid.datawiz.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListUtil {

  /**
   * 
   * @param fst
   * @param snd
   * @return
   */
  public static boolean equalsWithoutOrder(List<?> fst, List<?> snd) {
    if (fst != null && snd != null) {
      if (fst.size() == snd.size()) {
        List<?> cfst = new ArrayList<Object>(fst);
        List<?> csnd = new ArrayList<Object>(snd);
        Iterator<?> ifst = cfst.iterator();
        boolean foundEqualObject;
        while (ifst.hasNext()) {
          Iterator<?> isnd = csnd.iterator();
          foundEqualObject = false;
          Object sndn = ifst.next();
          while (isnd.hasNext()) {
            if (sndn.equals(isnd.next())) {
              ifst.remove();
              isnd.remove();
              foundEqualObject = true;
              break;
            }
          }
          if (!foundEqualObject) {
            break;
          }
        }
        if (cfst.isEmpty()) {
          return true;
        }
      }
    } else if (fst == null && snd == null) {
      return true;
    }
    return false;
  }
}
