package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.enumeration.InterventionTypes;

@Repository
@Scope("singleton")
public class StudyDAO extends SuperDAO {

  private static Logger log = LogManager.getLogger(StudyDAO.class);

  public StudyDAO() {
    super();
    log.info("Loading StudyDAO as Singleton and Service");
  }

  public List<StudyDTO> findAllStudiesByProjectId(final ProjectDTO project) throws Exception {
    log.trace("execute findAllStudiesByProjectId for project [id: {}; name: {}]", () -> project.getId(),
        () -> project.getTitle());
    String sql = "SELECT id, project_id, last_user_id, lastEdit, currentlyEdit, editSince,"
        + " editUserId, title, internalID, transTitle, sAbstract, sAbstractTrans"
        + " FROM dw_study WHERE dw_study.project_id = ?";
    final List<StudyDTO> res = jdbcTemplate.query(sql, new Object[] { project.getId() }, new RowMapper<StudyDTO>() {
      public StudyDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return setStudyDTO(rs, true, false);
      }
    });
    log.debug("leaving findAllStudiesByProjectId with size: {}", () -> res.size());
    return res;
  }

  public StudyDTO findById(final long studyId, final long projectId, final boolean onlyLockInfo) throws Exception {
    log.trace("execute findById for study [id: {}] from project [id: {}]", () -> studyId, () -> projectId);
    final StudyDTO res = jdbcTemplate.query(
        "SELECT dw_study.* FROM dw_study WHERE dw_study.id = ? AND dw_study.project_id = ?",
        new Object[] { studyId, projectId }, new ResultSetExtractor<StudyDTO>() {
          @Override
          public StudyDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next()) {
              return setStudyDTO(rs, false, onlyLockInfo);
            }
            return null;
          }
        });
    log.debug("leaving findByID with study: {}", () -> res != null ? res.getId() : "NULL");
    return res;
  }

  public int switchStudyLock(final long studyid, final long userid, final boolean deleteLock) {
    log.trace("execute switchStudyLock for [studyid: {}, userid: {}, deleteLock: {}]", () -> studyid, () -> userid,
        () -> deleteLock);
    int ret = this.jdbcTemplate.update(
        "UPDATE dw_study SET currentlyEdit = ?, editSince = ?, editUserId = ? WHERE id = ?",
        deleteLock ? new Object[] { false, null, null, studyid }
            : new Object[] { true, LocalDateTime.now(), userid, studyid });
    log.debug("leaving switchStudyLock with result: {}", () -> ret);
    return ret;
  }

  public int updateStudy(final StudyDTO study, final boolean unlock, final long userId) throws Exception {
    log.trace("execute updateStudy for [id: {}] with unlock [{}] for user [{}]", () -> study.getId(), () -> unlock,
        () -> userId);
    int ret = this.jdbcTemplate.update(
        "UPDATE dw_study SET lastEdit = ?, last_user_id = ?, currentlyEdit = ?, editSince = ?, editUserId = ?,"
            + " title = ?, internalID = ?, transTitle = ?, sAbstract = ?, sAbstractTrans = ?, completeSel = ?,"
            + " excerpt = ?, prevWork = ?, prevWorkStr = ?, repMeasures = ?, timeDim = ?, surveyIntervention = ?,"
            + " experimentalIntervention = ?, testIntervention = ?" + " WHERE id = ?",
        setParams(study, true, unlock, userId).toArray());
    log.debug("leaving updateStudy with result: {}", () -> ret);
    return ret;
  }

  private StudyDTO setStudyDTO(final ResultSet rs, final boolean overview, final boolean onlyLockInfo)
      throws SQLException {
    StudyDTO study = (StudyDTO) applicationContext.getBean("StudyDTO");
    study.setId(rs.getLong("id"));
    study.setProjectId(rs.getLong("project_id"));
    study.setTimestamp(rs.getTimestamp("lastEdit") != null ? rs.getTimestamp("lastEdit").toLocalDateTime() : null);
    study.setLastUserId(rs.getLong("last_user_id"));
    study.setCurrentlyEdit(rs.getBoolean("currentlyEdit"));
    study.setEditSince(rs.getTimestamp("editSince") != null ? rs.getTimestamp("editSince").toLocalDateTime() : null);
    study.setEditUserId(rs.getLong("editUserId"));
    if (!onlyLockInfo) {
      // Administrative Data
      study.setTitle(rs.getString("title"));
      study.setInternalID(rs.getString("internalID"));
      study.setTransTitle(rs.getString("transTitle"));
      study.setsAbstract(rs.getString("sAbstract"));
      study.setsAbstractTrans(rs.getString("sAbstractTrans"));
      if (!overview) {
        study.setCompleteSel(rs.getString("completeSel"));
        study.setExcerpt(rs.getString("excerpt"));
        study.setPrevWork(rs.getString("prevWork"));
        study.setPrevWorkStr(rs.getString("prevWorkStr"));
        // Design Data
        study.setRepMeasures(rs.getString("repMeasures"));
        study.setTimeDim(rs.getString("timeDim"));
        study.setSurveyIntervention(rs.getBoolean("surveyIntervention"));
        study.setExperimentalIntervention(rs.getBoolean("experimentalIntervention"));
        study.setTestIntervention(rs.getBoolean("testIntervention"));
        study.setInterTypeExp(
            rs.getString("interTypeExp") != null ? InterventionTypes.valueOf(rs.getString("interTypeExp")) : null);
        study.setInterTypeDes(
            rs.getString("interTypeDes") != null ? InterventionTypes.valueOf(rs.getString("interTypeDes")) : null);
        study.setInterTypeLab(
            rs.getString("interTypeLab") != null ? InterventionTypes.valueOf(rs.getString("interTypeLab")) : null);
        study.setRandomization(
            rs.getString("randomization") != null ? InterventionTypes.valueOf(rs.getString("randomization")) : null);
        study.setSurveyType(
            rs.getString("surveyType") != null ? InterventionTypes.valueOf(rs.getString("surveyType")) : null);
        study.setDescription(rs.getString("description"));
        study.setPopulation(rs.getString("population"));
        study.setSampleSize(rs.getString("sampleSize"));
        study.setPowerAnalysis(rs.getString("powerAnalysis"));
        study.setIntSampleSize(rs.getString("intSampleSize"));
        study.setObsUnit(rs.getString("obsUnit"));
        study.setObsUnitOther(rs.getString("obsUnitOther"));
        study.setMultilevel(rs.getString("multilevel"));
        study.setSex(rs.getString("sex"));
        study.setAge(rs.getString("age"));
        study.setSpecGroups(rs.getString("specGroups"));
        study.setCountry(rs.getString("country"));
        study.setCity(rs.getString("city"));
        study.setRegion(rs.getString("region"));
        study.setMissings(rs.getString("missings"));
        study.setDataRerun(rs.getString("dataRerun"));
        study.setResponsibility(rs.getString("responsibility"));
        study.setResponsibilityOther(rs.getString("responsibilityOther"));
        study.setCollStart(rs.getDate("collStart") != null ? rs.getDate("collStart").toLocalDate() : null);
        study.setCollEnd(rs.getDate("collEnd") != null ? rs.getDate("collSEnd").toLocalDate() : null);
        study.setOtherCMIP(rs.getString("otherCMIP"));
        study.setOtherCMINP(rs.getString("otherCMINP"));
        study.setSampMethod(rs.getString("sampMethod"));
        study.setSampMethodOther(rs.getString("sampMethodOther"));
        study.setRecruiting(rs.getString("recruiting"));
        study.setOtherSourFormat(rs.getString("otherSourFormat"));
        study.setSourTrans(rs.getString("sourTrans"));
        study.setOtherSourTrans(rs.getString("otherSourTrans"));
        study.setSpecCirc(rs.getString("specCirc"));
        study.setTransDescr(rs.getString("transDescr"));
        study.setQualInd(rs.getString("qualInd"));
        study.setQualLim(rs.getString("qualLim"));
        study.setIrb(rs.getBoolean("irb"));
        study.setIrbName(rs.getString("irbName"));
        study.setConsent(rs.getBoolean("consent"));
        study.setConsentShare(rs.getBoolean("consentShare"));
        study.setPersDataColl(rs.getBoolean("persDataColl"));
        study.setPersDataPres(rs.getString("persDataPres"));
        study.setAnonymProc(rs.getString("anonymProc"));
        study.setCopyright(rs.getBoolean("copyright"));
        study.setCopyrightHolder(rs.getString("copyrightHolder"));
        study.setThirdParty(rs.getBoolean("thirdParty"));
        study.setThirdPartyHolder(rs.getString("thirdPartyHolder"));
      }
    }
    return study;
  }

  private List<Object> setParams(StudyDTO study, final boolean update, final boolean unlock, final long userID)
      throws Exception {
    List<Object> oList = new LinkedList<Object>();
    LocalDateTime now = LocalDateTime.now();
    if (!update) {
      oList.add(study.getProjectId());
    }
    oList.add(now);
    oList.add(study.getLastUserId());
    if (unlock) {
      oList.add(false);
      oList.add(null);
      oList.add(null);
    } else {
      oList.add(true);
      oList.add(now);
      oList.add(userID);
    }
    oList.add(study.getTitle());
    oList.add(study.getInternalID());
    oList.add(study.getTransTitle());
    oList.add(study.getsAbstract());
    oList.add(study.getsAbstractTrans());
    oList.add(study.getCompleteSel());
    oList.add(study.getExcerpt());
    oList.add(study.getPrevWork());
    oList.add(study.getPrevWorkStr());
    oList.add(study.getRepMeasures());
    oList.add(study.getTimeDim());
    oList.add(study.isSurveyIntervention());
    oList.add(study.isExperimentalIntervention());
    oList.add(study.isTestIntervention());
    if (update)
      oList.add(study.getId());
    return oList;
  }
}
