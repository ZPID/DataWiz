package de.zpid.datawiz.dao;

import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DAO Class for study
 * <p>
 * This file is part of the DataWiz distribution (https://github.com/ZPID/DataWiz).
 * Copyright (c) 2018 <a href="https://leibniz-psychology.org/">Leibniz Institute for Psychology Information (ZPID)</a>.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 * <p>
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <a href="http://www.gnu.org/licenses/">http://www.gnu.org/licenses/</a>.
 *
 * @author Ronny Boelter
 * @version 1.0
 **/
@Repository
@Scope("singleton")
public class StudyDAO {

    private final ClassPathXmlApplicationContext applicationContext;
    private final JdbcTemplate jdbcTemplate;

    private static final Logger log = LogManager.getLogger(StudyDAO.class);

    @Autowired
    public StudyDAO(ClassPathXmlApplicationContext applicationContext, JdbcTemplate jdbcTemplate) {
        super();
        log.info("Loading StudyDAO as @Scope(\"singleton\") and @Repository");
        this.applicationContext = applicationContext;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Finds all Studies
     *
     * @return {@link List} of {@link StudyDTO}
     */
    public List<StudyDTO> findAll() {
        log.trace("execute findAll ");
        String sql = "SELECT id, project_id, last_user_id, lastEdit, currentlyEdit, editSince,"
                + " editUserId, title, internalID, transTitle, sAbstract, sAbstractTrans" + " FROM dw_study";
        final List<StudyDTO> res = jdbcTemplate.query(sql, new Object[]{}, (rs, rowNum) -> setStudyDTO(rs, true, false));
        log.debug("leaving findAll with size: {}", res::size);
        return res;
    }

    /**
     * Finds all Studies of a project
     *
     * @param project long Project identifier
     * @return {@link List} of {@link StudyDTO}
     */
    public List<StudyDTO> findAllStudiesByProjectId(final ProjectDTO project) {
        log.trace("execute findAllStudiesByProjectId for project [id: {}; name: {}]", project::getId, project::getTitle);
        String sql = "SELECT id, project_id, last_user_id, lastEdit, currentlyEdit, editSince,"
                + " editUserId, title, internalID, transTitle, sAbstract, sAbstractTrans" + " FROM dw_study WHERE dw_study.project_id = ?";
        final List<StudyDTO> res = jdbcTemplate.query(sql, new Object[]{project.getId()}, (rs, rowNum) -> setStudyDTO(rs, true, false));
        log.debug("leaving findAllStudiesByProjectId with size: {}", res::size);
        return res;
    }

    /**
     * Finds a study by studyId and projectId
     *
     * @param studyId          long study identifier
     * @param projectId        long project identifier
     * @param onlyInfoMetaData set true if only a small set of data has to be returned, for all data set false
     * @param onlyLockInfo     set true if only lock information have to be returned
     * @return {@link StudyDTO}
     */
    public StudyDTO findById(final long studyId, final long projectId, final boolean onlyInfoMetaData, final boolean onlyLockInfo) {
        log.trace("execute findById for study [id: {}] from project [id: {}]", () -> studyId, () -> projectId);
        final StudyDTO res = jdbcTemplate.query("SELECT dw_study.* FROM dw_study WHERE dw_study.id = ? AND dw_study.project_id = ?",
                new Object[]{studyId, projectId}, rs -> {
                    if (rs.next()) {
                        return setStudyDTO(rs, onlyInfoMetaData, onlyLockInfo);
                    }
                    return null;
                });
        log.debug("leaving findByID with study: {}", () -> res != null ? res.getId() : "NULL");
        return res;
    }

    /**
     * Sets the edit lock of a study
     *
     * @param studyid    long study identifier
     * @param userid     long user identifier
     * @param deleteLock true if the lock has to be deleted, otherwise false
     * @return 1 if changed, otherwise 0
     */
    public int switchStudyLock(final long studyid, final long userid, final boolean deleteLock) {
        log.trace("execute switchStudyLock for [studyid: {}, userid: {}, deleteLock: {}]", () -> studyid, () -> userid, () -> deleteLock);
        int ret = this.jdbcTemplate.update("UPDATE dw_study SET currentlyEdit = ?, editSince = ?, editUserId = ? WHERE id = ?",
                deleteLock ? new Object[]{false, null, null, studyid} : new Object[]{true, LocalDateTime.now(), userid, studyid});
        log.debug("leaving switchStudyLock with result: {}", () -> ret);
        return ret;
    }

    /**
     * Updates a study
     *
     * @param study  {@link StudyDTO} contains study data
     * @param unlock if true, edit lock is refreshed
     * @return 1 if changed, otherwise 0
     */
    public int update(final StudyDTO study, final boolean unlock) {
        log.trace("execute update for [id: {}] with unlock [{}] for user [{}]", study::getId, () -> unlock, study::getLastUserId);
        String stmt = "UPDATE dw_study SET lastEdit = ?, last_user_id = ?, currentlyEdit = ?, editSince = ?, editUserId = ?,"
                + " title = ?, internalID = ?, transTitle = ?, sAbstract = ?, sAbstractTrans = ?, completeSel = ?,"
                + " excerpt = ?, prevWork = ?, prevWorkStr = ?, repMeasures = ?, timeDim = ?, surveyIntervention = ?,"
                + " experimentalIntervention = ?, testIntervention = ?, interTypeExp = ?, interTypeDes = ?,"
                + " interTypeLab = ?, randomization = ?, surveyType = ?, description = ?, population = ?, sampleSize = ?,"
                + " powerAnalysis = ?, intSampleSize = ?, obsUnit = ?, obsUnitOther = ?, multilevel = ?, sex = ?, age = ?,"
                + " specGroups = ?, country = ?, city = ?, region = ?, missings = ?, dataRerun = ?, responsibility = ?, responsibilityOther = ?,"
                + " collStart = ?, collEnd = ?, otherCMIP = ?, otherCMINP = ?, sampMethod = ?, sampMethodOther = ?, recruiting = ?,"
                + " otherSourFormat = ?, sourTrans = ?, otherSourTrans = ?, specCirc = ?, transDescr = ?, qualInd = ?, qualLim = ?,"
                + " irb = ?, irbName = ?, consent = ?, consentShare = ?, persDataColl = ?, persDataPres = ?, anonymProc = ?, copyright = ?,"
                + " copyrightHolder = ?, thirdParty = ?, thirdPartyHolder = ? WHERE id = ?";
        int ret = this.jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(stmt);
            setPSTMT(ps, study, true, unlock);
            return ps;
        });
        log.debug("leaving update with result: {}", () -> ret);
        return ret;
    }

    /**
     * Inserts a study
     *
     * @param study  {@link StudyDTO} contains study data
     * @param unlock if true, edit lock is refreshed
     * @return identifier of the saved study on success, otherwise -1
     */
    public long insert(final StudyDTO study, final boolean unlock) {
        log.trace("execute insert for [id: {}] with unlock [{}] for user [{}]", study::getId, () -> unlock, study::getLastUserId);
        final KeyHolder holder = new GeneratedKeyHolder();
        String stmt = "INSERT INTO dw_study (project_id, lastEdit, last_user_id, currentlyEdit, editSince, editUserId,"
                + " title, internalID, transTitle, sAbstract, sAbstractTrans, completeSel,"
                + " excerpt, prevWork, prevWorkStr, repMeasures, timeDim, surveyIntervention,"
                + " experimentalIntervention, testIntervention, interTypeExp, interTypeDes,"
                + " interTypeLab, randomization, surveyType, description, population, sampleSize,"
                + " powerAnalysis, intSampleSize, obsUnit, obsUnitOther, multilevel, sex, age,"
                + " specGroups, country, city, region, missings, dataRerun, responsibility, responsibilityOther,"
                + " collStart, collEnd, otherCMIP, otherCMINP, sampMethod, sampMethodOther, recruiting,"
                + " otherSourFormat, sourTrans, otherSourTrans, specCirc, transDescr, qualInd, qualLim,"
                + " irb, irbName, consent, consentShare, persDataColl, persDataPres, anonymProc, copyright," + " copyrightHolder, thirdParty, thirdPartyHolder)"
                + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        int ret = this.jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
            setPSTMT(ps, study, false, unlock);
            return ps;
        }, holder);
        final long key = (holder.getKey() != null && holder.getKey().longValue() > 0) ? holder.getKey().longValue() : -1;
        if (key > 0 && ret > 0) {
            study.setId(key);
        }
        log.debug("leaving insert with changes: {} and key: {}", () -> ret, () -> key);
        return key;
    }

    /**
     * Copies data from a ResultSet to a StudyDTO
     *
     * @param rs           {@link ResultSet}
     * @param overview     if true all data have to be returned
     * @param onlyLockInfo if true only lock information
     * @return {@link StudyDTO}
     * @throws SQLException Database Exceptions
     */
    private StudyDTO setStudyDTO(final ResultSet rs, final boolean overview, final boolean onlyLockInfo) throws SQLException {
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
                study.setInterTypeExp(rs.getString("interTypeExp"));
                study.setInterTypeDes(rs.getString("interTypeDes"));
                study.setInterTypeLab(rs.getString("interTypeLab"));
                study.setRandomization(rs.getString("randomization"));
                study.setSurveyType(rs.getString("surveyType"));
                study.setDescription(rs.getString("description"));
                // Sample Data
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
                // Survey Data
                study.setResponsibility(rs.getString("responsibility"));
                study.setResponsibilityOther(rs.getString("responsibilityOther"));
                study.setCollStart(rs.getDate("collStart") != null ? rs.getDate("collStart").toLocalDate() : null);
                study.setCollEnd(rs.getDate("collEnd") != null ? rs.getDate("collEnd").toLocalDate() : null);
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
                // Ethical Data
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

    /**
     * Sets a PreparedStatement from a StudyDTO
     *
     * @param ps     {@link PreparedStatement}
     * @param s      {@link StudyDTO} contains study data
     * @param update true if update, false if insert
     * @param unlock true to update edit lock information
     * @throws SQLException DataBase Exceptions
     */
    private void setPSTMT(PreparedStatement ps, StudyDTO s, final boolean update, final boolean unlock) throws SQLException {
        LocalDateTime now = LocalDateTime.now();
        int i = 1;
        if (!update) {
            ps.setLong(i++, s.getProjectId());
        }
        ps.setTimestamp(i++, Timestamp.valueOf(now));
        ps.setLong(i++, s.getLastUserId());
        if (unlock) {
            ps.setBoolean(i++, false);
            ps.setTimestamp(i++, null);
            ps.setString(i++, null);
        } else {
            ps.setBoolean(i++, true);
            ps.setTimestamp(i++, Timestamp.valueOf(now));
            ps.setLong(i++, s.getLastUserId());
        }
        ps.setString(i++, s.getTitle());
        ps.setString(i++, s.getInternalID());
        ps.setString(i++, s.getTransTitle());
        ps.setString(i++, s.getsAbstract());
        ps.setString(i++, s.getsAbstractTrans());
        ps.setString(i++, s.getCompleteSel());
        ps.setString(i++, s.getExcerpt());
        ps.setString(i++, s.getPrevWork());
        ps.setString(i++, s.getPrevWorkStr());
        ps.setString(i++, s.getRepMeasures());
        ps.setString(i++, s.getTimeDim());
        ps.setBoolean(i++, s.isSurveyIntervention());
        ps.setBoolean(i++, s.isExperimentalIntervention());
        ps.setBoolean(i++, s.isTestIntervention());
        ps.setString(i++, s.getInterTypeExp());
        ps.setString(i++, s.getInterTypeDes());
        ps.setString(i++, s.getInterTypeLab());
        ps.setString(i++, s.getRandomization());
        ps.setString(i++, s.getSurveyType());
        ps.setString(i++, s.getDescription());
        ps.setString(i++, s.getPopulation());
        ps.setString(i++, s.getSampleSize());
        ps.setString(i++, s.getPowerAnalysis());
        ps.setString(i++, s.getIntSampleSize());
        ps.setString(i++, s.getObsUnit());
        ps.setString(i++, s.getObsUnitOther());
        ps.setString(i++, s.getMultilevel());
        ps.setString(i++, s.getSex());
        ps.setString(i++, s.getAge());
        ps.setString(i++, s.getSpecGroups());
        ps.setString(i++, s.getCountry());
        ps.setString(i++, s.getCity());
        ps.setString(i++, s.getRegion());
        ps.setString(i++, s.getMissings());
        ps.setString(i++, s.getDataRerun());
        ps.setString(i++, s.getResponsibility());
        ps.setString(i++, s.getResponsibilityOther());
        ps.setDate(i++, s.getCollStart() != null ? Date.valueOf(s.getCollStart()) : null);
        ps.setDate(i++, s.getCollEnd() != null ? Date.valueOf(s.getCollEnd()) : null);
        ps.setString(i++, s.getOtherCMIP());
        ps.setString(i++, s.getOtherCMINP());
        ps.setString(i++, s.getSampMethod());
        ps.setString(i++, s.getSampMethodOther());
        ps.setString(i++, s.getRecruiting());
        ps.setString(i++, s.getOtherSourFormat());
        ps.setString(i++, s.getSourTrans());
        ps.setString(i++, s.getOtherSourTrans());
        ps.setString(i++, s.getSpecCirc());
        ps.setString(i++, s.getTransDescr());
        ps.setString(i++, s.getQualInd());
        ps.setString(i++, s.getQualLim());
        ps.setBoolean(i++, s.isIrb());
        ps.setString(i++, s.getIrbName());
        ps.setBoolean(i++, s.isConsent());
        ps.setBoolean(i++, s.isConsentShare());
        ps.setBoolean(i++, s.isPersDataColl());
        ps.setString(i++, s.getPersDataPres());
        ps.setString(i++, s.getAnonymProc());
        ps.setBoolean(i++, s.isCopyright());
        ps.setString(i++, s.getCopyrightHolder());
        ps.setBoolean(i++, s.isThirdParty());
        ps.setString(i++, s.getThirdPartyHolder());
        if (update)
            ps.setLong(i, s.getId());
    }

    /**
     * Deletes a study
     *
     * @param id long Study identifier
     */
    public void deleteStudy(final long id) {
        if (log.isDebugEnabled())
            log.debug("execute deleteStudy id: " + id);
        this.jdbcTemplate.update("DELETE FROM dw_study WHERE id = ? ", id);
    }
}
