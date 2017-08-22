package de.zpid.datawiz.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import de.zpid.datawiz.dto.ExportStudyDTO;
import de.zpid.datawiz.dto.StudyDTO;

@Component
@Scope("singleton")
public class ConsistencyCheckUtil {

	@Autowired
	private MessageSource messageSource;

	public void checkStudyConsisty(final ExportStudyDTO studExp, final StudyDTO study) {
		List<String> warnings = new ArrayList<>();
		List<String> notices = new ArrayList<>();
		if (study != null) {
			if (checkStudyConsensFalse(study)) {
				warnings.add(messageSource.getMessage("export.warning.study.study.consent.false", null, LocaleContextHolder.getLocale()));
			}
			if (checkStudyConsensTrue(study)) {
				warnings.add(messageSource.getMessage("export.warning.study.study.consent.true", null, LocaleContextHolder.getLocale()));
			}
			if (checkIRB(study)) {
				notices.add(messageSource.getMessage("export.notice.study.irb", null, LocaleContextHolder.getLocale()));
			}
			if (checkPersDataColl(study)) {
				notices.add(messageSource.getMessage("export.notice.study.persDataColl", null, LocaleContextHolder.getLocale()));
			}
			if (study.isCopyright()) {
				notices.add(messageSource.getMessage("export.notice.study.copyright", null, LocaleContextHolder.getLocale()));
			}
			if (study.isThirdParty()) {
				notices.add(messageSource.getMessage("export.notice.study.thirdparty", null, LocaleContextHolder.getLocale()));
			}
		}
		studExp.setNotices(notices);
		studExp.setWarnings(warnings);
	}

	/**
	 * 
	 * @param study
	 * @return
	 */
	private boolean checkIRB(final StudyDTO study) {
		boolean result = false;
		if (study.isIrb()) {
			result = true;
		}
		return result;
	}

	/**
	 * 
	 * @param study
	 * @return
	 */
	private boolean checkStudyConsensFalse(final StudyDTO study) {
		boolean result = false;
		if (!study.isConsent() && study.isPersDataColl() && study.getPersDataPres().equals("NON_ANONYMOUS")) {
			result = true;
		}
		return result;
	}

	/**
	 * 
	 * @param study
	 * @return
	 */
	private boolean checkStudyConsensTrue(final StudyDTO study) {
		boolean result = false;
		if (study.isConsent() && !study.isConsentShare() && study.isPersDataColl() && study.getPersDataPres().equals("NON_ANONYMOUS")) {
			result = true;
		}
		return result;
	}

	/**
	 * 
	 * @param study
	 * @return
	 */
	private boolean checkPersDataColl(final StudyDTO study) {
		boolean result = false;
		if (study.isPersDataColl() && study.getPersDataPres().equals("")) {
			result = true;
		}
		return result;
	}
	
	

}
