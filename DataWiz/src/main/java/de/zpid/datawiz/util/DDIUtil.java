package de.zpid.datawiz.util;

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

import com.itextpdf.layout.property.ListNumberingType;

import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.dto.FormTypesDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.datawiz.dto.StudyConstructDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.StudyInstrumentDTO;
import de.zpid.datawiz.dto.StudyListTypesDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.spss.dto.SPSSValueLabelDTO;
import de.zpid.spss.dto.SPSSVarDTO;

@Component
@Scope("singleton")
public class DDIUtil {

	@Autowired
	protected MessageSource messageSource;

	private static Logger log = LogManager.getLogger(DDIUtil.class);
	private final Namespace ddi = new Namespace("ddi", "ddi:codebook:2_5");
	private final Namespace dwz = new Namespace("dw", null);
	private final Namespace xmlns = new Namespace("", "ddi:codebook:2_5");
	private final Namespace xsi = new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
	private final String xsi_schemaLocation = "ddi:codebook:2_5 http://www.ddialliance.org/Specification/DDI-Codebook/2.5/XMLSchema/codebook.xsd";
	private final String version = "2.5";

	/**
	 * Create document for project, dmp and additional project material
	 * 
	 * @param project
	 * @param dmp
	 * @param pFiles
	 * @return
	 */
	public Document createProjectDocument(final ProjectDTO project, final DmpDTO dmp, final List<FileDTO> pFiles) {
		log.trace("Entering createProjectDocument for project [id: {}]", () -> project.getId());
		Document doc = createDoc();
		Element root = addAndReturnRoot(doc, false);
		try {
			if (dmp != null)
				exportDmp(project, dmp, root.addElement(new QName("dmp", this.dwz)));
			if (pFiles != null)
				exportZus(pFiles, root.addElement(new QName("otherMat", ddi)));
		} catch (Exception e) {
			// TODO
			log.error("ExportError: ", () -> e);
			return null;
		}
		log.trace("Leaving createProjectDocument for project [id: {}] without Errors", () -> project.getId());
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
	public Document createStudyDocument(final StudyForm sForm, final List<FileDTO> sFiles, final UserDTO user) {
		log.trace("Entering createStudyDocument for project [id: {}], study [id: {}]", () -> sForm.getProject().getId(), () -> sForm.getStudy().getId());
		Document doc = createDoc();
		Element root = addAndReturnRoot(doc, true);
		try {
			exportStd(sForm, user, root, Locale.ENGLISH);
			if (sFiles != null)
				exportZus(sFiles, root.addElement(new QName("otherMat", ddi)));
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
	public Document createRecordDocument(final RecordDTO record) {
		log.trace("Entering createRecordDocument for record [id: {}]", () -> record.getId());
		Document doc = createDoc();
		Element root = addAndReturnRoot(doc, true);
		try {
			exportCodeBook(record, addAndReturnElement(root, "dataDscr", this.xmlns));
		} catch (Exception e) {
			// TODO
			log.error("ExportError: ", () -> e);
			return null;
		}
		log.trace("Leaving createStudyDocumen for project [id: {}] without Errors", () -> record.getId());
		return doc;
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
	private Element addAndReturnRoot(final Document doc, final Boolean isDDI) {
		log.trace("Entering addAndReturnRoot(Document doc)");
		Element root = doc.addElement("codeBook");
		if (isDDI) {
			root.add(this.xmlns);
			root.add(this.xsi);
			root.addAttribute("xsi:schemaLocation", this.xsi_schemaLocation);
			root.addAttribute("version", this.version);
		}
		return root;
	}

	/**
	 * This function inserts the dmp meta data into the referenced dmpElement
	 * 
	 * @param project
	 * @param dmp
	 * @param dmpElement
	 * @throws Exception
	 */
	private void exportDmp(final ProjectDTO project, final DmpDTO dmp, final Element dmpElement) throws Exception {
		log.trace("Entering exportDmp(ProjectDTO project, DmpDTO dmp, Element dmpElement)");
		Element admin = dmpElement.addElement(new QName("administration", this.dwz));
		admin.addElement(new QName("projectname", this.dwz)).addText(project.getTitle());
		admin.addElement(new QName("IDNo", this.dwz)).addText(String.valueOf(dmp.getId()));
		Element aims = admin.addElement(new QName("aims", this.dwz));
		aims.addElement(new QName("proj", this.dwz)).addText(dmp.getProjectAims());
		aims.addElement(new QName("plan", this.dwz)).addText(dmp.getPlanAims());
		admin.addElement(new QName("sponsors", this.dwz)).addText(dmp.getProjectSponsors());
		admin.addElement(new QName("duration", this.dwz)).addText(dmp.getDuration());
		admin.addElement(new QName("partners", this.dwz)).addText(dmp.getOrganizations());
		admin.addElement(new QName("leader", this.dwz));
		Element rsrch = dmpElement.addElement(new QName("research", this.dwz));
		Element exdat = rsrch.addElement(new QName("existingData", this.dwz));
		exdat.addElement(new QName("select", this.dwz)).addText(dmp.getExistingData());
		exdat.addElement(new QName("optional", this.dwz)).addText(dmp.getExistingDataRelevance());
		exdat.addElement(new QName("optional", this.dwz)).addText(dmp.getExistingDataIntegration());
		exdat.addElement(new QName("optional", this.dwz)).addText(dmp.getDataCitation());
		Element usdat = rsrch.addElement(new QName("usedData", this.dwz));
		usdat.addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.getUsedDataTypes()));
		usdat.addElement(new QName("optional", this.dwz)).addText(dmp.getOtherDataTypes());
		rsrch.addElement(new QName("reprod", this.dwz)).addText(dmp.getDataReproducibility());
		Element collm = rsrch.addElement(new QName("collectionMode", this.dwz));
		Element cmipr = collm.addElement(new QName("present", this.dwz));
		cmipr.addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.getUsedCollectionModes())); // filter
		cmipr.addElement(new QName("optional", this.dwz)).addText(dmp.getOtherCMIP());
		Element cminp = collm.addElement(new QName("notPresent", this.dwz));
		cminp.addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.getUsedCollectionModes())); // filter
		cminp.addElement(new QName("optional", this.dwz)).addText(dmp.getOtherCMINP());
		rsrch.addElement(new QName("design", this.dwz)).addElement(new QName("select", this.dwz)).addText(dmp.getMeasOccasions());
		Element quali = rsrch.addElement(new QName("quality", this.dwz));
		quali.addElement(new QName("assurance", this.dwz));
		quali.addElement(new QName("reliability", this.dwz)).addText(dmp.getReliabilityTraining());
		quali.addElement(new QName("multiple", this.dwz)).addText(dmp.getMultipleMeasurements());
		quali.addElement(new QName("other", this.dwz)).addText(dmp.getQualitityOther());
		rsrch.addElement(new QName("fileFormat", this.dwz)).addText(dmp.getFileFormat());
		Element storg = rsrch.addElement(new QName("storage", this.dwz));
		storg.addElement(new QName("workingCopy", this.dwz)).addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isWorkingCopy()));
		storg.addElement(new QName("goodScientific", this.dwz)).addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isGoodScientific()));
		storg.addElement(new QName("subsequentUse", this.dwz)).addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isSubsequentUse()));
		storg.addElement(new QName("requirements", this.dwz)).addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isRequirements()));
		storg.addElement(new QName("documentation", this.dwz)).addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isDocumentation()));
		Element slctn = rsrch.addElement(new QName("selection", this.dwz));
		slctn.addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isDataSelection()));
		slctn.addElement(new QName("optional", this.dwz)).addText(dmp.getSelectionTime());
		slctn.addElement(new QName("optional", this.dwz)).addText(dmp.getSelectionResp());
		rsrch.addElement(new QName("duration", this.dwz)).addText(dmp.getDuration());
		rsrch.addElement(new QName("delete", this.dwz)).addText(dmp.getDeleteProcedure());
		Element medat = dmpElement.addElement(new QName("metadata", this.dwz));
		medat.addElement(new QName("purpose", this.dwz)).addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.getSelectedMetaPurposes()));
		medat.addElement(new QName("description", this.dwz)).addText(dmp.getMetaDescription());
		medat.addElement(new QName("framework", this.dwz)).addText(dmp.getMetaFramework());
		medat.addElement(new QName("generation", this.dwz)).addText(dmp.getMetaGeneration());
		medat.addElement(new QName("monitor", this.dwz)).addText(dmp.getMetaMonitor());
		medat.addElement(new QName("format", this.dwz)).addText(dmp.getMetaFormat());
		Element shrng = dmpElement.addElement(new QName("sharing", this.dwz));
		shrng.addElement(new QName("obligation", this.dwz)).addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isReleaseObligation()));
		shrng.addElement(new QName("expectedUsage", this.dwz)).addText(dmp.getExpectedUsage());
		Element pubst = shrng.addElement(new QName("publicStrategy", this.dwz));
		pubst.addElement(new QName("select", this.dwz)).addText(dmp.getPublStrategy());
		pubst.addElement(new QName("optional", this.dwz)).addText(dmp.getDepositName());
		pubst.addElement(new QName("searchableData", this.dwz)).addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isSearchableData()));
		pubst.addElement(new QName("optional", this.dwz)).addText(dmp.getTransferTime());
		pubst.addElement(new QName("optional", this.dwz)).addText(dmp.getSensitiveData());
		pubst.addElement(new QName("optional", this.dwz)).addText(dmp.getInitialUsage());
		pubst.addElement(new QName("optional", this.dwz)).addText(dmp.getUsageRestriction());
		Element accos = pubst.addElement(new QName("accessCosts", this.dwz));
		accos.addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isAccessCosts()));
		accos.addElement(new QName("optional", this.dwz)).addText(dmp.getAccessCostsTxt());
		pubst.addElement(new QName("clarifiedRights", this.dwz)).addElement(new QName("select", this.dwz))
		    .addText(String.valueOf(dmp.isClarifiedRights()));
		pubst.addElement(new QName("acquisitionAgreement", this.dwz)).addElement(new QName("select", this.dwz))
		    .addText(String.valueOf(dmp.isAcquisitionAgreement()));
		Element uspid = pubst.addElement(new QName("usedPID", this.dwz));
		uspid.addElement(new QName("select", this.dwz)).addText(dmp.getUsedPID());
		uspid.addElement(new QName("optional", this.dwz)).addText(dmp.getUsedPIDTxt());
		Element strct = dmpElement.addElement(new QName("structure", this.dwz));
		strct.addElement(new QName("responsible", this.dwz)).addText(dmp.getStorageResponsible());
		strct.addElement(new QName("places", this.dwz)).addText(dmp.getStoragePlaces());
		strct.addElement(new QName("backups", this.dwz)).addText(dmp.getStorageBackups());
		strct.addElement(new QName("transfer", this.dwz)).addText(dmp.getStorageTransfer());
		strct.addElement(new QName("expectedSize", this.dwz)).addText(dmp.getStorageExpectedSize());
		Element requi = strct.addElement(new QName("requirements", this.dwz));
		requi.addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isStorageRequirements()));
		requi.addElement(new QName("optional", this.dwz)).addText(dmp.getStorageRequirementsTxt());
		Element sccss = strct.addElement(new QName("succession", this.dwz));
		sccss.addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isStorageSuccession()));
		sccss.addElement(new QName("optional", this.dwz)).addText(dmp.getStorageSuccessionTxt());
		Element mngmt = dmpElement.addElement(new QName("management", this.dwz));
		Element ntnly = mngmt.addElement(new QName("nationality", this.dwz));
		ntnly.addElement(new QName("select", this.dwz)).addText(dmp.getFrameworkNationality());
		ntnly.addElement(new QName("optional", this.dwz)).addText(dmp.getFrameworkNationalityTxt());
		mngmt.addElement(new QName("responsible", this.dwz)).addText(dmp.getResponsibleUnit());
		mngmt.addElement(new QName("institution", this.dwz)).addText(dmp.getInvolvedInstitutions());
		Element invld = mngmt.addElement(new QName("involved", this.dwz));
		invld.addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isInvolvedInformed()));
		Element inopt = invld.addElement(new QName("optional", this.dwz));
		inopt.addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isContributionsDefined()));
		inopt.addElement(new QName("optional", this.dwz)).addText(dmp.getContributionsDefinedTxt());
		invld.addElement(new QName("optional", this.dwz)).addText(String.valueOf(dmp.isGivenConsent()));
		Element wrkfl = mngmt.addElement(new QName("workflow", this.dwz));
		wrkfl.addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isManagementWorkflow()));
		wrkfl.addElement(new QName("optional", this.dwz)).addText(dmp.getManagementWorkflowTxt());
		Element staff = mngmt.addElement(new QName("staff", this.dwz));
		staff.addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isStaffDescription()));
		staff.addElement(new QName("optional", this.dwz)).addText(dmp.getStaffDescriptionTxt());
		mngmt.addElement(new QName("funderReq", this.dwz)).addText(dmp.getFunderRequirements());
		mngmt.addElement(new QName("adherence", this.dwz)).addText(dmp.getPlanningAdherence());
		Element ethic = dmpElement.addElement(new QName("ethical", this.dwz));
		Element prtct = ethic.addElement(new QName("protection", this.dwz));
		prtct.addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isDataProtection()));
		prtct.addElement(new QName("optinal", this.dwz)).addText(dmp.getProtectionRequirements());
		Element cnsnt = prtct.addElement(new QName("consent", this.dwz));
		cnsnt.addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isConsentObtained()));
		cnsnt.addElement(new QName("optional", this.dwz)).addText(dmp.getConsentObtainedTxt());
		cnsnt.addElement(new QName("optinal", this.dwz)).addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isSharingConsidered()));
		Element irbel = ethic.addElement(new QName("irb", this.dwz));
		irbel.addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isIrbApproval()));
		irbel.addElement(new QName("optional", this.dwz)).addText(dmp.getIrbApprovalTxt());
		Element sensi = ethic.addElement(new QName("sensitive", this.dwz));
		sensi.addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isSensitiveDataIncluded()));
		sensi.addElement(new QName("optional", this.dwz)).addText(dmp.getSensitiveDataIncludedTxt());
		Element cprit = ethic.addElement(new QName("copyrights", this.dwz));
		Element intnl = cprit.addElement(new QName("internal", this.dwz));
		intnl.addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isInternalCopyright()));
		intnl.addElement(new QName("optional", this.dwz)).addText(dmp.getInternalCopyrightTxt());
		Element extnl = cprit.addElement(new QName("external", this.dwz));
		extnl.addElement(new QName("select", this.dwz)).addText(String.valueOf(dmp.isExternalCopyright()));
		extnl.addElement(new QName("optional", this.dwz)).addText(dmp.getExternalCopyrightTxt());
		Element costs = dmpElement.addElement(new QName("costs", this.dwz));
		Element spcos = costs.addElement(new QName("specified", this.dwz));
		spcos.addElement(new QName("select", this.dwz)).addText(dmp.getSpecificCosts());
		spcos.addElement(new QName("optional", this.dwz)).addText(dmp.getSpecificCostsTxt());
		costs.addElement(new QName("bearCost", this.dwz)).addText(dmp.getBearCost());
	}

	private void exportZus(List<FileDTO> files, Element otherMat) throws Exception {
		for (FileDTO fle : files) {
			Element fileElement = otherMat.addElement(new QName("otherMat", this.ddi));
			fileElement.addElement(new QName("labl", this.ddi)).addText(fle.getFileName());
			Element notes = fileElement.addElement(new QName("notes", this.ddi));
			notes.addElement(new QName("fileSize", this.dwz)).addText(Long.toString(fle.getFileSize()));
			notes.addElement(new QName("uploadDate", this.dwz)).addText(fle.getUploadDate().toString());
			Element sum = notes.addElement(new QName("checksum", this.dwz));
			sum.addElement(new QName("sha1", this.dwz)).addText(fle.getSha1Checksum());
			sum.addElement(new QName("md5", this.dwz)).addText(fle.getMd5checksum());
		}
	}

	private void exportStd(final StudyForm sForm, UserDTO user, Element root, Locale locale) throws Exception {
		final StudyDTO study = sForm.getStudy();
		final ProjectDTO project = sForm.getProject();
		// docDscr start
		createDocDscr(study.getTitle(), study.getTransTitle(), study.getInternalID(), user, root, locale);
		// stdyDscr start
		Element stdyDscr = addAndReturnElement(root, "stdyDscr", this.xmlns);
		Element citation = addAndReturnElement(stdyDscr, "citation", null);
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
		if (study.getSoftware() != null && study.getSoftware().isEmpty()) {
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
		if (study.getEligibilities() != null && !study.getEligibilities().isEmpty()) {
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
				    !objective.getObjectivetype().isEmpty()
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
				if (construct.getType().equals("OTHER")) {
					setListItems(list, messageSource.getMessage("study.constructs.type", null, locale), construct.getOther());
				} else {
					setListItems(list, messageSource.getMessage("study.constructs.type", null, locale),
					    !construct.getType().isEmpty()
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
			if ((study.getMultilevel() != null && !study.getMultilevel().isEmpty())
			    || (study.getRecruiting() != null && !study.getRecruiting().isEmpty())) {
				list = addAndReturnElement(addAndReturnElement(dataColl, "collSitu", null), "list", null);
				setListItems(list, messageSource.getMessage("study.multilevel", null, locale), study.getMultilevel());
				setListItems(list, messageSource.getMessage("study.recruiting", null, locale), study.getRecruiting());
			}

		}

		/*
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 		
		 */

		Element meth = stdyDscr.addElement(new QName("method", ddi));
		// Element devi = coll.addElement(new QName("deviat", dwz));
		// devi.addElement(new QName("qualInd", dwz)).addText(study.getQualInd());
		// devi.addElement(new QName("qualLim", dwz)).addText(study.getQualLim());
		// Element mode = coll.addElement(new QName("collMode", ddi));
		// mode.addElement(new QName("formDscr", dwz)).addText(study.getDescription());
		// Element inms = mode.addElement(new QName("instruments", dwz));
		// if (study.getInstruments() != null)
		// for (StudyInstrumentDTO inst : study.getInstruments()) {
		// Element instr = inms.addElement(new QName("instrument", dwz));
		// instr.addElement(new QName("title", dwz)).addText(inst.getTitle());
		// instr.addElement(new QName("author", dwz)).addText(inst.getAuthor());
		// instr.addElement(new QName("citation", dwz)).addText(inst.getCitation());
		// instr.addElement(new QName("summary", dwz)).addText(inst.getSummary());
		// instr.addElement(new QName("theoHint", dwz)).addText(inst.getTheoHint());
		// instr.addElement(new QName("structure", dwz)).addText(inst.getStructure());
		// instr.addElement(new QName("construction", dwz)).addText(inst.getConstruction());
		// instr.addElement(new QName("objectivity", dwz)).addText(inst.getObjectivity());
		// instr.addElement(new QName("reliability", dwz)).addText(inst.getReliability());
		// instr.addElement(new QName("validity", dwz)).addText(inst.getValidity());
		// instr.addElement(new QName("norm", dwz)).addText(inst.getNorm());
		// }
		// Element used = mode.addElement(new QName("usedCollectionModes", dwz));
		// used.addElement(new QName("select", dwz)).addText(String.valueOf(study.getUsedCollectionModes()));
		// used.addElement(new QName("optinal", dwz)).addAttribute("Invest", "present").addText(study.getOtherCMIP());
		// used.addElement(new QName("optional", dwz)).addAttribute("Invest", "not present").addText(study.getOtherCMINP());
		// Element iven = mode.addElement(new QName("intervention", dwz));
		// iven.addElement(new QName("select", dwz)).addText(study.getInterTypeExp());
		// iven.addElement(new QName("select", dwz)).addText(study.getInterTypeDes());
		// iven.addElement(new QName("select", dwz)).addText(study.getInterTypeLab());
		// iven.addElement(new QName("select", dwz)).addText(study.getRandomization());
		// Element resi = coll.addElement(new QName("resInstru", ddi));
		// resi.addElement(new QName("select", dwz)).addText(study.getSurveyType() == null ? "" : study.getSurveyType());
		// Element srcs = coll.addElement(new QName("sources", ddi));
		// Element dsrc = srcs.addElement(new QName("dataSrc", ddi));
		// dsrc.addElement(new QName("select", dwz)).addText(String.valueOf(study.getUsedSourFormat()));
		// dsrc.addElement(new QName("optional", dwz)).addText(study.getOtherSourFormat());
		// Element situ = coll.addElement(new QName("collSitu", ddi));
		// situ.addElement(new QName("transDescr", dwz)).addText(study.getTransDescr());
		// situ.addElement(new QName("multilevel", dwz)).addText(study.getMultilevel());
		// coll.addElement(new QName("sctMin", ddi)).addText(study.getRecruiting());
		// coll.addElement(new QName("cleanOps", ddi)).addText(study.getSpecCirc());
		// Element sasi = coll.addElement(new QName("targetSampleSize", ddi));
		// sasi.addElement(new QName("sampleSize", ddi)).addText(study.getIntSampleSize());
		Element note1 = meth.addElement(new QName("notes", ddi));
		note1.addElement(new QName("dataRerun", dwz)).addText(study.getDataRerun());
		Element pres = note1.addElement(new QName("persDataPres", dwz));
		pres.addElement(new QName("select", dwz)).addText(study.getPersDataPres());
		pres.addElement(new QName("optional", dwz)).addText(study.getAnonymProc());
		Element anfo = meth.addElement(new QName("anlyInfo", ddi));
		Element appr = anfo.addElement(new QName("dataAppr", ddi));
		appr.addElement(new QName("qualInd", dwz)).addText(study.getQualInd());
		appr.addElement(new QName("qualLim", dwz)).addText(study.getQualLim());
		Element accs = stdyDscr.addElement(new QName("dataAccs", ddi));
		Element avai = accs.addElement(new QName("setAvail", ddi));
		Element comp = avai.addElement(new QName("complete", ddi));
		Element cose = comp.addElement(new QName("completeSel", dwz));
		cose.addElement(new QName("select", dwz)).addText(study.getCompleteSel());
		comp.addElement(new QName("excerpt", dwz)).addText(study.getExcerpt());
		Element note2 = accs.addElement(new QName("notes", ddi));
		Element irb = note2.addElement(new QName("irb", dwz));
		irb.addElement(new QName("select", dwz)).addText(String.valueOf(study.isIrb()));
		irb.addElement(new QName("optional", dwz)).addText(study.getIrbName());
		Element sent = note2.addElement(new QName("consent", dwz));
		sent.addElement(new QName("select", dwz)).addText(String.valueOf(study.isConsent()));
		Element shar = sent.addElement(new QName("optional", dwz));
		shar.addElement(new QName("select", dwz)).addText(String.valueOf(study.isConsentShare()));
		Element perd = note2.addElement(new QName("persData", dwz));
		perd.addElement(new QName("select", dwz)).addText(String.valueOf(study.isPersDataColl()));
		Element smat = stdyDscr.addElement(new QName("othrStdyMat", ddi));
		Element rels = smat.addElement(new QName("relStdy", ddi));
		Element prev = rels.addElement(new QName("prevWork", dwz));
		prev.addElement(new QName("select", dwz)).addText(study.getPrevWork());
		prev.addElement(new QName("optional", dwz)).addText(study.getPrevWorkStr());
		rels.addElement(new QName("interArms", dwz)).addText(String.valueOf(study.getInterArms()));
		Element relp = smat.addElement(new QName("relPubl", ddi));
		if (study.getPubOnData() != null)
			for (StudyListTypesDTO pubs : study.getPubOnData()) {
				relp.addElement(new QName("item", dwz)).addText(pubs.getText());
			}
		Element refs = smat.addElement(new QName("othRefs", ddi));
		if (study.getConflInterests() != null)
			for (StudyListTypesDTO conf : study.getConflInterests()) {
				refs.addElement(new QName("item", dwz)).addText(conf.getText());
			}
	}

	public void exportCodeBook(RecordDTO record, Element dataDscr) throws Exception {
		Element varsElement = dataDscr.addElement(new QName("variables", dwz));
		for (SPSSVarDTO vars : record.getVariables()) {
			Element variable = varsElement.addElement(new QName("var", ddi)).addAttribute("id", Long.toString(vars.getId()))
			    .addAttribute("name", vars.getName()).addAttribute("dcml", String.valueOf(vars.getDecimals()))
			    .addAttribute("scale", Long.toString(vars.getWidth())).addAttribute("nature", String.valueOf(((SPSSVarDTO) vars).getMeasureLevel()))
			    .addAttribute("catQnty", Long.toString(vars.getNumOfAttributes()));
			variable.addElement(new QName("labl", ddi)).addText(vars.getLabel());
			variable.addElement(new QName("varFormat", ddi)).addText(String.valueOf(((SPSSVarDTO) vars).getType()));
			Element invalrng = variable.addElement(new QName("invalrng", ddi));
			invalrng.addElement(new QName("notes", ddi)).addText(String.valueOf(((SPSSVarDTO) vars).getMissingFormat()));
			invalrng.addElement(new QName("range", ddi)).addAttribute("UNIT", "REAL").addAttribute("min", vars.getMissingVal1()).addAttribute("max",
			    vars.getMissingVal2());
			invalrng.addElement(new QName("item", ddi)).addAttribute("UNIT", "REAL").addAttribute("VALUE", vars.getMissingVal1());
			invalrng.addElement(new QName("item", ddi)).addAttribute("UNIT", "REAL").addAttribute("VALUE", vars.getMissingVal2());
			invalrng.addElement(new QName("item", ddi)).addAttribute("UNIT", "REAL").addAttribute("VALUE", vars.getMissingVal3());
			Element vallabel = variable.addElement(new QName("catgry", ddi)).addAttribute("catgry", "dw_record_var_vallabel");
			if (vars.getValues() != null)
				for (SPSSValueLabelDTO labl : vars.getValues()) {
					Element item = vallabel.addElement(new QName("item", dwz));
					item.addElement(new QName("labl", ddi)).addText(labl.getLabel());
					item.addElement(new QName("catValu", ddi)).addText(labl.getValue());
				}
			if (vars.getDw_attributes() != null)
				for (SPSSValueLabelDTO dwa : vars.getDw_attributes()) {
					Element dw = variable.addElement(new QName("catgry", ddi)).addAttribute("catgry", String.valueOf(dwa));
					dw.addElement(new QName("labl", ddi)).addText(dwa.getLabel());
					dw.addElement(new QName("catValu", ddi)).addText(dwa.getValue());
				}
			Element usra = variable.addElement(new QName("userAttr", dwz));
			if (vars.getAttributes() != null)
				for (SPSSValueLabelDTO user : vars.getAttributes()) {
					Element itm = usra.addElement(new QName("item", dwz));
					Element cat = itm.addElement(new QName("catgry", ddi)).addAttribute("catgry", "benutzerdefiniert");
					cat.addElement(new QName("labl", ddi)).addText(user.getLabel());
					cat.addElement(new QName("catValu", ddi)).addText(user.getValue());
				}
			variable.addElement(new QName("notes", ddi)).addText(String.valueOf(((SPSSVarDTO) vars).getRole()));
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
		Element docDscr = addAndReturnElement(root, "docDscr", this.xmlns);
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
		addTextElement(prodStmt, "software", null, messageSource.getMessage("export.ddi.study.software", null, locale));
	}

	/**
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

}
