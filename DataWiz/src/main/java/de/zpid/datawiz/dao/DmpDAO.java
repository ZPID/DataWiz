package de.zpid.datawiz.dao;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.util.DelType;

public class DmpDAO {

  private static final Logger log = Logger.getLogger(DmpDAO.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
  private JdbcTemplate jdbcTemplate;

  public DmpDAO() {
    super();
  }

  public DmpDAO(DataSource dataSource) {
    super();
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  /**
   * 
   * @param project
   * @return
   * @throws Exception
   */
  public DmpDTO getByID(ProjectDTO project) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute getByID [id: " + project.getId() + "]");
    String sql = "SELECT * FROM dw_dmp where id = ?";
    DmpDTO dmp = jdbcTemplate.query(sql, new Object[] { project.getId() }, new ResultSetExtractor<DmpDTO>() {
      @Override
      public DmpDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (rs.next()) {
          DmpDTO dmp = (DmpDTO) context.getBean("DmpDTO");
          dmp.setId(new BigInteger(rs.getBigDecimal("id").toString()));
          // ***************** Administrative Data *****************
          dmp.setProjectAims(rs.getString("projectAims"));
          dmp.setProjectSponsors(rs.getString("projectSponsors"));
          dmp.setDuration(rs.getString("duration"));
          dmp.setOrganizations(rs.getString("organizations"));
          dmp.setPlanAims(rs.getString("planAims"));
          // ***************** Research Data *****************
          dmp.setExistingData(rs.getString("existingData"));
          dmp.setDataCitation(rs.getString("dataCitation"));
          dmp.setExistingDataRelevance(rs.getString("existingDataRelevance"));
          dmp.setExistingDataIntegration(rs.getString("existingDataIntegration"));
          // TODO usedDataTypes speichern
          dmp.setOtherDataTypes(rs.getString("otherDataTypes"));
          dmp.setDataReproducibility(rs.getString("dataReproducibility"));
          // TODO usedCollectionModes speichern
          dmp.setOtherCMIP(rs.getString("otherCMIP"));
          dmp.setOtherCMINP(rs.getString("otherCMINP"));
          dmp.setMeasOccasions(rs.getString("measOccasions"));
          dmp.setReliabilityTraining(rs.getString("reliabilityTraining"));
          dmp.setMultipleMeasurements(rs.getString("multipleMeasurements"));
          dmp.setQualitityOther(rs.getString("qualitityOther"));
          dmp.setFileFormat(rs.getString("fileFormat"));
          dmp.setWorkingCopy(rs.getBoolean("workingCopy"));
          dmp.setWorkingCopyTxt(rs.getString("workingCopyTxt"));
          dmp.setGoodScientific(rs.getBoolean("goodScientific"));
          dmp.setGoodScientificTxt(rs.getString("goodScientificTxt"));
          dmp.setSubsequentUse(rs.getBoolean("subsequentUse"));
          dmp.setSubsequentUseTxt(rs.getString("subsequentUseTxt"));
          dmp.setRequirements(rs.getBoolean("requirements"));
          dmp.setRequirementsTxt(rs.getString("requirementsTxt"));
          dmp.setDocumentation(rs.getBoolean("documentation"));
          dmp.setDocumentationTxt(rs.getString("documentationTxt"));
          dmp.setDataSelection(rs.getBoolean("dataSelection"));
          dmp.setSelectionTime(rs.getString("selectionTime"));
          dmp.setSelectionResp(rs.getString("selectionResp"));
          dmp.setSelectionSoftware(rs.getString("selectionSoftware"));
          dmp.setSelectionCriteria(rs.getString("selectionCriteria"));
          dmp.setStorageDuration(rs.getString("storageDuration"));
          dmp.setDeleteProcedure(rs.getString("deleteProcedure"));
          // ***************** MetaData Data *****************

          return dmp;
        }
        return null;
      }
    });
    dmp.setUsedDataTypes(getDMPUsedDataTypes(dmp.getId(), DelType.datatype));
    dmp.setAdminChanged(false);
    dmp.setResearchChanged(false);
    if (log.isDebugEnabled())
      log.debug("leaving getByProject with result:" + ((dmp != null) ? dmp.getId() : "null"));
    return dmp;
  }

  public int updateResearchData(DmpDTO dmp) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute updateResearchData for [id: " + dmp.getId() + "]");
    int ret = this.jdbcTemplate.update(
        "UPDATE dw_dmp SET existingData = ?, dataCitation = ?, existingDataRelevance = ?, existingDataIntegration = ?,"
            + " otherDataTypes = ?, dataReproducibility = ?, otherCMIP = ?, otherCMINP = ?, measOccasions = ?,"
            + " reliabilityTraining = ?, multipleMeasurements = ?, qualitityOther = ?, fileFormat = ?,"
            + " workingCopy = ?, workingCopyTxt = ?, goodScientific = ?, goodScientificTxt = ?,"
            + " subsequentUse = ?, subsequentUseTxt = ?, requirements = ?, requirementsTxt = ?,"
            + " documentation = ?, documentationTxt = ?, dataSelection = ?, selectionTime = ?, selectionResp = ?,"
            + " selectionSoftware = ?, selectionCriteria = ?, storageDuration = ?, deleteProcedure = ?"
            + " WHERE id = ?",
        dmp.getExistingData(), dmp.getDataCitation(), dmp.getExistingDataRelevance(), dmp.getExistingDataIntegration(),
        dmp.getOtherDataTypes(), dmp.getDataReproducibility(), dmp.getOtherCMIP(), dmp.getOtherCMINP(),
        dmp.getMeasOccasions(), dmp.getReliabilityTraining(), dmp.getMultipleMeasurements(), dmp.getQualitityOther(),
        dmp.getFileFormat(), dmp.isWorkingCopy(), dmp.getWorkingCopyTxt(), dmp.isGoodScientific(),
        dmp.getGoodScientificTxt(), dmp.isSubsequentUse(), dmp.getSubsequentUseTxt(), dmp.isRequirements(),
        dmp.getRequirementsTxt(), dmp.isDocumentation(), dmp.getDocumentationTxt(), dmp.isDataSelection(),
        dmp.getSelectionTime(), dmp.getSelectionResp(), dmp.getSelectionSoftware(), dmp.getSelectionCriteria(),
        dmp.getStorageDuration(), dmp.getDeleteProcedure(), dmp.getId());
    List<Integer> datatypes = getDMPUsedDataTypes(dmp.getId(), DelType.datatype);
    if (datatypes != null && datatypes.size() > 0) {
      for (Integer i : datatypes)
        deleteDMPUsedDataTypes(dmp.getId(), i);
    }
    if (dmp.getUsedDataTypes() != null && dmp.getUsedDataTypes().size() > 0) {
      for (int i : dmp.getUsedDataTypes())
        insertDMPUsedDataTypes(dmp.getId(), i);
    }
    return ret;
  }

  public int updateAdminData(DmpDTO dmp) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute updateAdminData for [id: " + dmp.getId() + "]");
    return this.jdbcTemplate.update(
        "UPDATE dw_dmp SET projectAims = ?, projectSponsors = ?, duration = ?, organizations = ?, planAims = ? WHERE id = ?",
        dmp.getProjectAims(), dmp.getProjectSponsors(), dmp.getDuration(), dmp.getOrganizations(), dmp.getPlanAims(),
        dmp.getId());
  }

  private List<Integer> getDMPUsedDataTypes(BigInteger dmpid, DelType type) {
    if (log.isDebugEnabled())
      log.debug("execute getDMPUsedDataTypes for dmp [id: " + dmpid + " type: " + type.toString() + "]");
    String sql = "SELECT dw_dmp_formtypes.ftid FROM dw_dmp_formtypes "
        + "LEFT JOIN dw_formtypes ON dw_dmp_formtypes.ftid = dw_formtypes.id "
        + "WHERE dw_dmp_formtypes.dmpid = ? AND dw_formtypes.type = ? ";
    return jdbcTemplate.query(sql, new Object[] { dmpid, type.toString() }, new RowMapper<Integer>() {
      public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("ftid");
      }
    });
  }

  private int deleteDMPUsedDataTypes(BigInteger dmpid, int ftid) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute deleteDMPUsedDataTypes for [dmpid: " + dmpid + " ftid: " + ftid + "]");
    return this.jdbcTemplate.update("DELETE FROM dw_dmp_formtypes WHERE dmpid = ? AND ftid = ?", dmpid, ftid);
  }

  private int insertDMPUsedDataTypes(BigInteger dmpid, int ftid) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute instert DMPUsedDataTypes for [dmpid: " + dmpid + " ftid: " + ftid + "]");
    return this.jdbcTemplate.update("INSERT INTO dw_dmp_formtypes (dmpid, ftid) VALUES(?,?)", dmpid, ftid);
  }
}
