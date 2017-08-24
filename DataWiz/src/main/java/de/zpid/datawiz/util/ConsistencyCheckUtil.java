package de.zpid.datawiz.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.ExportStudyDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.form.ExportProjectForm;

@Component
@Scope("singleton")
public class ConsistencyCheckUtil {

	@Autowired
	private MessageSource messageSource;

	public void checkStudyConsistency(final ExportStudyDTO studExp, final StudyDTO study) {
		List<String> warnings = new ArrayList<>();
		List<String> notices = new ArrayList<>();
		if (study != null) {
			if (!study.isConsent() && study.isPersDataColl() && study.getPersDataPres() != null && study.getPersDataPres().equals("NON_ANONYMOUS")) {
				warnings.add(messageSource.getMessage("export.warning.study.study.consent.false", null, LocaleContextHolder.getLocale()));
			}
			if (study.isConsent() && !study.isConsentShare() && study.isPersDataColl() && study.getPersDataPres() != null
			    && study.getPersDataPres().equals("NON_ANONYMOUS")) {
				warnings.add(messageSource.getMessage("export.warning.study.study.consent.true", null, LocaleContextHolder.getLocale()));
			}
			if (study.isIrb()) {
				notices.add(messageSource.getMessage("export.notice.study.irb", null, LocaleContextHolder.getLocale()));
			}
			if (study.isPersDataColl() && (study.getPersDataPres() == null || study.getPersDataPres().equals(""))) {
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

	public void checkDMPConsistency(final ExportProjectForm exForm, final DmpDTO dmp) {
		List<String> warnings = new ArrayList<>();
		List<String> notices = new ArrayList<>();
		if (dmp != null) {
			if (dmp.getPublStrategy() != null && !dmp.getPublStrategy().isEmpty()) {
				StringBuilder str = new StringBuilder();
				str.append(messageSource.getMessage("export.notice.dmp.publstrategy",
				    new Object[] { messageSource.getMessage("dmp.edit.publStrategy." + dmp.getPublStrategy(), null, LocaleContextHolder.getLocale()) },
				    LocaleContextHolder.getLocale()));
				if (dmp.getPublStrategy().equals("repository") && dmp.getDepositName() != null && !dmp.getDepositName().isEmpty()) {
					str.append("<br />");
					str.append(messageSource.getMessage("export.notice.dmp.publstrategy.name", null, LocaleContextHolder.getLocale()));
					str.append(dmp.getDepositName());
				}
				notices.add(str.toString());
			}
			if (dmp.isDataProtection()) {
				notices.add(messageSource.getMessage("export.notice.dmp.protection", null, LocaleContextHolder.getLocale()));
			}
			if (dmp.isSensitiveDataIncluded()) {
				notices.add(messageSource.getMessage("export.notice.dmp.sensitive", null, LocaleContextHolder.getLocale()));
			}
			if (dmp.getFrameworkNationality() != null && dmp.getFrameworkNationality().equals("international")) {
				notices.add(messageSource.getMessage("export.notice.dmp.frameworknationality", null, LocaleContextHolder.getLocale()));
			}
		}
		exForm.setNotices(notices);
		exForm.setWarnings(warnings);
	}
}
