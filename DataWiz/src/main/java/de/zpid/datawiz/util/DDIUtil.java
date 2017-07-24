package de.zpid.datawiz.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.ProjectDTO;

@Component
@Scope("singleton")
public class DDIUtil {

  private static Logger log = LogManager.getLogger(DDIUtil.class);
  private static final Namespace ddi = new Namespace("ddi", null);
  private static final Namespace dwz = new Namespace("dw", null);

  public Document exportProjectAndDMPXML(ProjectDTO project, DmpDTO dmp) {
    Document doc = createDoc();
    Element root = addAndReturnRoot(doc);
    try {
      exportDmp(project, dmp, root.addElement(new QName("dmp", dwz)));
    } catch (Exception e) {
      return null;
    }
    return doc;
  }

  private Document createDoc() {
    log.trace("Entering createDoc");
    return DocumentHelper.createDocument();
  }

  private Element addAndReturnRoot(Document doc) {
    log.trace("Entering addAndReturnRoot(Document doc)");
    return doc.addElement("codebook");
  }

  private void exportDmp(ProjectDTO project, DmpDTO dmp, Element dmpElement) throws Exception {
    log.trace("Entering exportDmp(ProjectDTO project, DmpDTO dmp, Element dmpElement)");
    Element admin = dmpElement.addElement(new QName("administration", dwz));
    admin.addElement(new QName("projectname", dwz)).addText(project.getTitle());
    admin.addElement(new QName("IDNo", dwz)).addText(String.valueOf(dmp.getId()));
    Element aims = admin.addElement(new QName("aims", dwz));
    aims.addElement(new QName("proj", dwz)).addText(dmp.getProjectAims());
    aims.addElement(new QName("plan", dwz)).addText(dmp.getPlanAims());
    admin.addElement(new QName("sponsors", dwz)).addText(dmp.getProjectSponsors());
    admin.addElement(new QName("duration", dwz)).addText(dmp.getDuration());
    admin.addElement(new QName("partners", dwz)).addText(dmp.getOrganizations());
    admin.addElement(new QName("leader", dwz));
    Element rsrch = dmpElement.addElement(new QName("research", dwz));
    Element exdat = rsrch.addElement(new QName("existingData", dwz));
    exdat.addElement(new QName("select", dwz)).addText(dmp.getExistingData());
    exdat.addElement(new QName("optional", dwz)).addText(dmp.getExistingDataRelevance());
    exdat.addElement(new QName("optional", dwz)).addText(dmp.getExistingDataIntegration());
    exdat.addElement(new QName("optional", dwz)).addText(dmp.getDataCitation());
    Element usdat = rsrch.addElement(new QName("usedData", dwz));
    usdat.addElement(new QName("select", dwz)).addText(String.valueOf(dmp.getUsedDataTypes()));
    usdat.addElement(new QName("optional", dwz)).addText(dmp.getOtherDataTypes());
    rsrch.addElement(new QName("reprod", dwz)).addText(dmp.getDataReproducibility());
    Element collm = rsrch.addElement(new QName("collectionMode", dwz));
    Element cmipr = collm.addElement(new QName("present", dwz));
    cmipr.addElement(new QName("select", dwz)).addText(String.valueOf(dmp.getUsedCollectionModes())); // filter
    cmipr.addElement(new QName("optional", dwz)).addText(dmp.getOtherCMIP());
    Element cminp = collm.addElement(new QName("notPresent", dwz));
    cminp.addElement(new QName("select", dwz)).addText(String.valueOf(dmp.getUsedCollectionModes())); // filter
    cminp.addElement(new QName("optional", dwz)).addText(dmp.getOtherCMINP());
    rsrch.addElement(new QName("design", dwz)).addElement(new QName("select", dwz)).addText(dmp.getMeasOccasions());
    Element quali = rsrch.addElement(new QName("quality", dwz));
    quali.addElement(new QName("assurance", dwz));
    quali.addElement(new QName("reliability", dwz)).addText(dmp.getReliabilityTraining());
    quali.addElement(new QName("multiple", dwz)).addText(dmp.getMultipleMeasurements());
    quali.addElement(new QName("other", dwz)).addText(dmp.getQualitityOther());
    rsrch.addElement(new QName("fileFormat", dwz)).addText(dmp.getFileFormat());
    Element storg = rsrch.addElement(new QName("storage", dwz));
    storg.addElement(new QName("workingCopy", dwz)).addElement(new QName("select", dwz))
        .addText(String.valueOf(dmp.isWorkingCopy()));
    storg.addElement(new QName("goodScientific", dwz)).addElement(new QName("select", dwz))
        .addText(String.valueOf(dmp.isGoodScientific()));
    storg.addElement(new QName("subsequentUse", dwz)).addElement(new QName("select", dwz))
        .addText(String.valueOf(dmp.isSubsequentUse()));
    storg.addElement(new QName("requirements", dwz)).addElement(new QName("select", dwz))
        .addText(String.valueOf(dmp.isRequirements()));
    storg.addElement(new QName("documentation", dwz)).addElement(new QName("select", dwz))
        .addText(String.valueOf(dmp.isDocumentation()));
    Element slctn = rsrch.addElement(new QName("selection", dwz));
    slctn.addElement(new QName("select", dwz)).addText(String.valueOf(dmp.isDataSelection()));
    slctn.addElement(new QName("optional", dwz)).addText(dmp.getSelectionTime());
    slctn.addElement(new QName("optional", dwz)).addText(dmp.getSelectionResp());
    rsrch.addElement(new QName("duration", dwz)).addText(dmp.getDuration());
    rsrch.addElement(new QName("delete", dwz)).addText(dmp.getDeleteProcedure());
    Element medat = dmpElement.addElement(new QName("metadata", dwz));
    medat.addElement(new QName("purpose", dwz)).addElement(new QName("select", dwz))
        .addText(String.valueOf(dmp.getSelectedMetaPurposes()));
    medat.addElement(new QName("description", dwz)).addText(dmp.getMetaDescription());
    medat.addElement(new QName("framework", dwz)).addText(dmp.getMetaFramework());
    medat.addElement(new QName("generation", dwz)).addText(dmp.getMetaGeneration());
    medat.addElement(new QName("monitor", dwz)).addText(dmp.getMetaMonitor());
    medat.addElement(new QName("format", dwz)).addText(dmp.getMetaFormat());
    Element shrng = dmpElement.addElement(new QName("sharing", dwz));
    shrng.addElement(new QName("obligation", dwz)).addElement(new QName("select", dwz))
        .addText(String.valueOf(dmp.isReleaseObligation()));
    shrng.addElement(new QName("expectedUsage", dwz)).addText(dmp.getExpectedUsage());
    Element pubst = shrng.addElement(new QName("publicStrategy", dwz));
    pubst.addElement(new QName("select", dwz)).addText(dmp.getPublStrategy());
    pubst.addElement(new QName("optional", dwz)).addText(dmp.getDepositName());
    pubst.addElement(new QName("searchableData", dwz)).addElement(new QName("select", dwz))
        .addText(String.valueOf(dmp.isSearchableData()));
    pubst.addElement(new QName("optional", dwz)).addText(dmp.getTransferTime());
    pubst.addElement(new QName("optional", dwz)).addText(dmp.getSensitiveData());
    pubst.addElement(new QName("optional", dwz)).addText(dmp.getInitialUsage());
    pubst.addElement(new QName("optional", dwz)).addText(dmp.getUsageRestriction());
    Element accos = pubst.addElement(new QName("accessCosts", dwz));
    accos.addElement(new QName("select", dwz)).addText(String.valueOf(dmp.isAccessCosts()));
    accos.addElement(new QName("optional", dwz)).addText(dmp.getAccessCostsTxt());
    pubst.addElement(new QName("clarifiedRights", dwz)).addElement(new QName("select", dwz))
        .addText(String.valueOf(dmp.isClarifiedRights()));
    pubst.addElement(new QName("acquisitionAgreement", dwz)).addElement(new QName("select", dwz))
        .addText(String.valueOf(dmp.isAcquisitionAgreement()));
    Element uspid = pubst.addElement(new QName("usedPID", dwz));
    uspid.addElement(new QName("select", dwz)).addText(dmp.getUsedPID());
    uspid.addElement(new QName("optional", dwz)).addText(dmp.getUsedPIDTxt());
    Element strct = dmpElement.addElement(new QName("structure", dwz));
    strct.addElement(new QName("responsible", dwz)).addText(dmp.getStorageResponsible());
    strct.addElement(new QName("places", dwz)).addText(dmp.getStoragePlaces());
    strct.addElement(new QName("backups", dwz)).addText(dmp.getStorageBackups());
    strct.addElement(new QName("transfer", dwz)).addText(dmp.getStorageTransfer());
    strct.addElement(new QName("expectedSize", dwz)).addText(dmp.getStorageExpectedSize());
    Element requi = strct.addElement(new QName("requirements", dwz));
    requi.addElement(new QName("select", dwz)).addText(String.valueOf(dmp.isStorageRequirements()));
    requi.addElement(new QName("optional", dwz)).addText(dmp.getStorageRequirementsTxt());
    Element sccss = strct.addElement(new QName("succession", dwz));
    sccss.addElement(new QName("select", dwz)).addText(String.valueOf(dmp.isStorageSuccession()));
    sccss.addElement(new QName("optional", dwz)).addText(dmp.getStorageSuccessionTxt());
    Element mngmt = dmpElement.addElement(new QName("management", dwz));
    Element ntnly = mngmt.addElement(new QName("nationality", dwz));
    ntnly.addElement(new QName("select", dwz)).addText(dmp.getFrameworkNationality());
    ntnly.addElement(new QName("optional", dwz)).addText(dmp.getFrameworkNationalityTxt());
    mngmt.addElement(new QName("responsible", dwz)).addText(dmp.getResponsibleUnit());
    mngmt.addElement(new QName("institution", dwz)).addText(dmp.getInvolvedInstitutions());
    Element invld = mngmt.addElement(new QName("involved", dwz));
    invld.addElement(new QName("select", dwz)).addText(String.valueOf(dmp.isInvolvedInformed()));
    Element inopt = invld.addElement(new QName("optional", dwz));
    inopt.addElement(new QName("select", dwz)).addText(String.valueOf(dmp.isContributionsDefined()));
    inopt.addElement(new QName("optional", dwz)).addText(dmp.getContributionsDefinedTxt());
    invld.addElement(new QName("optional", dwz)).addText(String.valueOf(dmp.isGivenConsent()));
    Element wrkfl = mngmt.addElement(new QName("workflow", dwz));
    wrkfl.addElement(new QName("select", dwz)).addText(String.valueOf(dmp.isManagementWorkflow()));
    wrkfl.addElement(new QName("optional", dwz)).addText(dmp.getManagementWorkflowTxt());
    Element staff = mngmt.addElement(new QName("staff", dwz));
    staff.addElement(new QName("select", dwz)).addText(String.valueOf(dmp.isStaffDescription()));
    staff.addElement(new QName("optional", dwz)).addText(dmp.getStaffDescriptionTxt());
    mngmt.addElement(new QName("funderReq", dwz)).addText(dmp.getFunderRequirements());
    mngmt.addElement(new QName("adherence", dwz)).addText(dmp.getPlanningAdherence());
    Element ethic = dmpElement.addElement(new QName("ethical", dwz));
    Element prtct = ethic.addElement(new QName("protection", dwz));
    prtct.addElement(new QName("select", dwz)).addText(String.valueOf(dmp.isDataProtection()));
    prtct.addElement(new QName("optinal", dwz)).addText(dmp.getProtectionRequirements());
    Element cnsnt = prtct.addElement(new QName("consent", dwz));
    cnsnt.addElement(new QName("select", dwz)).addText(String.valueOf(dmp.isConsentObtained()));
    cnsnt.addElement(new QName("optional", dwz)).addText(dmp.getConsentObtainedTxt());
    cnsnt.addElement(new QName("optinal", dwz)).addElement(new QName("select", dwz))
        .addText(String.valueOf(dmp.isSharingConsidered()));
    Element irbel = ethic.addElement(new QName("irb", dwz));
    irbel.addElement(new QName("select", dwz)).addText(String.valueOf(dmp.isIrbApproval()));
    irbel.addElement(new QName("optional", dwz)).addText(dmp.getIrbApprovalTxt());
    Element sensi = ethic.addElement(new QName("sensitive", dwz));
    sensi.addElement(new QName("select", dwz)).addText(String.valueOf(dmp.isSensitiveDataIncluded()));
    sensi.addElement(new QName("optional", dwz)).addText(dmp.getSensitiveDataIncludedTxt());
    Element cprit = ethic.addElement(new QName("copyrights", dwz));
    Element intnl = cprit.addElement(new QName("internal", dwz));
    intnl.addElement(new QName("select", dwz)).addText(String.valueOf(dmp.isInternalCopyright()));
    intnl.addElement(new QName("optional", dwz)).addText(dmp.getInternalCopyrightTxt());
    Element extnl = cprit.addElement(new QName("external", dwz));
    extnl.addElement(new QName("select", dwz)).addText(String.valueOf(dmp.isExternalCopyright()));
    extnl.addElement(new QName("optional", dwz)).addText(dmp.getExternalCopyrightTxt());
    Element costs = dmpElement.addElement(new QName("costs", dwz));
    Element spcos = costs.addElement(new QName("specified", dwz));
    spcos.addElement(new QName("select", dwz)).addText(dmp.getSpecificCosts());
    spcos.addElement(new QName("optional", dwz)).addText(dmp.getSpecificCostsTxt());
    costs.addElement(new QName("bearCost", dwz)).addText(dmp.getBearCost());
  }

}
