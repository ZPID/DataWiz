package de.zpid.datawiz.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.zpid.datawiz.dto.StudyListTypesDTO;
import org.springframework.stereotype.Component;

@Component
public class ListUtil {

  /**
   * 
   * @param fst
   * @param snd
   * @return
   */
  public static boolean equalsWithoutOrder(final List<?> fst, final List<?> snd) {
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

  public static void deleteEmptyStudyListTypes(final List<StudyListTypesDTO> fst) {
    fst.removeIf(p -> p.getText().trim().equals(""));
  }

  public static <T> List<T> addObject(List<T> list, T obj) {
    if (list == null)
      list = new ArrayList<T>();
    if (list.size() < 1)
      list.add(obj);
    return list;
  }
}
