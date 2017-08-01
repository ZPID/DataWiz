package de.zpid.datawiz.util;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.datawiz.dto.StudyConstructDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.StudyInstrumentDTO;
import de.zpid.datawiz.dto.StudyListTypesDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.spss.dto.SPSSValueLabelDTO;
import de.zpid.spss.dto.SPSSVarDTO;

@Component
@Scope("singleton")
public class DDIUtil {

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
	public Document createStudyDocument(final ProjectDTO project, final StudyDTO study, final List<FileDTO> sFiles, final UserDTO user) {
		log.trace("Entering createStudyDocument for project [id: {}], study [id: {}]", () -> project.getId(), () -> study.getId());
		Document doc = createDoc();
		Element root = addAndReturnRoot(doc, true);
		try {
			exportStd(project, study, user, root);
			if (sFiles != null)
				exportZus(sFiles, root.addElement(new QName("otherMat", ddi)));
		} catch (Exception e) {
			// TODO
			log.error("ExportError: ", () -> e);
			return null;
		}
		log.trace("Leaving createStudyDocument for project [id: {}], study [id: {}] without Errors", () -> project.getId(), () -> study.getId());
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

	private void addTextElement(final Element parent, final String name, final Namespace nsp, final Object text) {
		String input = null;
		if (text == null) {
			input = "";
		} else if (text instanceof Number) {
			input = String.valueOf(text);
		} else if (text instanceof Boolean) {
			input = (boolean) (text) ? "yes" : "no";
		} else if (text instanceof String) {
			input = (String) text;
		}
		if (nsp == null)
			parent.addElement(name.trim()).addText(input);
		else
			parent.addElement(new QName(name.trim(), nsp)).addText(input);
	}

	private Element addAndReturnElement(final Element parent, final String name, final Namespace nsp) {
		Element el;
		if (nsp == null)
			el = parent.addElement(name.trim());
		else
			el = parent.addElement(new QName(name, nsp));
		return el;
	}

	private void exportStd(ProjectDTO project, StudyDTO study, UserDTO user, Element root) throws Exception {
		// docDscr start
		Element docDscr = addAndReturnElement(root, "docDscr", this.xmlns);
		Element citation = addAndReturnElement(docDscr, "citation", null);
		Element titlStmt = addAndReturnElement(citation, "titlStmt", null);
		addTextElement(titlStmt, "titl", null, study.getTitle());
		addTextElement(titlStmt, "parTitl", null, study.getTransTitle());
		addTextElement(titlStmt, "IDNo", null, study.getInternalID());
		Element prodStmt = addAndReturnElement(citation, "prodStmt", null);
		addTextElement(prodStmt, "producer", null, user.getEmail().toString());
		addTextElement(prodStmt, "software", null, "DataWiz");
		// docDscr end
		// stdyDscr start
		Element stdyDscr = addAndReturnElement(root, "stdyDscr", this.xmlns);
		citation = addAndReturnElement(stdyDscr, "citation", null);
		titlStmt = addAndReturnElement(citation, "titlStmt", null);
		addTextElement(titlStmt, "titl", null, study.getTitle());
		addTextElement(titlStmt, "parTitl", null, study.getTransTitle());
		addTextElement(titlStmt, "IDNo", null, study.getInternalID());
		Element rspStmt = addAndReturnElement(citation, "rspStmt", null);
		if (study.getContributors() != null)
			for (ContributorDTO contributor : study.getContributors()) {
				Element authEnty = addAndReturnElement(rspStmt, "AuthEnty", null);
				authEnty.addElement(new QName("title", dwz)).addText(contributor.getTitle());
				authEnty.addElement(new QName("firstName", dwz)).addText(contributor.getFirstName());
				authEnty.addElement(new QName("lastName", dwz)).addText(contributor.getLastName());
				authEnty.addElement(new QName("institution", dwz)).addText(contributor.getInstitution());
				authEnty.addElement(new QName("department", dwz)).addText(contributor.getDepartment());
				authEnty.addElement(new QName("orcid", dwz)).addText(contributor.getOrcid());
			}
		prodStmt = citation.addElement(new QName("prodStmt", ddi));
		addTextElement(prodStmt, "producer", null, "");
		Element ware2 = prodStmt.addElement(new QName("software", ddi));
		if (study.getSoftware() != null)
			for (StudyListTypesDTO software : study.getSoftware()) {
				ware2.addElement(new QName("item", dwz)).addText(software.getText() != null ? software.getText() : "");
			}
		prodStmt.addElement(new QName("fundAg", ddi)).addText(project.getFunding());
		prodStmt.addElement(new QName("grantNumber", ddi)).addText(project.getGrantNumber());

		Element dist = citation.addElement(new QName("distStmt", ddi));
		// dist.addElement(new QName("distrbtr", ddi)).addText();
		Element info = stdyDscr.addElement(new QName("stdyInfo", ddi));
		Element subj = info.addElement(new QName("subject", ddi));

		subj.addElement(new QName("keyword", ddi)).addText("");

		Element abst = stdyDscr.addElement(new QName("abstract", ddi));
		abst.addElement(new QName("sAbstract", dwz)).addText(study.getsAbstract());
		abst.addElement(new QName("sAbstractTrans", dwz)).addText(study.getsAbstractTrans());
		Element sum = stdyDscr.addElement(new QName("sumDscr", ddi));
		Element dte = sum.addElement(new QName("collDate", ddi));
		dte.addElement(new QName("collStart", dwz)).addText(String.valueOf(study.getCollStart()));
		dte.addElement(new QName("collEnd", dwz)).addText(String.valueOf(study.getCollEnd()));
		sum.addElement(new QName("nation", ddi)).addText(study.getCountry());
		Element geo = sum.addElement(new QName("geogCover", ddi));
		geo.addElement(new QName("city", dwz)).addText(study.getCity());
		geo.addElement(new QName("region", dwz)).addText(study.getRegion());
		Element anly = sum.addElement(new QName("anlyUnit", ddi));
		anly.addElement(new QName("select", dwz)).addText(study.getObsUnit());
		anly.addElement(new QName("optional", dwz)).addText(study.getObsUnitOther());
		Element uni = sum.addElement(new QName("universe", ddi));
		Element eligs = uni.addElement(new QName("eligibilities", dwz));
		if (study.getEligibilities() != null)
			for (StudyListTypesDTO elig : study.getEligibilities()) {
				eligs.addElement(new QName("eligibility", dwz)).addText(elig.getText());
			}
		uni.addElement(new QName("population", dwz)).addText(study.getPopulation());
		uni.addElement(new QName("sex", dwz)).addText(study.getSex());
		uni.addElement(new QName("age", dwz)).addText(study.getAge());
		uni.addElement(new QName("specGroups", dwz)).addText(study.getSpecGroups());
		Element kind = sum.addElement(new QName("dataKind", ddi));
		if (study.isExperimentalIntervention()) {
			kind.addElement(new QName("intervention", dwz)).addText("experimental");
		}
		if (study.isSurveyIntervention()) {
			kind.addElement(new QName("intervention", dwz)).addText("survey");
		}
		if (study.isTestIntervention()) {
			kind.addElement(new QName("intervention", dwz)).addText("test");
		}
		Element sourform = kind.addElement(new QName("usedSourFormat", dwz));
		if (study.getUsedSourFormat() != null)
			for (Integer sour : study.getUsedSourFormat()) {
				sourform.addText(String.valueOf(sour) + " ");
			}
		Element note = stdyDscr.addElement(new QName("notes", ddi));
		Element objs = note.addElement(new QName("objectives", dwz));
		if (study.getObjectives() != null)
			for (StudyListTypesDTO ob : study.getObjectives()) {
				Element obj = objs.addElement(new QName("objective", dwz));
				obj.addElement(new QName("text", dwz)).addText(ob.getText() != null ? ob.getText() : "");
				obj.addElement(new QName("select", dwz)).addText(ob.getObjectivetype() != null ? ob.getObjectivetype() : "");
			}
		Element cons = note.addElement(new QName("constructs", dwz));
		if (study.getConstructs() != null)
			for (StudyConstructDTO co : study.getConstructs()) {
				Element con = cons.addElement(new QName("construct", dwz));
				con.addElement(new QName("text", dwz)).addText(co.getName());
				con.addElement(new QName("select", dwz)).addText(co.getType());
				con.addElement(new QName("optional", dwz)).addText(co.getOther());
			}
		Element meth = stdyDscr.addElement(new QName("method", ddi));
		Element coll = meth.addElement(new QName("dataColl", ddi));
		Element time = coll.addElement(new QName("timeMeth", ddi));
		Element repm = time.addElement(new QName("repMeasures", dwz));
		repm.addElement(new QName("select", dwz)).addText(study.getRepMeasures());
		time.addElement(new QName("timeDim", dwz)).addText(study.getTimeDim());
		Element dtco = coll.addElement(new QName("dataCollector", ddi));
		dtco.addElement(new QName("select", dwz)).addText(study.getResponsibility());
		dtco.addElement(new QName("optional", dwz)).addText(study.getResponsibilityOther());
		Element freq = coll.addElement(new QName("frequenc", ddi));
		if (study.getMeasOcc() != null)
			for (StudyListTypesDTO occ : study.getMeasOcc()) {
				Element item = freq.addElement(new QName("item", dwz));
				item.addElement(new QName("text", dwz)).addText(occ.getText());
				item.addElement(new QName("select", dwz)).addText(String.valueOf(occ.isTimetable()));
				item.addElement(new QName("sort", dwz)).addText(String.valueOf(occ.getSort()));
			}
		Element samp = coll.addElement(new QName("sampProc", ddi));
		samp.addElement(new QName("sampSize", dwz)).addText(study.getSampleSize());
		samp.addElement(new QName("powerAnalysis", dwz)).addText(study.getPowerAnalysis());
		Element ling = samp.addElement(new QName("sampling", dwz));
		ling.addElement(new QName("select", dwz)).addText(study.getSampMethod());
		ling.addElement(new QName("optional", dwz)).addText(study.getSampMethodOther());
		Element devi = coll.addElement(new QName("deviat", dwz));
		devi.addElement(new QName("qualInd", dwz)).addText(study.getQualInd());
		devi.addElement(new QName("qualLim", dwz)).addText(study.getQualLim());
		Element mode = coll.addElement(new QName("collMode", ddi));
		mode.addElement(new QName("formDscr", dwz)).addText(study.getDescription());
		Element inms = mode.addElement(new QName("instruments", dwz));
		if (study.getInstruments() != null)
			for (StudyInstrumentDTO inst : study.getInstruments()) {
				Element instr = inms.addElement(new QName("instrument", dwz));
				instr.addElement(new QName("title", dwz)).addText(inst.getTitle());
				instr.addElement(new QName("author", dwz)).addText(inst.getAuthor());
				instr.addElement(new QName("citation", dwz)).addText(inst.getCitation());
				instr.addElement(new QName("summary", dwz)).addText(inst.getSummary());
				instr.addElement(new QName("theoHint", dwz)).addText(inst.getTheoHint());
				instr.addElement(new QName("structure", dwz)).addText(inst.getStructure());
				instr.addElement(new QName("construction", dwz)).addText(inst.getConstruction());
				instr.addElement(new QName("objectivity", dwz)).addText(inst.getObjectivity());
				instr.addElement(new QName("reliability", dwz)).addText(inst.getReliability());
				instr.addElement(new QName("validity", dwz)).addText(inst.getValidity());
				instr.addElement(new QName("norm", dwz)).addText(inst.getNorm());
			}
		Element used = mode.addElement(new QName("usedCollectionModes", dwz));
		used.addElement(new QName("select", dwz)).addText(String.valueOf(study.getUsedCollectionModes()));
		used.addElement(new QName("optinal", dwz)).addAttribute("Invest", "present").addText(study.getOtherCMIP());
		used.addElement(new QName("optional", dwz)).addAttribute("Invest", "not present").addText(study.getOtherCMINP());
		Element iven = mode.addElement(new QName("intervention", dwz));
		iven.addElement(new QName("select", dwz)).addText(study.getInterTypeExp());
		iven.addElement(new QName("select", dwz)).addText(study.getInterTypeDes());
		iven.addElement(new QName("select", dwz)).addText(study.getInterTypeLab());
		iven.addElement(new QName("select", dwz)).addText(study.getRandomization());
		Element resi = coll.addElement(new QName("resInstru", ddi));
		resi.addElement(new QName("select", dwz)).addText(study.getSurveyType() == null ? "" : study.getSurveyType());
		Element srcs = coll.addElement(new QName("sources", ddi));
		Element dsrc = srcs.addElement(new QName("dataSrc", ddi));
		dsrc.addElement(new QName("select", dwz)).addText(String.valueOf(study.getUsedSourFormat()));
		dsrc.addElement(new QName("optional", dwz)).addText(study.getOtherSourFormat());
		Element relt = srcs.addElement(new QName("relTheorys", dwz));
		if (study.getRelTheorys() != null)
			for (StudyListTypesDTO theo : study.getRelTheorys()) {
				relt.addElement(new QName("item", dwz)).addText(theo.getText() != null ? theo.getText() : "");
			}
		Element situ = coll.addElement(new QName("collSitu", ddi));
		situ.addElement(new QName("transDescr", dwz)).addText(study.getTransDescr());
		situ.addElement(new QName("multilevel", dwz)).addText(study.getMultilevel());
		coll.addElement(new QName("sctMin", ddi)).addText(study.getRecruiting());
		coll.addElement(new QName("cleanOps", ddi)).addText(study.getSpecCirc());
		Element sasi = coll.addElement(new QName("targetSampleSize", ddi));
		sasi.addElement(new QName("sampleSize", ddi)).addText(study.getIntSampleSize());
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

}
