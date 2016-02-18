package de.zpid.datawiz.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListUtil {

  public static boolean equalsWithoutOrder(List<?> fst, List<?> snd) {
    if (fst != null && snd != null) {
      if (fst.size() == snd.size()) {
        // create copied lists so the original list is not modified
        List<?> cfst = new ArrayList<Object>(fst);
        List<?> csnd = new ArrayList<Object>(snd);

        Iterator<?> ifst = cfst.iterator();
        boolean foundEqualObject;
        while (ifst.hasNext()) {
          Iterator<?> isnd = csnd.iterator();
          foundEqualObject = false;
          while (isnd.hasNext()) {
            if (ifst.next().equals(isnd.next())) {
              ifst.remove();
              isnd.remove();
              foundEqualObject = true;
              break;
            }
          }

          if (!foundEqualObject) {
            // fail early
            break;
          }
        }
        if (cfst.isEmpty()) { // both temporary lists have the same size
          return true;
        }
      }
    } else if (fst == null && snd == null) {
      return true;
    }
    return false;
  }

}
