package de.zpid.datawiz.util;

import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.dto.FormTypesDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.datawiz.dto.StudyConstructDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.StudyListTypesDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.spss.dto.SPSSValueLabelDTO;
import de.zpid.spss.dto.SPSSVarDTO;
import de.zpid.spss.util.SPSSMissing;

@Component
@Scope("singleton")
public class DDIUtil {

	@Autowired
	protected MessageSource messageSource;
	private static Logger log = LogManager.getLogger(DDIUtil.class);
	private final Namespace xmlns_ddi = new Namespace("", "ddi:codebook:2_5");
	private final Namespace xmlns_dw = new Namespace("", "dw:codebook:1_0");
	private final Namespace xsi = new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
	private final String xsi_schemaLocation_ddi = "ddi:codebook:2_5 http://www.ddialliance.org/Specification/DDI-Codebook/2.5/XMLSchema/codebook.xsd";
	// TODO FIXE URL!!!
	private final String xsi_schemaLocation_dw = "dw:codebook:1_0 https://datawiz.leibniz-psychology.org/DataWiz/static/xml/datawiz.xsd";
	private final String version_ddi = "2.5";

	/**
	 * Create document for project, dmp and additional project material
	 * 
	 * @param project
	 * @param dmp
	 * @param pFiles
	 * @return
	 */
	public Document createProjectDocument(final ProjectForm pForm, final List<FileDTO> pFiles, final List<UserDTO> cUploader) {
		log.trace("Entering createProjectDocument for project [id: {}]", () -> pForm.getProject().getId());
		Document doc = createDoc();
		Element root = addAndReturnRoot(doc, false, true);
		try {
			createProjectAndDMPDocument(pForm, root, Locale.ENGLISH);
		} catch (Exception e) {
			// TODO
			log.error("ExportError: ", () -> e);
			return null;
		}
		log.trace("Leaving createProjectDocument for project [id: {}] without Errors", () -> pForm.getProject().getId());
		return doc;
	}

	/**
	 * Create document for study and additional study material
	 * 
	 * @param project
	 * @param study
	 * @param sFiles
	 * @return
	 */
	public Document createStudyDocument(final StudyForm sForm, final List<FileDTO> sFiles, final List<UserDTO> cUploader, final UserDTO user) {
		log.trace("Entering createStudyDocument for project [id: {}], study [id: {}]", () -> sForm.getProject().getId(), () -> sForm.getStudy().getId());
		Document doc = createDoc();
		Element root = addAndReturnRoot(doc, true, false);
		try {
			createStudyDDI(sForm, user, root, Locale.ENGLISH);
			if (sFiles != null)
				createOtherMatDDI(sFiles, cUploader, root, "study", Locale.ENGLISH);
		} catch (Exception e) {
			// TODO
			log.error("ExportError: ", () -> e);
			return null;
		}
		log.trace("Leaving createStudyDocument for project [id: {}], study [id: {}] without Errors", () -> sForm.getProject().getId(),
		    () -> sForm.getStudy().getId());
		return doc;
	}

	/**
	 * Create document for study and additional study material
	 * 
	 * @param project
	 * @param study
	 * @param sFiles
	 * @return
	 */
	public Document createRecordDocument(final StudyForm sForm, final FileDTO fileHash, final UserDTO user) {
		log.trace("Entering createRecordDocument for record [id: {}]", () -> sForm.getRecord().getId());
		Document doc = createDoc();
		Element root = addAndReturnRoot(doc, true, false);
		try {
			createCodeBookDDI(sForm, fileHash, root, user, Locale.ENGLISH);
		} catch (Exception e) {
			// TODO
			log.error("ExportError: ", () -> e);
			return null;
		}
		log.trace("Leaving createStudyDocumen for project [id: {}] without Errors", () -> sForm.getRecord().getId());
		return doc;
	}

	/**
	 * TODO locale listen!!!!
	 * 
	 * @param pForm
	 * @param dmpElement
	 * @throws Exception
	 */
	private void createProjectAndDMPDocument(final ProjectForm pForm, final Element dmpElement, final Locale locale) throws Exception {
		log.trace("Entering exportDmp(ProjectDTO project, DmpDTO dmp, Element dmpElement)");
		ProjectDTO project = pForm.getProject();
		if (project != null) {
			Element e1 = addAndReturnElement(dmpElement, "projectDoc", this.xmlns_dw);
			addTextElement(e1, "title", null, project.getTitle());
			addTextElement(e1, "identifier", null, project.getProjectIdent());
			addTextElement(e1, "scope", null, project.getDescription());
			Element e2;
			if ((project.getFunding() != null && !project.getFunding().isEmpty())
			    || (project.getGrantNumber() != null && !project.getGrantNumber().isEmpty())) {
				e2 = addAndReturnElement(e1, "funding", null);
				addTextElement(e2, "funder", null, project.getFunding());
				addTextElement(e2, "grantNumber", null, project.getGrantNumber());
			}
			if (pForm.getPrimaryContributor() != null) {
				e2 = addAndReturnElement(e1, "principalInvestigator", null);
				addTextElement(e2, "title", null, pForm.getPrimaryContributor().getTitle());
				addTextElement(e2, "orcid", null, pForm.getPrimaryContributor().getOrcid());
				addTextElement(e2, "firstName", null, pForm.getPrimaryContributor().getFirstName());
				addTextElement(e2, "lastName", null, pForm.getPrimaryContributor().getLastName());
				addTextElement(e2, "institute", null, pForm.getPrimaryContributor().getInstitution());
				addTextElement(e2, "department", null, pForm.getPrimaryContributor().getDepartment());
			}
			if (pForm.getContributors() != null && !pForm.getContributors().isEmpty()) {
				Element e2_t = addAndReturnElement(e1, "researchers", null);
				pForm.getContributors().forEach(contri -> {
					Element e3 = addAndReturnElement(e2_t, "researcher", null);
					addTextElement(e3, "title", null, contri.getTitle());
					addTextElement(e3, "orcid", null, contri.getOrcid());
					addTextElement(e3, "firstName", null, contri.getFirstName());
					addTextElement(e3, "lastName", null, contri.getLastName());
					addTextElement(e3, "institute", null, contri.getInstitution());
					addTextElement(e3, "department", null, contri.getDepartment());
				});
			}
		}
		DmpDTO dmp = pForm.getDmp();
		if (dmp != null) {
			Element e1 = addAndReturnElement(dmpElement, "dmp", this.xmlns_dw);
			Element e2 = addAndReturnElement(e1, "administration", null);
			addTextElement(e2, "duration", null, dmp.getDuration());
			addTextElement(e2, "partner", null, dmp.getOrganizations());
			addTextElement(e2, "dmpAims", null, dmp.getPlanAims());
			removeElementIfEmpty(e2);
			e2 = addAndReturnElement(e1, "researchData", null);
			Element e3 = addAndReturnElement(e2, "existingData", null);
			if (dmp.getExistingData() != null && !dmp.getExistingData().isEmpty())
				addTextElement(e3, "dataReuse", null, messageSource.getMessage("dmp.edit.existingData." + dmp.getExistingData(), null, locale));
			addTextElement(e3, "dataCitation", null, dmp.getDataCitation());
			addTextElement(e3, "relevance", null, dmp.getExistingDataRelevance());
			addTextElement(e3, "integration", null, dmp.getExistingDataIntegration());
			removeElementIfEmpty(e3);
			if (dmp.getUsedDataTypes() != null && !dmp.getUsedDataTypes().isEmpty() && pForm.getDataTypes() != null) {
				Element et = addAndReturnElement(e2, "researchMethods", null);
				dmp.getUsedDataTypes().forEach(method -> {
					if (method == 20) {
						addTextElement(et, "method", null, dmp.getOtherDataTypes());
					} else {
						FormTypesDTO type = pForm.getDataTypes().parallelStream().filter(dt -> (dt.getId() == method)).findFirst().orElse(null);
						if (type != null)
							addTextElement(et, "method", null, type.getNameEN());
					}
				});
			}
			addTextElement(e2, "reproducibility", null, dmp.getDataReproducibility());
			e3 = addAndReturnElement(e2, "dataColl", null);
			if (dmp.getUsedCollectionModes() != null && !dmp.getUsedCollectionModes().isEmpty() && pForm.getCollectionModes() != null) {
				Element et = addAndReturnElement(e3, "collModes", null);
				dmp.getUsedCollectionModes().forEach(mode -> {
					if (mode == 1) {
						addTextElement(et, "collMode", null, dmp.getOtherCMIP());
					} else if (mode == 2) {
						addTextElement(et, "collMode", null, dmp.getOtherCMINP());
					} else {
						FormTypesDTO type = pForm.getCollectionModes().parallelStream().filter(dt -> (dt.getId() == mode)).findFirst().orElse(null);
						if (type != null)
							addTextElement(et, "collMode", null, type.getNameEN());
					}
				});
			}
			if (dmp.getMeasOccasions() != null && !dmp.getMeasOccasions().isEmpty())
				addTextElement(e3, "timeMeth", null, messageSource.getMessage("dmp.edit.measOccasions." + dmp.getMeasOccasions(), null, locale));
			removeElementIfEmpty(e3);
			e3 = addAndReturnElement(e2, "quality", null);
			addTextElement(e3, "collectorTraining", null, dmp.getReliabilityTraining());
			addTextElement(e3, "multipleMeasures", null, dmp.getMultipleMeasurements());
			addTextElement(e3, "other", null, dmp.getQualitityOther());
			removeElementIfEmpty(e3);
			addTextElement(e2, "fileFormat", null, dmp.getFileFormat());
			e3 = addAndReturnElement(e2, "storageReasons", null);
			addTextElement(e3, "workingCopy", null, dmp.isWorkingCopy());
			addTextElement(e3, "goodScientific", null, dmp.isGoodScientific());
			addTextElement(e3, "reproducibility", null, dmp.isSubsequentUse());
			addTextElement(e3, "obligations", null, dmp.isRequirements());
			addTextElement(e3, "bestPractice", null, dmp.isDocumentation());
			removeElementIfEmpty(e3);
			e3 = addAndReturnElement(e2, "dataSelection", null);
			addTextElement(e3, "dataSelectionPresent", null, dmp.isDataSelection());
			addTextElement(e3, "selectionTime", null, dmp.getSelectionTime());
			addTextElement(e3, "selectionProcedures", null, dmp.getSelectionResp());
			removeElementIfEmpty(e3);
			addTextElement(e2, "storageDuration", null, dmp.getStorageDuration());
			addTextElement(e2, "deletion", null, dmp.getDeleteProcedure());
			removeElementIfEmpty(e2);
			e2 = addAndReturnElement(e1, "metaData", null);
			if (dmp.getSelectedMetaPurposes() != null && !dmp.getSelectedMetaPurposes().isEmpty() && pForm.getMetaPurposes() != null) {
				Element et = addAndReturnElement(e2, "purposes", null);
				dmp.getSelectedMetaPurposes().forEach(purpose -> {
					FormTypesDTO type = pForm.getMetaPurposes().parallelStream().filter(dt -> (dt.getId() == purpose)).findFirst().orElse(null);
					if (type != null)
						addTextElement(et, "purpose", null, type.getNameEN());
				});
			}
			addTextElement(e2, "description", null, dmp.getMetaDescription());
			addTextElement(e2, "framework", null, dmp.getMetaFramework());
			addTextElement(e2, "generation", null, dmp.getMetaGeneration());
			addTextElement(e2, "monitor", null, dmp.getMetaMonitor());
			addTextElement(e2, "format", null, dmp.getMetaFormat());
			removeElementIfEmpty(e2);
			e2 = addAndReturnElement(e1, "sharing", null);
			addTextElement(e2, "obligation", null, dmp.isReleaseObligation());
			addTextElement(e2, "expectedUsage", null, dmp.getExpectedUsage());
			e3 = addAndReturnElement(e2, "publStrategy", null);
			if (dmp.getPublStrategy() != null && !dmp.getPublStrategy().isEmpty()) {
				addTextElement(e3, "strategy", null, messageSource.getMessage("dmp.edit.publStrategy." + dmp.getPublStrategy(), null, locale));
				if (dmp.getPublStrategy() != null)
					if (dmp.getPublStrategy().trim().equals("nopubl") && dmp.getNoAccessReason() != null && !dmp.getNoAccessReason().isEmpty()) {
						addTextElement(e3, "noAccessReason", null, dmp.getNoAccessReason().equals("other") ? dmp.getNoAccessReasonOther()
						    : messageSource.getMessage("dmp.edit.noAccessReason." + dmp.getNoAccessReason(), null, locale));
					} else if (dmp.getPublStrategy().trim().equals("author")) {
						Element e4 = addAndReturnElement(e3, "onDemand", null);
						addTextElement(e4, "accessReasonAuthor", null, dmp.getAccessReasonAuthor());
						addTextElement(e4, "searchableData", null, dmp.getSearchableData());
						removeElementIfEmpty(e4);
					} else if (dmp.getPublStrategy().trim().equals("repository")) {
						Element e4 = addAndReturnElement(e3, "deposit", null);
						addTextElement(e4, "depositName", null, dmp.getDepositName());
						addTextElement(e4, "searchableData", null, dmp.getSearchableData());
						if (dmp.getUsedPID() != null && !dmp.getUsedPID().isEmpty())
							addTextElement(e4, "usedPID", null, dmp.getUsedPID().equals("other") ? dmp.getUsedPIDTxt()
							    : messageSource.getMessage("dmp.edit.usedPID." + dmp.getUsedPID(), null, locale));
						addTextElement(e4, "transferTime", null, dmp.getTransferTime());
						addTextElement(e4, "sensitiveData", null, dmp.getSensitiveData());
						Element e5 = addAndReturnElement(e4, "access", null);
						addTextElement(e5, "embargo", null, dmp.getInitialUsage());
						addTextElement(e5, "restriction", null, dmp.getUsageRestriction());
						addTextElement(e5, "accessCosts", null, dmp.isAccessCosts());
						removeElementIfEmpty(e5);
						e5 = addAndReturnElement(e4, "responsibilities", null);
						addTextElement(e5, "clarifiedRights", null, dmp.isClarifiedRights());
						addTextElement(e5, "acquisitionAgreement", null, dmp.isAcquisitionAgreement());
						removeElementIfEmpty(e4);
					}
				removeElementIfEmpty(e3);
			}
			removeElementIfEmpty(e2);
			e2 = addAndReturnElement(e1, "infrastructure", null);
			addTextElement(e2, "responsible", null, dmp.getStorageResponsible());
			addTextElement(e2, "namingCon", null, dmp.getNamingCon());
			addTextElement(e2, "storageLocation", null, dmp.getStoragePlaces());
			addTextElement(e2, "backups", null, dmp.getStorageBackups());
			addTextElement(e2, "transfer", null, dmp.getStorageTransfer());
			addTextElement(e2, "expectedSize", null, dmp.getStorageExpectedSize());
			e3 = addAndReturnElement(e2, "technicalRequirements", null);
			addTextElement(e3, "present", null, dmp.isStorageRequirements());
			if (dmp.isStorageRequirements())
				addTextElement(e3, "desc", null, dmp.getStorageRequirementsTxt());
			removeElementIfEmpty(e3);
			e3 = addAndReturnElement(e2, "successionPlan", null);
			addTextElement(e3, "present", null, dmp.isStorageSuccession());
			if (dmp.isStorageSuccession())
				addTextElement(e3, "desc", null, dmp.getStorageSuccessionTxt());
			removeElementIfEmpty(e3);
			removeElementIfEmpty(e2);
			e2 = addAndReturnElement(e1, "organization", null);
			if (dmp.getFrameworkNationality() != null && !dmp.getFrameworkNationality().isEmpty()) {
				e3 = addAndReturnElement(e2, "nationalFramework", null);
				addTextElement(e3, "present", null, dmp.isStorageSuccession());
				if (dmp.getFrameworkNationality().equals("international"))
					addTextElement(e3, "desc", null, dmp.getFrameworkNationalityTxt());
				removeElementIfEmpty(e3);
			}
			addTextElement(e2, "responsible", null, dmp.getResponsibleUnit());
			addTextElement(e2, "contributor", null, dmp.getInvolvedInstitutions());
			e3 = addAndReturnElement(e2, "workPlan", null);
			Element e4 = addAndReturnElement(e3, "contributions", null);
			addTextElement(e4, "informed", null, dmp.isInvolvedInformed());
			if (dmp.isInvolvedInformed()) {
				addTextElement(e4, "defined", null, dmp.isContributionsDefined());
				if (dmp.isContributionsDefined()) {
					addTextElement(e4, "desc", null, dmp.getContributionsDefinedTxt());
					addTextElement(e4, "givenConsent", null, dmp.isGivenConsent());
				}
			}
			removeElementIfEmpty(e4);
			e4 = addAndReturnElement(e3, "workflow", null);
			addTextElement(e4, "described", null, dmp.isManagementWorkflow());
			if (dmp.isManagementWorkflow())
				addTextElement(e4, "desc", null, dmp.getManagementWorkflowTxt());
			removeElementIfEmpty(e4);
			e4 = addAndReturnElement(e3, "staff", null);
			addTextElement(e4, "described", null, dmp.isStaffDescription());
			if (dmp.isStaffDescription())
				addTextElement(e4, "desc", null, dmp.getStaffDescriptionTxt());
			removeElementIfEmpty(e4);
			addTextElement(e3, "adherence", null, dmp.getPlanningAdherence());
			removeElementIfEmpty(e3);
			addTextElement(e2, "policies", null, dmp.getFunderRequirements());
			removeElementIfEmpty(e2);
			e2 = addAndReturnElement(e1, "ethical", null);
			e3 = addAndReturnElement(e2, "persData", null);
			addTextElement(e3, "persDataColl", null, dmp.isDataProtection());
			if (dmp.isDataProtection())
				addTextElement(e3, "persDataRequ", null, dmp.getProtectionRequirements());
			removeElementIfEmpty(e3);
			e3 = addAndReturnElement(e2, "consentObtained", null);
			addTextElement(e3, "present", null, dmp.isConsentObtained());
			if (dmp.isConsentObtained()) {
				addTextElement(e3, "sharingConsidered", null, dmp.isSharingConsidered());
			} else {
				addTextElement(e3, "desc", null, dmp.getConsentObtainedTxt());
			}
			removeElementIfEmpty(e3);
			e3 = addAndReturnElement(e2, "irb", null);
			addTextElement(e3, "present", null, dmp.isIrbApproval());
			if (dmp.isIrbApproval())
				addTextElement(e3, "desc", null, dmp.getIrbApprovalTxt());
			removeElementIfEmpty(e3);
			e3 = addAndReturnElement(e2, "otherSensitiveData", null);
			addTextElement(e3, "present", null, dmp.isSensitiveDataIncluded());
			if (dmp.isSensitiveDataIncluded())
				addTextElement(e3, "desc", null, dmp.getSensitiveDataIncludedTxt());
			removeElementIfEmpty(e3);
			e3 = addAndReturnElement(e2, "copyright", null);
			e4 = addAndReturnElement(e3, "ownCopyrights", null);
			addTextElement(e4, "present", null, dmp.isInternalCopyright());
			if (dmp.isInternalCopyright())
				addTextElement(e4, "desc", null, dmp.getInternalCopyrightTxt());
			removeElementIfEmpty(e4);
			e4 = addAndReturnElement(e3, "thirdPartyRights", null);
			addTextElement(e4, "present", null, dmp.isExternalCopyright());
			if (dmp.isExternalCopyright())
				addTextElement(e4, "desc", null, dmp.getExternalCopyrightTxt());
			removeElementIfEmpty(e4);
			removeElementIfEmpty(e3);
			removeElementIfEmpty(e2);
			if (dmp.getSpecificCosts() != null && !dmp.getSpecificCosts().isEmpty()) {
				e2 = addAndReturnElement(e1, "costs", null);
				addTextElement(e2, "costing", null, messageSource.getMessage("dmp.edit.specificCosts." + dmp.getSpecificCosts(), null, locale));
				if (!dmp.getSpecificCosts().equals("no")) {
					addTextElement(e2, "costingDescr", null, dmp.getSpecificCostsTxt());
					addTextElement(e2, "costAssumption", null, dmp.getBearCost());
				}
				removeElementIfEmpty(e2);

			}
			removeElementIfEmpty(e1);
		}
		if (pForm.getFiles() != null && !pForm.getFiles().isEmpty()) {
			Element e1 = addAndReturnElement(dmpElement, "otherMat", this.xmlns_dw);
			pForm.getFiles().forEach(file -> {
				Element e2 = addAndReturnElement(e1, "file", null);
				addTextElement(e2, "name", null, file.getFileName());
				addTextElement(e2, "size", null, file.getFileSize());
				addTextElement(e2, "contentType", null, file.getContentType());
				// TODO addTextElement(e2, "description", null, file.getDe); No Description at the moment!!!
				addTextElement(e2, "uploadDate", null, file.getUploadDate());
				addTextElement(e2, "md5", null, file.getMd5checksum());
				addTextElement(e2, "sha1", null, file.getSha1Checksum());
				addTextElement(e2, "sha256", null, file.getSha256Checksum());
				removeElementIfEmpty(e2);
			});
			removeElementIfEmpty(e1);
		}
	}

	private void removeElementIfEmpty(Element e) {
		if (!e.hasContent()) {
			e.detach();
		}
	}

	/**
	 * 
	 * @param sForm
	 * @param fileHash
	 * @param root
	 * @param user
	 * @param locale
	 * @throws Exception
	 */
	private void createCodeBookDDI(final StudyForm sForm, final FileDTO fileHash, final Element root, final UserDTO user, final Locale locale)
	    throws Exception {
		if (sForm != null && sForm.getRecord() != null) {
			RecordDTO record = sForm.getRecord();
			createDocDscr(messageSource.getMessage("export.ddi.rec.title", new Object[] { record.getRecordName() }, locale), null, null, user, root,
			    locale);
			createSharedStudyMeta(locale, sForm.getStudy(), addAndReturnElement(addAndReturnElement(root, "stdyDscr", this.xmlns_ddi), "citation", null));
			if (record.getVersionId() > 0) {
				Element fileDscr = addAndReturnElement(root, "fileDscr", this.xmlns_ddi);
				Element fileTxt = addAndReturnElement(fileDscr, "fileTxt", null);
				addTextElement(fileTxt, "fileName", null, record.getFileName());
				Element fileCitation = addAndReturnElement(fileTxt, "fileCitation", null);
				addTextElement(addAndReturnElement(fileCitation, "titlStmt", null), "titl", null, record.getRecordName());
				Element verStmt = addAndReturnElement(fileCitation, "verStmt", null);
				addTextElement(verStmt, "version", null,
				    record.getChanged() != null ? record.getChanged().toString() : record.getCreated() != null ? record.getCreated().toString() : "");
				addTextElement(verStmt, "verResp", null, record.getChangedBy());
				addTextElement(verStmt, "notes", null, record.getChangeLog());
				if (fileHash != null) {
					Element dataFingerprint = null;
					if (fileHash.getMd5checksum() != null && !fileHash.getMd5checksum().isEmpty()) {
						dataFingerprint = addAndReturnElement(fileTxt, "dataFingerprint", null);
						dataFingerprint.addAttribute("type", "dataFile");
						addTextElement(dataFingerprint, "digitalFingerprintValue", null, fileHash.getMd5checksum());
						addTextElement(dataFingerprint, "algorithmSpecification", null, "MD5");
					}
					if (fileHash.getSha1Checksum() != null && !fileHash.getSha1Checksum().isEmpty()) {
						dataFingerprint = addAndReturnElement(fileTxt, "dataFingerprint", null);
						dataFingerprint.addAttribute("type", "dataFile");
						addTextElement(dataFingerprint, "digitalFingerprintValue", null, fileHash.getSha1Checksum());
						addTextElement(dataFingerprint, "algorithmSpecification", null, "SHA1");
					}
					if (fileHash.getSha256Checksum() != null && !fileHash.getSha256Checksum().isEmpty()) {
						dataFingerprint = addAndReturnElement(fileTxt, "dataFingerprint", null);
						dataFingerprint.addAttribute("type", "dataFile");
						addTextElement(dataFingerprint, "digitalFingerprintValue", null, fileHash.getSha256Checksum());
						addTextElement(dataFingerprint, "algorithmSpecification", null, "SHA256");
					}
				}
				addTextElement(fileTxt, "fileCont", null, record.getDescription());
				addAndReturnElement(fileTxt, "fileStrc", null).addAttribute("type", "rectangular");
				addTextElement(fileTxt, "format", null, "comma-delimited");
			} else {
				addTextElement(addAndReturnElement(root, "fileDscr", this.xmlns_ddi), "notes", null,
				    messageSource.getMessage("export.ddi.rec.noVers", null, locale));
			}
			if (record.getVariables() != null && !record.getVariables().isEmpty()) {
				Element dataDscr = addAndReturnElement(root, "dataDscr", this.xmlns_ddi);
				for (SPSSVarDTO variable : record.getVariables()) {
					Element var = addAndReturnElement(dataDscr, "var", null);
					var.addAttribute("name", variable.getName());
					var.addAttribute("dcml", String.valueOf(variable.getDecimals()));
					var.addAttribute("representationType", "other");
					var.addAttribute("otherRepresentationType", messageSource.getMessage("export.ddi.rec.SPSS", null, locale));
					addAndReturnElement(var, "location", null).addAttribute("StartPos", String.valueOf(variable.getPosition()))
					    .addAttribute("EndPos", String.valueOf(variable.getPosition())).addAttribute("width", String.valueOf(variable.getWidth()));
					addTextElement(var, "labl", null, variable.getLabel());
					if (variable.getDw_attributes() != null)
						variable.getDw_attributes().forEach(attr -> {
							if (attr.getLabel().equals("dw_itemtext")) {
								addTextElement(var, "qstn", null, attr.getValue());
							}
						});
					Element invalrng = null;
					if (variable.getMissingFormat().equals(SPSSMissing.SPSS_MISS_RANGE)) {
						invalrng = addAndReturnElement(var, "invalrng", null);
						addAndReturnElement(invalrng, "range", null).addAttribute("min", variable.getMissingVal1()).addAttribute("max",
						    variable.getMissingVal2());
					} else if (variable.getMissingFormat().equals(SPSSMissing.SPSS_MISS_RANGEANDVAL)) {
						invalrng = addAndReturnElement(var, "invalrng", null);
						addAndReturnElement(invalrng, "range", null).addAttribute("min", variable.getMissingVal1()).addAttribute("max",
						    variable.getMissingVal2());
						addAndReturnElement(invalrng, "item", null).addAttribute("VALUE", variable.getMissingVal3());
					} else if (variable.getMissingFormat().equals(SPSSMissing.SPSS_ONE_MISSVAL)) {
						invalrng = addAndReturnElement(var, "invalrng", null);
						addAndReturnElement(invalrng, "item", null).addAttribute("VALUE", variable.getMissingVal1());
					} else if (variable.getMissingFormat().equals(SPSSMissing.SPSS_TWO_MISSVAL)) {
						invalrng = addAndReturnElement(var, "invalrng", null);
						addAndReturnElement(invalrng, "item", null).addAttribute("VALUE", variable.getMissingVal1());
						addAndReturnElement(invalrng, "item", null).addAttribute("VALUE", variable.getMissingVal2());
					} else if (variable.getMissingFormat().equals(SPSSMissing.SPSS_THREE_MISSVAL)) {
						invalrng = addAndReturnElement(var, "invalrng", null);
						addAndReturnElement(invalrng, "item", null).addAttribute("VALUE", variable.getMissingVal1());
						addAndReturnElement(invalrng, "item", null).addAttribute("VALUE", variable.getMissingVal2());
						addAndReturnElement(invalrng, "item", null).addAttribute("VALUE", variable.getMissingVal3());
					}
					if (variable.getValues() != null && !variable.getValues().isEmpty()) {
						for (SPSSValueLabelDTO value : variable.getValues()) {
							Element catgry = addAndReturnElement(var, "catgry", null);
							addTextElement(catgry, "catValu", null, value.getValue());
							addTextElement(catgry, "labl", null, value.getLabel());
						}
					}
					if ((variable.getDw_attributes() != null && variable.getDw_attributes().size() > 0)
					    || (variable.getAttributes() != null && variable.getAttributes().size() > 0)) {
						Element list = addAndReturnElement(addAndReturnElement(var, "concept", null), "list", null);
						if (variable.getDw_attributes() != null && !variable.getDw_attributes().isEmpty()) {
							variable.getDw_attributes().forEach(attr -> {
								switch (attr.getLabel()) {
								case "dw_construct":
									setListItems(list, messageSource.getMessage("dataset.import.report.codebook.construct", null, locale), attr.getValue());
									break;
								case "dw_instrument":
									setListItems(list, messageSource.getMessage("dataset.import.report.codebook.instrument", null, locale), attr.getValue());
									break;
								case "dw_measocc":
									setListItems(list, messageSource.getMessage("dataset.import.report.codebook.measocc", null, locale), attr.getValue());
									break;
								case "dw_filtervar":
									setListItems(list, messageSource.getMessage("dataset.import.report.codebook.filtervar", null, locale),
									    (attr.getValue() != null && attr.getValue().equals("1") ? true : false));
									break;
								}
							});
						}
						if (variable.getAttributes() != null && !variable.getAttributes().isEmpty()) {
							variable.getAttributes().forEach(attr -> {
								setListItems(list, attr.getLabel(), attr.getValue());
							});
						}
					}
					Element list = addAndReturnElement(addAndReturnElement(var, "varFormat", null), "list", null);
					setListItems(list, messageSource.getMessage("export.ddi.rec.SPSS", null, locale),
					    messageSource.getMessage("spss.type." + (variable.getType() != null ? variable.getType().name() : "SPSS_UNKNOWN"), null, locale));
					setListItems(list, messageSource.getMessage("export.ddi.rec.columns", null, locale), variable.getColumns());
					setListItems(list, messageSource.getMessage("export.ddi.rec.aligment", null, locale), messageSource
					    .getMessage("spss.aligment." + (variable.getAligment() != null ? variable.getAligment().name() : "SPSS_UNKNOWN"), null, locale));
					setListItems(list, messageSource.getMessage("export.ddi.rec.measureLevel", null, locale), messageSource.getMessage(
					    "spss.measureLevel." + (variable.getMeasureLevel() != null ? variable.getMeasureLevel().name() : "SPSS_UNKNOWN"), null, locale));
					setListItems(list, messageSource.getMessage("export.ddi.rec.role", null, locale),
					    messageSource.getMessage("spss.role." + (variable.getRole() != null ? variable.getRole().name() : "SPSS_UNKNOWN"), null, locale));
				}

			} else {

			}

		}

	}

	/**
	 * 
	 * @param files
	 * @param cUploader
	 * @param root
	 * @param lvl
	 * @param locale
	 * @throws Exception
	 */
	private void createOtherMatDDI(final List<FileDTO> files, final List<UserDTO> cUploader, final Element root, final String lvl, final Locale locale)
	    throws Exception {
		Element otherMat_root = addAndReturnElement(root, "otherMat", this.xmlns_ddi);
		otherMat_root.addAttribute("level", lvl);
		for (FileDTO file : files) {
			Element otherMat = addAndReturnElement(otherMat_root, "otherMat", this.xmlns_ddi);
			otherMat.addAttribute("level", "unknown");
			if (file.getContentType() != null && !file.getContentType().isEmpty())
				otherMat.addAttribute("type", file.getContentType());
			addTextElement(otherMat, "labl", null, file.getFileName());
			Element list = addAndReturnElement(addAndReturnElement(otherMat, "notes", this.xmlns_ddi), "list", null);
			setListItems(list, messageSource.getMessage("export.ddi.otherMat.fileSize", null, locale), file.getFileSize());
			setListItems(list, messageSource.getMessage("export.ddi.otherMat.uploadDate", null, locale), file.getUploadDate());
			Element itm = addAndReturnElement(list, "itm", null);
			addTextElement(itm, "label", null, messageSource.getMessage("export.ddi.otherMat.uploadBy", null, locale));
			if (cUploader != null && !cUploader.isEmpty()) {
				for (UserDTO uploader : cUploader)
					if (uploader.getId() == file.getUserId()) {
						Element list_user = addAndReturnElement(itm, "list", null);
						setListItems(list_user, messageSource.getMessage("export.ddi.study.title", null, locale), uploader.getTitle());
						setListItems(list_user, messageSource.getMessage("export.ddi.study.orcid", null, locale), uploader.getOrcid());
						setListItems(list_user, messageSource.getMessage("export.ddi.study.firstName", null, locale), uploader.getFirstName());
						setListItems(list_user, messageSource.getMessage("export.ddi.study.lastName", null, locale), uploader.getLastName());
						setListItems(list_user, messageSource.getMessage("export.ddi.study.mail", null, locale), uploader.getEmail());
						break;
					}
			}
			setListItems(list, messageSource.getMessage("export.ddi.otherMat.sha256", null, locale), file.getSha256Checksum());
			setListItems(list, messageSource.getMessage("export.ddi.otherMat.sha1", null, locale), file.getSha1Checksum());
			setListItems(list, messageSource.getMessage("export.ddi.otherMat.md5", null, locale), file.getMd5checksum());
		}
	}

	/**
	 * 
	 * @param sForm
	 * @param user
	 * @param root
	 * @param locale
	 * @throws Exception
	 */
	private void createStudyDDI(final StudyForm sForm, final UserDTO user, final Element root, final Locale locale) throws Exception {
		final StudyDTO study = sForm.getStudy();
		final ProjectDTO project = sForm.getProject();
		// docDscr start
		createDocDscr(study.getTitle(), study.getTransTitle(), study.getInternalID(), user, root, locale);
		// stdyDscr start
		Element stdyDscr = addAndReturnElement(root, "stdyDscr", this.xmlns_ddi);
		Element citation = addAndReturnElement(stdyDscr, "citation", null);
		createSharedStudyMeta(locale, study, citation);
		Element prodStmt = addAndReturnElement(citation, "prodStmt", null);
		Element copyright = addAndReturnElement(prodStmt, "copyright", null);
		Element list = addAndReturnElement(copyright, "list", null);
		setListItems(list, messageSource.getMessage("study.copyright", null, locale),
		    messageSource.getMessage(study.isCopyright() ? "export.boolean.true" : "export.boolean.false", null, locale));
		if (study.isCopyright())
			setListItems(list, messageSource.getMessage("study.copyrightHolder", null, locale), study.getCopyrightHolder());
		setListItems(list, messageSource.getMessage("study.thirdParty", null, locale),
		    messageSource.getMessage(study.isThirdParty() ? "export.boolean.true" : "export.boolean.false", null, locale));
		if (study.isThirdParty())
			setListItems(list, messageSource.getMessage("study.thirdPartyHolder", null, locale), study.getThirdPartyHolder());
		if (study.getSoftware() != null && !study.getSoftware().isEmpty()) {
			Element software = addAndReturnElement(prodStmt, "software", null);
			list = addAndReturnElement(software, "list", null);
			for (StudyListTypesDTO sw : study.getSoftware()) {
				addTextElement(list, "itm", null, sw.getText());
			}
		}
		addTextElement(prodStmt, "fundAg", null, project.getFunding());
		addTextElement(prodStmt, "grantNo", null, project.getGrantNumber());
		Element stdyInfo = addAndReturnElement(stdyDscr, "stdyInfo", null);
		if ((study.getsAbstract() != null && !study.getsAbstract().isEmpty())
		    || (study.getsAbstractTrans() != null && !study.getsAbstractTrans().isEmpty())) {
			Element abstr = addAndReturnElement(stdyInfo, "abstract", null);
			list = addAndReturnElement(abstr, "list", null);
			setListItems(list, messageSource.getMessage("study.sAbstract", null, locale), study.getsAbstract());
			setListItems(list, messageSource.getMessage("study.sAbstractTrans", null, locale), study.getsAbstractTrans());
		}
		Element sumDscr = addAndReturnElement(stdyInfo, "sumDscr", null);
		addTextElement(sumDscr, "collDate", null, study.getCollStart(), new SimpleEntry<String, String>("event", "start"));
		addTextElement(sumDscr, "collDate", null, study.getCollEnd(), new SimpleEntry<String, String>("event", "end"));
		addTextElement(sumDscr, "nation", null, study.getCountry());
		if ((study.getCity() != null && !study.getCity().isEmpty()) || (study.getRegion() != null && !study.getRegion().isEmpty())) {
			list = addAndReturnElement(addAndReturnElement(sumDscr, "geogCover", null), "list", null);
			setListItems(list, messageSource.getMessage("study.city", null, locale), study.getCity());
			setListItems(list, messageSource.getMessage("study.region", null, locale), study.getRegion());
		}
		if (study.getObsUnit() != null && !study.getObsUnit().isEmpty()) {
			Element anlyUnit = addAndReturnElement(sumDscr, "anlyUnit", null);
			addTextElement(anlyUnit, "concept", null, messageSource.getMessage("study.obsUnit", null, locale));
			addTextElement(anlyUnit, "txt", null, messageSource.getMessage("study.obsUnit." + study.getObsUnit().trim().toLowerCase(), null, locale));
			if (study.getObsUnit().trim().toLowerCase().equals("other")) {
				anlyUnit = addAndReturnElement(sumDscr, "anlyUnit", null);
				addTextElement(anlyUnit, "concept", null, messageSource.getMessage("export.ddi.obsUnit.other", null, locale));
				addTextElement(anlyUnit, "txt", null, study.getObsUnitOther());
			}

		}
		if (study.getEligibilities() != null && study.getEligibilities().isEmpty()) {
			Element universe = addAndReturnElement(sumDscr, "universe", null);
			addTextElement(universe, "concept", null, messageSource.getMessage("study.eligibilities", null, locale));
			list = addAndReturnElement(universe, "list", null);
			for (StudyListTypesDTO elig : study.getEligibilities()) {
				addTextElement(list, "itm", null, elig.getText());
			}
		}
		addUniverseWithContent(study.getPopulation(), messageSource.getMessage("study.population", null, locale), sumDscr);
		addUniverseWithContent(study.getSex(), messageSource.getMessage("export.ddi.sex", null, locale), sumDscr);
		addUniverseWithContent(study.getAge(), messageSource.getMessage("export.ddi.age", null, locale), sumDscr);
		addUniverseWithContent(study.getSpecGroups(), messageSource.getMessage("export.ddi.specGroups", null, locale), sumDscr);
		if (study.getUsedSourFormat() != null && !study.getUsedSourFormat().isEmpty()) {
			Element dataKind = addAndReturnElement(sumDscr, "dataKind", null);
			dataKind.addAttribute("type", messageSource.getMessage("study.usedSourFormat", null, locale));
			Element listPar = addAndReturnElement(dataKind, "list", null);
			for (Integer elig : study.getUsedSourFormat()) {
				sForm.getSourFormat().parallelStream().forEach(s -> {
					if (s.getId() == elig) {
						if (elig == 41)
							addTextElement(listPar, "itm", null, study.getOtherSourFormat());
						else
							addTextElement(listPar, "itm", null, locale.equals(Locale.GERMAN) ? s.getNameDE() : s.getNameEN());
					}
				});
			}
		}
		if (study.isExperimentalIntervention() || study.isSurveyIntervention() || study.isTestIntervention()) {
			Element dataKind = addAndReturnElement(sumDscr, "dataKind", null);
			dataKind.addAttribute("type", messageSource.getMessage("study.intervention", null, locale));
			list = addAndReturnElement(dataKind, "list", null);
			if (study.isSurveyIntervention()) {
				addTextElement(list, "itm", null, messageSource.getMessage("study.intervention.survey", null, locale));
			}
			if (study.isExperimentalIntervention()) {
				addTextElement(list, "itm", null, messageSource.getMessage("study.intervention.experimental", null, locale));
			}
			if (study.isTestIntervention()) {
				addTextElement(list, "itm", null, messageSource.getMessage("study.intervention.test", null, locale));
			}
		}
		Element notes;
		if (study.getObjectives() != null && !study.getObjectives().isEmpty()) {
			notes = addAndReturnElement(stdyInfo, "notes", null);
			notes.addAttribute("type", messageSource.getMessage("study.objectives", null, locale));
			for (StudyListTypesDTO objective : study.getObjectives()) {
				list = addAndReturnElement(notes, "list", null);
				setListItems(list, messageSource.getMessage("export.ddi.objective.text", null, locale), objective.getText());
				setListItems(list, messageSource.getMessage("export.ddi.objective.type", null, locale),
				    (objective.getObjectivetype() != null && !objective.getObjectivetype().isEmpty())
				        ? messageSource.getMessage("study.objectives." + objective.getObjectivetype().trim().toLowerCase(), null, locale)
				        : "");
			}
		}
		if (study.getConstructs() != null && !study.getConstructs().isEmpty()) {
			notes = addAndReturnElement(stdyInfo, "notes", null);
			notes.addAttribute("type", messageSource.getMessage("study.constructs", null, locale));
			for (StudyConstructDTO construct : study.getConstructs()) {
				list = addAndReturnElement(notes, "list", null);
				setListItems(list, messageSource.getMessage("study.constructs.name", null, locale), construct.getName());
				if (construct.getType() != null && construct.getType().equals("OTHER")) {
					setListItems(list, messageSource.getMessage("study.constructs.type", null, locale), construct.getOther());
				} else {
					setListItems(list, messageSource.getMessage("study.constructs.type", null, locale),
					    (construct.getType() != null && !construct.getType().isEmpty())
					        ? messageSource.getMessage("study.constructs.type." + construct.getType().trim().toLowerCase(), null, locale)
					        : "");
				}
			}
		}
		if (study.getRelTheorys() != null && !study.getRelTheorys().isEmpty()) {
			notes = addAndReturnElement(stdyInfo, "notes", null);
			notes.addAttribute("type", messageSource.getMessage("study.relTheorys", null, locale));
			list = addAndReturnElement(notes, "list", null);
			for (StudyListTypesDTO realTh : study.getRelTheorys()) {
				addTextElement(list, "itm", null, realTh.getText());
			}
		}
		notes = addAndReturnElement(stdyInfo, "notes", null);
		notes.addAttribute("type", messageSource.getMessage("study.irb", null, locale));
		list = addAndReturnElement(notes, "list", null);
		setListItems(list, messageSource.getMessage("study.irb", null, locale), study.isIrb());
		if (study.isIrb())
			setListItems(list, messageSource.getMessage("study.irbName", null, locale), study.getIrbName());
		notes = addAndReturnElement(stdyInfo, "notes", null);
		notes.addAttribute("type", messageSource.getMessage("study.consent", null, locale));
		list = addAndReturnElement(notes, "list", null);
		setListItems(list, messageSource.getMessage("export.ddi.consens", null, locale), study.isConsent());
		if (study.isConsent())
			setListItems(list, messageSource.getMessage("study.consentShare", null, locale), study.isConsentShare());
		if (study.getConflInterests() != null && !study.getConflInterests().isEmpty()) {
			notes = addAndReturnElement(stdyInfo, "notes", null);
			notes.addAttribute("type", messageSource.getMessage("study.conflInterests", null, locale));
			list = addAndReturnElement(notes, "list", null);
			for (StudyListTypesDTO confl : study.getConflInterests()) {
				addTextElement(list, "itm", null, confl.getText());
			}
		}
		Element method = addAndReturnElement(stdyDscr, "method", null);
		Element dataColl = addAndReturnElement(method, "dataColl", null);
		if (study.getRepMeasures() != null && !study.getRepMeasures().isEmpty()) {
			Element timeMeth = addAndReturnElement(dataColl, "timeMeth", null);
			addTextElement(timeMeth, "concept", null, messageSource.getMessage("study.repMeasures", null, locale));
			addTextElement(timeMeth, "txt", null,
			    messageSource.getMessage("study.repMeasures." + study.getRepMeasures().trim().toLowerCase(), null, locale));
			if (study.getRepMeasures().trim().equals("MULTIPLE")) {
				timeMeth = addAndReturnElement(dataColl, "timeMeth", null);
				addTextElement(timeMeth, "concept", null, messageSource.getMessage("study.timeDim", null, locale));
				addTextElement(timeMeth, "txt", null, study.getTimeDim());
			}
		}
		if (study.getMeasOcc() != null && !study.getMeasOcc().isEmpty()) {
			Element timeMeth = addAndReturnElement(dataColl, "timeMeth", null);
			addTextElement(timeMeth, "concept", null, messageSource.getMessage("study.measOcc", null, locale));
			for (StudyListTypesDTO occ : study.getMeasOcc()) {
				list = addAndReturnElement(timeMeth, "list", null);
				setListItems(list, messageSource.getMessage("study.measOcc.time", null, locale), occ.getText());
				setListItems(list, messageSource.getMessage("study.measOcc.dim", null, locale), occ.isTimetable());
				setListItems(list, messageSource.getMessage("study.measOcc.sort", null, locale), occ.getSort());
			}
		}
		if (study.getResponsibility() != null && !study.getResponsibility().isEmpty()) {
			if (study.getResponsibility().trim().equals("OTHER"))
				addTextElement(dataColl, "dataCollector", null, study.getResponsibilityOther(),
				    new SimpleEntry<String, String>("role", messageSource.getMessage("study.responsibility", null, locale)));
			else
				addTextElement(dataColl, "dataCollector", null,
				    messageSource.getMessage("study.responsibility." + study.getResponsibility().toLowerCase(), null, locale),
				    new SimpleEntry<String, String>("role", messageSource.getMessage("study.responsibility", null, locale)));
		}
		if (study.getSampleSize() != null && !study.getSampleSize().isEmpty()) {
			Element sampProc = addAndReturnElement(dataColl, "sampProc", null);
			addTextElement(sampProc, "concept", null, messageSource.getMessage("export.ddi.actsampsize", null, locale));
			addTextElement(sampProc, "txt", null, study.getSampleSize());
		}
		if (study.getSampMethod() != null && !study.getSampMethod().isEmpty()) {
			Element sampProc = addAndReturnElement(dataColl, "sampProc", null);
			addTextElement(sampProc, "concept", null, messageSource.getMessage("study.sampMethod", null, locale));
			if (study.getSampMethod().trim().toLowerCase().equals("other"))
				addTextElement(sampProc, "txt", null, study.getSampMethodOther());
			else
				addTextElement(sampProc, "txt", null,
				    messageSource.getMessage("study.sampMethod." + study.getSampMethod().trim().toLowerCase(), null, locale));
		}
		if ((study.getSampleSize() != null && !study.getSampleSize().isEmpty())
		    || (study.getPowerAnalysis() != null && !study.getPowerAnalysis().isEmpty())) {
			Element targetSampleSize = addAndReturnElement(dataColl, "targetSampleSize", null);
			try {
				int sampsize = Integer.parseInt(study.getSampleSize());
				addTextElement(targetSampleSize, "sampleSize", null, sampsize);
			} catch (Exception e) {
			}
			addTextElement(targetSampleSize, "sampleSizeFormula", null, study.getPowerAnalysis());
		}
		if (study.getUsedCollectionModes() != null && !study.getUsedCollectionModes().isEmpty() && sForm.getCollectionModes() != null) {
			List<String> cmip = new ArrayList<>();
			List<String> cminp = new ArrayList<>();
			for (int collId : study.getUsedCollectionModes()) {
				for (FormTypesDTO collMode : sForm.getCollectionModes()) {
					if (collMode.getId() == collId) {
						switch (collId) {
						case 1:
							if (study.getOtherCMIP() != null && !study.getOtherCMIP().isEmpty()) {
								cmip.add(study.getOtherCMIP());
							}
							break;
						case 2:
							if (study.getOtherCMINP() != null && !study.getOtherCMINP().isEmpty()) {
								cminp.add(study.getOtherCMINP());
							}
							break;
						default:
							if (collMode.isInvestPresent())
								cmip.add(locale.equals(Locale.GERMAN) ? collMode.getNameDE() : collMode.getNameEN());
							else
								cminp.add(locale.equals(Locale.GERMAN) ? collMode.getNameDE() : collMode.getNameEN());
							break;
						}
					}
				}
			}
			if (!cmip.isEmpty()) {
				Element collMode = addAndReturnElement(dataColl, "collMode", null);
				addTextElement(collMode, "concept", null, messageSource.getMessage("study.usedCollectionModes.present", null, locale));
				Element list_t = addAndReturnElement(collMode, "list", null);
				cmip.forEach(s -> {
					addTextElement(list_t, "itm", null, s);
				});
			}
			if (!cminp.isEmpty()) {
				Element collMode = addAndReturnElement(dataColl, "collMode", null);
				addTextElement(collMode, "concept", null, messageSource.getMessage("study.usedCollectionModes.not.present", null, locale));
				Element list_t = addAndReturnElement(collMode, "list", null);
				cminp.forEach(s -> {
					addTextElement(list_t, "itm", null, s);
				});
			}
		}
		if ((study.getInterTypeExp() != null && !study.getInterTypeExp().isEmpty())
		    || (study.getInterTypeDes() != null && !study.getInterTypeDes().isEmpty())
		    || (study.getInterTypeLab() != null && !study.getInterTypeLab().isEmpty())
		    || (study.getRandomization() != null && !study.getRandomization().isEmpty())
		    || (study.getInterArms() != null && !study.getInterArms().isEmpty())) {
			Element collMode = addAndReturnElement(dataColl, "collMode", null);
			addTextElement(collMode, "concept", null, messageSource.getMessage("study.interTypeExp", null, locale));
			list = addAndReturnElement(collMode, "list", null);
			if (study.getInterTypeExp() != null && !study.getInterTypeExp().isEmpty())
				addTextElement(list, "itm", null,
				    messageSource.getMessage("study.interTypeExp." + study.getInterTypeExp().trim().toLowerCase(), null, locale));
			if (study.getInterTypeDes() != null && !study.getInterTypeDes().isEmpty())
				addTextElement(list, "itm", null,
				    messageSource.getMessage("study.interTypeDes." + study.getInterTypeDes().trim().toLowerCase(), null, locale));
			if (study.getInterTypeLab() != null && !study.getInterTypeLab().isEmpty())
				addTextElement(list, "itm", null,
				    messageSource.getMessage("study.interTypeLab." + study.getInterTypeLab().trim().toLowerCase(), null, locale));
			if (study.getRandomization() != null && !study.getRandomization().isEmpty())
				addTextElement(list, "itm", null,
				    messageSource.getMessage("study.randomization." + study.getRandomization().trim().toLowerCase(), null, locale));
			if (study.getInterArms() != null && !study.getInterArms().isEmpty()) {
				Element itm = addAndReturnElement(list, "itm", null);
				addTextElement(itm, "label", null, messageSource.getMessage("study.interArms", null, locale));
				Element list_t = addAndReturnElement(itm, "list", null);
				study.getInterArms().forEach(s -> {
					addTextElement(list_t, "itm", null, s.getText());
				});
			}
		}
		if (study.getDescription() != null && !study.getDescription().isEmpty()) {
			Element collMode = addAndReturnElement(dataColl, "collMode", null);
			addTextElement(collMode, "concept", null, messageSource.getMessage("study.description", null, locale));
			addTextElement(collMode, "txt", null, study.getDescription());
		}
		if (study.isSurveyIntervention() && (study.getSurveyType() != null && !study.getSurveyType().isEmpty())) {
			addTextElement(dataColl, "resInstru", null,
			    messageSource.getMessage("study.surveyType." + study.getSurveyType().trim().toLowerCase(), null, locale),
			    new SimpleEntry<String, String>("type", messageSource.getMessage("study.surveyType", null, locale)));
		}
		if (study.getInstruments() != null && !study.getInstruments().isEmpty()) {
			Element resInstru = addAndReturnElement(dataColl, "resInstru", null);
			resInstru.addAttribute("type", messageSource.getMessage("study.instruments", null, locale));
			Element list_t = addAndReturnElement(resInstru, "list", null);
			study.getInstruments().forEach(instr -> {
				Element list_int = addAndReturnElement(addAndReturnElement(list_t, "itm", null), "list", null);
				setListItems(list_int, messageSource.getMessage("study.instruments.title", null, locale), instr.getTitle());
				setListItems(list_int, messageSource.getMessage("study.instruments.author", null, locale), instr.getAuthor());
				setListItems(list_int, messageSource.getMessage("study.instruments.citation", null, locale), instr.getCitation());
				setListItems(list_int, messageSource.getMessage("study.instruments.summary", null, locale), instr.getSummary());
				setListItems(list_int, messageSource.getMessage("study.instruments.theoHint", null, locale), instr.getTheoHint());
				setListItems(list_int, messageSource.getMessage("study.instruments.structure", null, locale), instr.getStructure());
				setListItems(list_int, messageSource.getMessage("study.instruments.construction", null, locale), instr.getConstruction());
				setListItems(list_int, messageSource.getMessage("study.instruments.objectivity", null, locale), instr.getObjectivity());
				setListItems(list_int, messageSource.getMessage("study.instruments.reliability", null, locale), instr.getReliability());
				setListItems(list_int, messageSource.getMessage("study.instruments.validity", null, locale), instr.getValidity());
				setListItems(list_int, messageSource.getMessage("study.instruments.norm", null, locale), instr.getNorm());
			});
		}
		if ((study.getMultilevel() != null && !study.getMultilevel().isEmpty()) || (study.getRecruiting() != null && !study.getRecruiting().isEmpty())) {
			list = addAndReturnElement(addAndReturnElement(dataColl, "collSitu", null), "list", null);
			setListItems(list, messageSource.getMessage("study.multilevel", null, locale), study.getMultilevel());
			setListItems(list, messageSource.getMessage("study.recruiting", null, locale), study.getRecruiting());
		}
		notes = addAndReturnElement(method, "notes", null);
		notes.addAttribute("type", messageSource.getMessage("export.ddi.type.persData", null, locale));
		list = addAndReturnElement(notes, "list", null);
		setListItems(list, messageSource.getMessage("study.persDataColl", null, locale), study.isPersDataColl());
		if (study.isPersDataColl() && study.getPersDataPres() != null && !study.getPersDataPres().isEmpty()) {
			setListItems(list, messageSource.getMessage("study.persDataPres", null, locale),
			    messageSource.getMessage("study.persDataPres." + study.getPersDataPres().trim().toLowerCase(), null, locale));
			if (study.getPersDataPres().trim().toLowerCase().equals("anonymous"))
				setListItems(list, messageSource.getMessage("study.anonymProc", null, locale), study.getAnonymProc());
		}
		Element anlyInfo = addAndReturnElement(method, "anlyInfo", null);
		addTextElement(anlyInfo, "respRate", null, study.getDataRerun());
		addTextElement(anlyInfo, "dataAppr", null, study.getQualInd(),
		    new SimpleEntry<String, String>("type", messageSource.getMessage("study.qualInd", null, locale)));
		addTextElement(anlyInfo, "dataAppr", null, study.getQualLim(),
		    new SimpleEntry<String, String>("type", messageSource.getMessage("study.qualLim", null, locale)));
		addTextElement(anlyInfo, "dataAppr", null, study.getSpecCirc(),
		    new SimpleEntry<String, String>("type", messageSource.getMessage("study.specCirc", null, locale)));
		if (study.getSourTrans() != null && !study.getSourTrans().isEmpty()) {
			Element dataProcessing = addAndReturnElement(method, "dataProcessing", null);
			dataProcessing.addAttribute("type", messageSource.getMessage("export.ddi.type.dataProc", null, locale));
			list = addAndReturnElement(dataProcessing, "list", null);
			setListItems(list, messageSource.getMessage("study.sourTrans", null, locale),
			    messageSource.getMessage("study.sourTrans." + study.getSourTrans().trim().toLowerCase(), null, locale));
			if (study.getSourTrans().trim().toLowerCase().equals("complex")) {
				setListItems(list, messageSource.getMessage("export.ddi.type.dataProc.other", null, locale), study.getOtherSourTrans());
			}
		}
		addTextElement(method, "dataProcessing", null, study.getTransDescr(),
		    new SimpleEntry<String, String>("type", messageSource.getMessage("study.transDescr", null, locale)));
		addTextElement(method, "dataProcessing", null, study.getMissings(),
		    new SimpleEntry<String, String>("type", messageSource.getMessage("study.missings", null, locale)));
		if (study.getCompleteSel() != null && !study.getCompleteSel().isEmpty()) {
			list = addAndReturnElement(
			    addAndReturnElement(addAndReturnElement(addAndReturnElement(stdyDscr, "dataAccs", null), "setAvail", null), "complete", null), "list",
			    null);
			setListItems(list, messageSource.getMessage("study.completeSel", null, locale),
			    messageSource.getMessage("study.completeSel." + study.getCompleteSel().trim().toLowerCase(), null, locale));
			if (study.getCompleteSel().trim().toLowerCase().equals("excerpt")) {
				setListItems(list, messageSource.getMessage("study.excerpt", null, locale), study.getExcerpt());
			}
		}
		if ((study.getPrevWork() != null && !study.getPrevWork().isEmpty()) || (study.getPubOnData() != null && !study.getPubOnData().isEmpty())) {
			Element othrStdyMat = addAndReturnElement(stdyDscr, "othrStdyMat", null);
			if (study.getPrevWork() != null && !study.getPrevWork().isEmpty()) {
				list = addAndReturnElement(addAndReturnElement(othrStdyMat, "relStdy", null), "list", null);
				setListItems(list, messageSource.getMessage("study.prevWork", null, locale),
				    messageSource.getMessage("study.prevWork." + study.getPrevWork().trim().toLowerCase(), null, locale));
				if (study.getPrevWork().trim().toLowerCase().equals("other")) {
					setListItems(list, messageSource.getMessage("export.ddi.prevwork.other", null, locale), study.getPrevWorkStr());
				}
			}
			if (study.getPubOnData() != null && !study.getPubOnData().isEmpty()) {
				Element list_t = addAndReturnElement(addAndReturnElement(othrStdyMat, "relPubl", null), "list", null);
				study.getPubOnData().forEach(pub -> {
					addTextElement(list_t, "itm", null, pub.getText());
				});
			}
		}
	}

	/**
	 * This function generates XML/DDI tags which are used in Study and Record export files
	 * 
	 * @param locale
	 * @param study
	 * @param citation
	 */
	private void createSharedStudyMeta(final Locale locale, final StudyDTO study, Element citation) {
		Element titlStmt = addAndReturnElement(citation, "titlStmt", null);
		addTextElement(titlStmt, "titl", null, study.getTitle());
		addTextElement(titlStmt, "parTitl", null, study.getTransTitle());
		addTextElement(titlStmt, "IDNo", null, study.getInternalID());
		Element rspStmt = addAndReturnElement(citation, "rspStmt", null);
		if (study.getContributors() != null && !study.getContributors().isEmpty())
			for (ContributorDTO contributor : study.getContributors()) {
				Element authEnty = addAndReturnElement(rspStmt, "AuthEnty", null);
				authEnty.addAttribute("affiliation", contributor.getInstitution());
				Element list = addAndReturnElement(authEnty, "list", null);
				setListItems(list, messageSource.getMessage("export.ddi.study.role", null, locale),
				    contributor.getPrimaryContributor() ? messageSource.getMessage("export.ddi.study.role.pi", null, locale)
				        : messageSource.getMessage("export.ddi.study.role.re", null, locale));
				setListItems(list, messageSource.getMessage("export.ddi.study.title", null, locale), contributor.getTitle());
				setListItems(list, messageSource.getMessage("export.ddi.study.orcid", null, locale), contributor.getOrcid());
				setListItems(list, messageSource.getMessage("export.ddi.study.firstName", null, locale), contributor.getFirstName());
				setListItems(list, messageSource.getMessage("export.ddi.study.lastName", null, locale), contributor.getLastName());
				setListItems(list, messageSource.getMessage("export.ddi.study.department", null, locale), contributor.getDepartment());
			}
	}

	/**
	 * 
	 * @param titl
	 * @param parTitl
	 * @param idno
	 * @param user
	 * @param root
	 * @param locale
	 */
	private void createDocDscr(final String titl, final String parTitl, final String idno, final UserDTO user, final Element root,
	    final Locale locale) {
		Element docDscr = addAndReturnElement(root, "docDscr", this.xmlns_ddi);
		Element citation = addAndReturnElement(docDscr, "citation", null);
		Element titlStmt = addAndReturnElement(citation, "titlStmt", null);
		addTextElement(titlStmt, "titl", null, titl);
		addTextElement(titlStmt, "parTitl", null, parTitl);
		addTextElement(titlStmt, "IDNo", null, idno);
		Element prodStmt = addAndReturnElement(citation, "prodStmt", null);
		Element producer = addAndReturnElement(prodStmt, "producer", null);
		producer.addAttribute("affiliation", user.getInstitution());
		producer.addAttribute("role", messageSource.getMessage("export.ddi.study.producer.role", null, locale));
		Element list = addAndReturnElement(producer, "list", null);
		setListItems(list, messageSource.getMessage("export.ddi.study.title", null, locale), user.getTitle());
		setListItems(list, messageSource.getMessage("export.ddi.study.orcid", null, locale), user.getOrcid());
		setListItems(list, messageSource.getMessage("export.ddi.study.firstName", null, locale), user.getFirstName());
		setListItems(list, messageSource.getMessage("export.ddi.study.lastName", null, locale), user.getLastName());
		setListItems(list, messageSource.getMessage("export.ddi.study.department", null, locale), user.getDepartment());
		addTextElement(prodStmt, "prodDate", null, LocalDateTime.now().toString());
		addTextElement(prodStmt, "software", null, messageSource.getMessage("export.ddi.study.software", null, locale));
	}

	/**
	 * TODO locale
	 * 
	 * @param parent
	 * @param name
	 * @param nsp
	 * @param text
	 * @param attributes
	 */
	private void addTextElement(final Element parent, final String name, final Namespace nsp, final Object text,
	    final SimpleEntry<?, ?>... attributes) {
		String input = null;
		if (text == null) {
			input = "";
		} else if (text instanceof Number) {
			input = String.valueOf(text);
		} else if (text instanceof Boolean) {
			input = (boolean) (text) ? "yes" : "no";
		} else if (text instanceof String) {
			input = String.valueOf(text);
		} else {
			input = String.valueOf(text);
		}
		if (input != null && !input.isEmpty()) {
			Element e = addAndReturnElement(parent, name.trim(), nsp);
			e.addText(input);
			if (attributes != null) {
				for (SimpleEntry<?, ?> m : attributes) {
					e.addAttribute(String.valueOf(m.getKey()), String.valueOf(m.getValue()));
				}
			}
		}

	}

	/**
	 * 
	 * @param parent
	 * @param name
	 * @param nsp
	 * @return
	 */
	private Element addAndReturnElement(final Element parent, final String name, final Namespace nsp) {
		Element el;
		if (nsp == null)
			el = parent.addElement(name.trim());
		else
			el = parent.addElement(new QName(name, nsp));
		return el;
	}

	/**
	 * 
	 * @param list
	 * @param label
	 * @param p
	 */
	private void setListItems(final Element list, final String label, final Object p) {
		if (p != null && ((p instanceof String) && !String.valueOf(p).isEmpty()) || !(p instanceof String)) {
			Element list_item = addAndReturnElement(list, "itm", null);
			addTextElement(list_item, "label", null, label);
			addTextElement(list_item, "p", null, p);
		}
	}

	/**
	 * 
	 * @param txt
	 * @param concept
	 * @param sumDscr
	 */
	private void addUniverseWithContent(final String txt, final String concept, final Element sumDscr) {
		if (txt != null && !txt.isEmpty()) {
			Element universe = addAndReturnElement(sumDscr, "universe", null);
			addTextElement(universe, "concept", null, concept);
			addTextElement(universe, "txt", null, txt);
		}
	}

	/**
	 * Initial new document
	 * 
	 * @return
	 */
	private Document createDoc() {
		log.trace("Entering createDoc");
		return DocumentHelper.createDocument();
	}

	/**
	 * Create root element and set namespaces
	 * 
	 * @param doc
	 * @return
	 */
	private Element addAndReturnRoot(final Document doc, final Boolean isDDI, final boolean isDW) {
		log.trace("Entering addAndReturnRoot(Document doc)");
		Element root = doc.addElement("codeBook");
		if (isDDI) {
			root.add(this.xmlns_ddi);
			root.add(this.xsi);
			root.addAttribute("xsi:schemaLocation", this.xsi_schemaLocation_ddi);
			root.addAttribute("version", this.version_ddi);
		} else if (isDW) {
			root.add(this.xmlns_dw);
			root.add(this.xsi);
			root.addAttribute("xsi:schemaLocation", this.xsi_schemaLocation_dw);
			root.addAttribute("created", LocalDateTime.now().toString());
			root.addAttribute("createdBy", "test@test.de");
			root.addAttribute("creator", "DataWiz");
		}
		return root;
	}

}
