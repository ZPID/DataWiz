package de.zpid.datawiz.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import de.zpid.datawiz.enumeration.PageState;

public class BreadCrumpUtil {

  public static List<BreadCrump> generateBC(final PageState position, final String[] name, final long[] ids,
      final MessageSource messageSource) {
    List<BreadCrump> bcl = new ArrayList<BreadCrump>();
    switch (position) {
    case INDEX:
    	bcl.add(new BreadCrump(messageSource.getMessage("breadcrumb.home", null, LocaleContextHolder.getLocale()), ""));    	
    	break;
    case LOGIN:
    	bcl.add(new BreadCrump(messageSource.getMessage("breadcrumb.home", null, LocaleContextHolder.getLocale()), "/"));
    	bcl.add(new BreadCrump(messageSource.getMessage("breadcrumb.login", null, LocaleContextHolder.getLocale()), ""));
    	break;
    case REGISTER:
    	bcl.add(new BreadCrump(messageSource.getMessage("breadcrumb.home", null, LocaleContextHolder.getLocale()), "/"));
    	bcl.add(new BreadCrump(messageSource.getMessage("breadcrumb.register", null, LocaleContextHolder.getLocale()), ""));
    	break;
    case PANEL:
      bcl.add(new BreadCrump(messageSource.getMessage("breadcrumb.home", null, LocaleContextHolder.getLocale()), "/"));
      bcl.add(new BreadCrump(messageSource.getMessage("breadcrumb.panel", null, LocaleContextHolder.getLocale()), ""));
      break;
    case PROJECT:
      bcl.add(new BreadCrump(messageSource.getMessage("breadcrumb.home", null, LocaleContextHolder.getLocale()), "/"));
      bcl.add(new BreadCrump(messageSource.getMessage("breadcrumb.panel", null, LocaleContextHolder.getLocale()),
          "/panel"));
      bcl.add(new BreadCrump(name[0], ""));
      break;
    case STUDY:
      bcl.add(new BreadCrump(messageSource.getMessage("breadcrumb.home", null, LocaleContextHolder.getLocale()), "/"));
      bcl.add(new BreadCrump(messageSource.getMessage("breadcrumb.panel", null, LocaleContextHolder.getLocale()),
          "/panel"));
      bcl.add(new BreadCrump(name[0], "/project/" + ids[0] + "/studies"));
      bcl.add(new BreadCrump(name[1], ""));
      break;
    case RECORDS:
      bcl.add(new BreadCrump(messageSource.getMessage("breadcrumb.home", null, LocaleContextHolder.getLocale()), "/"));
      bcl.add(new BreadCrump(messageSource.getMessage("breadcrumb.panel", null, LocaleContextHolder.getLocale()),
          "/panel"));
      bcl.add(new BreadCrump(name[0], "/project/" + ids[0] + "/studies"));
      bcl.add(new BreadCrump(name[1], "/project/" + ids[0] + "/study/" + ids[1] + "/records"));
      bcl.add(new BreadCrump(name[2], ""));
      break;
    case USERSETTING:
      bcl.add(new BreadCrump(messageSource.getMessage("breadcrumb.home", null, LocaleContextHolder.getLocale()), "/"));
      bcl.add(new BreadCrump(
          messageSource.getMessage("breadcrumb.user.settings", null, LocaleContextHolder.getLocale()), ""));
      break;
    default:
      break;
    }
    return bcl;
  }
}
