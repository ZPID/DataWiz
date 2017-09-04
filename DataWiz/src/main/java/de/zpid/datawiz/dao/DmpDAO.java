package de.zpid.datawiz.dao;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.ProjectDTO;

@Repository
@Scope("singleton")
public class DmpDAO {

	@Autowired
	protected ClassPathXmlApplicationContext applicationContext;
	@Autowired
	protected JdbcTemplate jdbcTemplate;

	private static Logger log = LogManager.getLogger(DmpDAO.class);

	public DmpDAO() {
		super();
		if (log.isInfoEnabled())
			log.info("Loading DmpDAO as Singleton and Service");
	}

	public DmpDTO findByID(ProjectDTO project) throws Exception {
		if (log.isDebugEnabled())
			log.debug("execute findByID [id: " + project.getId() + "]");
		String sql = "SELECT * FROM dw_dmp where id = ?";
		DmpDTO dmp = jdbcTemplate.query(sql, new Object[] { project.getId() }, new ResultSetExtractor<DmpDTO>() {
			@Override
			public DmpDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					DmpDTO dmp = (DmpDTO) applicationContext.getBean("DmpDTO");
					dmp.setId(rs.getLong("id"));
					// ***************** Administrative Data *****************
					dmp.setDuration(rs.getString("duration"));
					dmp.setOrganizations(rs.getString("organizations"));
					dmp.setPlanAims(rs.getString("planAims"));
					// ***************** Research Data *****************
					dmp.setExistingData(rs.getString("existingData"));
					dmp.setDataCitation(rs.getString("dataCitation"));
					dmp.setExistingDataRelevance(rs.getString("existingDataRelevance"));
					dmp.setExistingDataIntegration(rs.getString("existingDataIntegration"));
					dmp.setOtherDataTypes(rs.getString("otherDataTypes"));
					dmp.setDataReproducibility(rs.getString("dataReproducibility"));
					dmp.setOtherCMIP(rs.getString("otherCMIP"));
					dmp.setOtherCMINP(rs.getString("otherCMINP"));
					dmp.setMeasOccasions(rs.getString("measOccasions"));
					dmp.setReliabilityTraining(rs.getString("reliabilityTraining"));
					dmp.setMultipleMeasurements(rs.getString("multipleMeasurements"));
					dmp.setQualitityOther(rs.getString("qualitityOther"));
					dmp.setFileFormat(rs.getString("fileFormat"));
					dmp.setWorkingCopy(rs.getBoolean("workingCopy"));
					dmp.setGoodScientific(rs.getBoolean("goodScientific"));
					dmp.setSubsequentUse(rs.getBoolean("subsequentUse"));
					dmp.setRequirements(rs.getBoolean("requirements"));
					dmp.setDocumentation(rs.getBoolean("documentation"));
					dmp.setDataSelection(rs.getBoolean("dataSelection"));
					dmp.setSelectionTime(rs.getString("selectionTime"));
					dmp.setSelectionResp(rs.getString("selectionResp"));
					dmp.setStorageDuration(rs.getString("storageDuration"));
					dmp.setDeleteProcedure(rs.getString("deleteProcedure"));
					// ***************** MetaData Data *****************
					dmp.setMetaDescription(rs.getString("metaDescription"));
					dmp.setMetaFramework(rs.getString("metaFramework"));
					dmp.setMetaGeneration(rs.getString("metaGeneration"));
					dmp.setMetaMonitor(rs.getString("metaMonitor"));
					dmp.setMetaFormat(rs.getString("metaFormat"));
					// ***************** Data Sharing *****************
					dmp.setReleaseObligation(rs.getBoolean("releaseObligation"));
					dmp.setSearchableData(rs.getString("searchableData"));
					dmp.setExpectedUsage(rs.getString("expectedUsage"));
					dmp.setPublStrategy(rs.getString("publStrategy"));
					dmp.setAccessReasonAuthor(rs.getString("accessReasonAuthor"));
					dmp.setNoAccessReason(rs.getString("noAccessReason"));
					dmp.setNoAccessReasonOther(rs.getString("noAccessReasonOther"));
					dmp.setDepositName(rs.getString("depositName"));
					dmp.setTransferTime(rs.getString("transferTime"));
					dmp.setSensitiveData(rs.getString("sensitiveData"));
					dmp.setInitialUsage(rs.getString("initialUsage"));
					dmp.setUsageRestriction(rs.getString("usageRestriction"));
					dmp.setAccessCosts(rs.getBoolean("accessCosts"));
					dmp.setClarifiedRights(rs.getBoolean("clarifiedRights"));
					dmp.setAcquisitionAgreement(rs.getBoolean("acquisitionAgreement"));
					dmp.setUsedPID(rs.getString("usedPID"));
					dmp.setUsedPIDTxt(rs.getString("usedPIDTxt"));
					// ***************** Storage and infrastructure *****************
					dmp.setStorageResponsible(rs.getString("storageResponsible"));
					dmp.setNamingCon(rs.getString("namingCon"));
					dmp.setStoragePlaces(rs.getString("storagePlaces"));
					dmp.setStorageBackups(rs.getString("storageBackups"));
					dmp.setStorageTransfer(rs.getString("storageTransfer"));
					dmp.setStorageExpectedSize(rs.getString("storageExpectedSize"));
					dmp.setStorageRequirements(rs.getBoolean("storageRequirements"));
					dmp.setStorageRequirementsTxt(rs.getString("storageRequirementsTxt"));
					dmp.setStorageSuccession(rs.getBoolean("storageSuccession"));
					dmp.setStorageSuccessionTxt(rs.getString("storageSuccessionTxt"));
					// ***************** Organization, management and policies *****************
					dmp.setFrameworkNationality(rs.getString("frameworkNationality"));
					dmp.setFrameworkNationalityTxt(rs.getString("frameworkNationalityTxt"));
					dmp.setResponsibleUnit(rs.getString("responsibleUnit"));
					dmp.setInvolvedInstitutions(rs.getString("involvedInstitutions"));
					dmp.setInvolvedInformed(rs.getBoolean("involvedInformed"));
					dmp.setContributionsDefined(rs.getBoolean("contributionsDefined"));
					dmp.setContributionsDefinedTxt(rs.getString("contributionsDefinedTxt"));
					dmp.setGivenConsent(rs.getBoolean("givenConsent"));
					dmp.setManagementWorkflow(rs.getBoolean("managementWorkflow"));
					dmp.setManagementWorkflowTxt(rs.getString("managementWorkflowTxt"));
					dmp.setStaffDescription(rs.getBoolean("staffDescription"));
					dmp.setStaffDescriptionTxt(rs.getString("staffDescriptionTxt"));
					dmp.setFunderRequirements(rs.getString("funderRequirements"));
					dmp.setPlanningAdherence(rs.getString("planningAdherence"));
					// ***************** Ethical and legal aspects *****************
					dmp.setDataProtection(rs.getBoolean("dataProtection"));
					dmp.setProtectionRequirements(rs.getString("protectionRequirements"));
					dmp.setConsentObtained(rs.getBoolean("consentObtained"));
					dmp.setConsentObtainedTxt(rs.getString("consentObtainedTxt"));
					dmp.setSharingConsidered(rs.getBoolean("sharingConsidered"));
					dmp.setIrbApproval(rs.getBoolean("irbApproval"));
					dmp.setIrbApprovalTxt(rs.getString("irbApprovalTxt"));
					dmp.setSensitiveDataIncluded(rs.getBoolean("sensitiveDataIncluded"));
					dmp.setSensitiveDataIncludedTxt(rs.getString("sensitiveDataIncludedTxt"));
					dmp.setExternalCopyright(rs.getBoolean("externalCopyright"));
					dmp.setExternalCopyrightTxt(rs.getString("externalCopyrightTxt"));
					dmp.setInternalCopyright(rs.getBoolean("internalCopyright"));
					dmp.setInternalCopyrightTxt(rs.getString("internalCopyrightTxt"));
					// ***************** Costs *****************
					dmp.setSpecificCosts(rs.getString("specificCosts"));
					dmp.setSpecificCostsTxt(rs.getString("specificCostsTxt"));
					dmp.setBearCost(rs.getString("bearCost"));
					rs.close();
					return dmp;
				}
				return null;
			}
		});
		if (log.isDebugEnabled())
			log.debug("leaving getByProject with result:" + ((dmp != null) ? dmp.getId() : "null"));
		return dmp;
	}

	public int updateAdminData(DmpDTO dmp) throws Exception {
		if (log.isDebugEnabled())
			log.debug("execute updateAdminData for [id: " + dmp.getId() + "]");
		return this.jdbcTemplate.update("UPDATE dw_dmp SET duration = ?, organizations = ?, planAims = ? WHERE id = ?", dmp.getDuration(),
		    dmp.getOrganizations(), dmp.getPlanAims(), dmp.getId());
	}

	public int updateResearchData(final DmpDTO dmp) throws Exception {
		if (log.isDebugEnabled())
			log.debug("execute updateResearchData for [id: " + dmp.getId() + "]");
		int ret = this.jdbcTemplate.update(
		    "UPDATE dw_dmp SET existingData = ?, dataCitation = ?, existingDataRelevance = ?, existingDataIntegration = ?,"
		        + " otherDataTypes = ?, dataReproducibility = ?, otherCMIP = ?, otherCMINP = ?, measOccasions = ?,"
		        + " reliabilityTraining = ?, multipleMeasurements = ?, qualitityOther = ?, fileFormat = ?,"
		        + " workingCopy = ?, goodScientific = ?, subsequentUse = ?, requirements = ?,"
		        + " documentation = ?, dataSelection = ?, selectionTime = ?, selectionResp = ?, storageDuration = ?, deleteProcedure = ? WHERE id = ?",
		    dmp.getExistingData(), dmp.getDataCitation(), dmp.getExistingDataRelevance(), dmp.getExistingDataIntegration(), dmp.getOtherDataTypes(),
		    dmp.getDataReproducibility(), dmp.getOtherCMIP(), dmp.getOtherCMINP(), dmp.getMeasOccasions(), dmp.getReliabilityTraining(),
		    dmp.getMultipleMeasurements(), dmp.getQualitityOther(), dmp.getFileFormat(), dmp.isWorkingCopy(), dmp.isGoodScientific(),
		    dmp.isSubsequentUse(), dmp.isRequirements(), dmp.isDocumentation(), dmp.isDataSelection(), dmp.getSelectionTime(), dmp.getSelectionResp(),
		    dmp.getStorageDuration(), dmp.getDeleteProcedure(), dmp.getId());
		return ret;
	}

	public int updateMetaData(DmpDTO dmp) throws Exception {
		if (log.isDebugEnabled())
			log.debug("execute updateMetaData for [id: " + dmp.getId() + "]");
		int ret = this.jdbcTemplate.update(
		    "UPDATE dw_dmp SET metaDescription = ?, metaFramework = ?, metaGeneration = ?, metaMonitor = ?, metaFormat = ? WHERE id = ?",
		    dmp.getMetaDescription(), dmp.getMetaFramework(), dmp.getMetaGeneration(), dmp.getMetaMonitor(), dmp.getMetaFormat(), dmp.getId());
		return ret;
	}

	public int updateSharingData(DmpDTO dmp) throws Exception {
		if (log.isDebugEnabled())
			log.debug("execute updateSharingData for [id: " + dmp.getId() + "]");
		return this.jdbcTemplate.update(
		    "UPDATE dw_dmp SET releaseObligation = ?, searchableData = ?, expectedUsage = ?, "
		        + "publStrategy = ?, accessReasonAuthor = ?, noAccessReason = ?, noAccessReasonOther = ?, "
		        + "depositName = ?, transferTime = ?, sensitiveData= ?, initialUsage = ?, usageRestriction = ?, accessCosts = ?,"
		        + "clarifiedRights = ?, acquisitionAgreement = ?, usedPID = ?, " + "usedPIDTxt = ? WHERE id = ?",
		    dmp.isReleaseObligation(), dmp.getSearchableData(), dmp.getExpectedUsage(), dmp.getPublStrategy(), dmp.getAccessReasonAuthor(),
		    dmp.getNoAccessReason(), dmp.getNoAccessReasonOther(), dmp.getDepositName(), dmp.getTransferTime(), dmp.getSensitiveData(),
		    dmp.getInitialUsage(), dmp.getUsageRestriction(), dmp.isAccessCosts(), dmp.isClarifiedRights(), dmp.isAcquisitionAgreement(),
		    dmp.getUsedPID(), dmp.getUsedPIDTxt(), dmp.getId());
	}

	public int updateStorageData(DmpDTO dmp) throws Exception {
		if (log.isDebugEnabled())
			log.debug("execute updateStorageData for [id: " + dmp.getId() + "]");
		return this.jdbcTemplate.update(
		    "UPDATE dw_dmp SET storageResponsible = ?, namingCon = ?, storagePlaces = ?, storageBackups = ?, "
		        + "storageTransfer = ?, storageExpectedSize = ?, storageRequirements = ?, storageRequirementsTxt = ?, "
		        + "storageSuccession = ?, storageSuccessionTxt = ? WHERE id = ?",
		    dmp.getStorageResponsible(), dmp.getNamingCon(), dmp.getStoragePlaces(), dmp.getStorageBackups(), dmp.getStorageTransfer(),
		    dmp.getStorageExpectedSize(), dmp.isStorageRequirements(), dmp.getStorageRequirementsTxt(), dmp.isStorageSuccession(),
		    dmp.getStorageSuccessionTxt(), dmp.getId());
	}

	public int updateOrganizationData(DmpDTO dmp) throws Exception {
		if (log.isDebugEnabled())
			log.debug("execute updateOrganizationData for [id: " + dmp.getId() + "]");
		return this.jdbcTemplate.update(
		    "UPDATE dw_dmp SET frameworkNationality = ?, frameworkNationalityTxt = ?, responsibleUnit = ?, "
		        + "involvedInstitutions = ?, involvedInformed = ?, contributionsDefined = ?, "
		        + "contributionsDefinedTxt = ?, givenConsent = ?, managementWorkflow = ?, "
		        + "managementWorkflowTxt = ?, staffDescription = ?, staffDescriptionTxt = ?, funderRequirements = ?, planningAdherence = ? WHERE id = ?",
		    dmp.getFrameworkNationality(), dmp.getFrameworkNationalityTxt(), dmp.getResponsibleUnit(), dmp.getInvolvedInstitutions(),
		    dmp.isInvolvedInformed(), dmp.isContributionsDefined(), dmp.getContributionsDefinedTxt(), dmp.isGivenConsent(), dmp.isManagementWorkflow(),
		    dmp.getManagementWorkflowTxt(), dmp.isStaffDescription(), dmp.getStaffDescriptionTxt(), dmp.getFunderRequirements(),
		    dmp.getPlanningAdherence(), dmp.getId());
	}

	public int updateEthicalData(DmpDTO dmp) throws Exception {
		if (log.isDebugEnabled())
			log.debug("execute updateEthicalData for [id: " + dmp.getId() + "]");
		return this.jdbcTemplate.update(
		    "UPDATE dw_dmp SET dataProtection = ?, protectionRequirements = ?, consentObtained = ?, consentObtainedTxt = ?, "
		        + "sharingConsidered = ?, irbApproval = ?, irbApprovalTxt = ?, sensitiveDataIncluded = ?, "
		        + "sensitiveDataIncludedTxt = ?, externalCopyright = ?, externalCopyrightTxt = ?, "
		        + "internalCopyright = ?, internalCopyrightTxt = ? WHERE id = ?",
		    dmp.isDataProtection(), dmp.getProtectionRequirements(), dmp.isConsentObtained(), dmp.getConsentObtainedTxt(), dmp.isSharingConsidered(),
		    dmp.isIrbApproval(), dmp.getIrbApprovalTxt(), dmp.isSensitiveDataIncluded(), dmp.getSensitiveDataIncludedTxt(), dmp.isExternalCopyright(),
		    dmp.getExternalCopyrightTxt(), dmp.isInternalCopyright(), dmp.getInternalCopyrightTxt(), dmp.getId());
	}

	public int updateCostsData(DmpDTO dmp) throws Exception {
		if (log.isDebugEnabled())
			log.debug("execute updateCostsData for [id: " + dmp.getId() + "]");
		return this.jdbcTemplate.update("UPDATE dw_dmp SET specificCosts = ?, specificCostsTxt = ?, bearCost= ? WHERE id = ?", dmp.getSpecificCosts(),
		    dmp.getSpecificCostsTxt(), dmp.getBearCost(), dmp.getId());
	}

	public int insertNewDMP(BigInteger dmpid) throws Exception {
		if (log.isDebugEnabled())
			log.debug("execute insertNewDMP for project [id: " + dmpid + "]");
		return this.jdbcTemplate.update("INSERT INTO dw_dmp (id) VALUES(?)", dmpid);
	}
}
