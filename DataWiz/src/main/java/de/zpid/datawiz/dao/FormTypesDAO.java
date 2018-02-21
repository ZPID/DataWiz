package de.zpid.datawiz.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.FormTypesDTO;
import de.zpid.datawiz.enumeration.DWFieldTypes;

/**
 * This file is part of Datawiz
 * 
 * <b>Copyright 2018, Leibniz Institute for Psychology Information (ZPID), <a href="http://zpid.de" title="http://zpid.de">http://zpid.de</a>.</b><br />
 * <br />
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License" style= "border-width:0" src=
 * "https://i.creativecommons.org/l/by-nc-sa/4.0/80x15.png" /></a><br />
 * <span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">Datawiz</span> by
 * <a xmlns:cc="http://creativecommons.org/ns#" href="zpid.de" property="cc:attributionName" rel="cc:attributionURL"> Leibniz Institute for Psychology
 * Information (ZPID)</a> is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International License</a>. <br />
 * <br />
 * 
 * @author Ronny Boelter
 * @version 1.0
 *
 */
@Repository
@Scope("singleton")
public class FormTypesDAO {

	@Autowired
	protected ClassPathXmlApplicationContext applicationContext;
	@Autowired
	protected JdbcTemplate jdbcTemplate;

	private static Logger log = LogManager.getLogger(FormTypesDAO.class);

	/**
	 * Instantiates a new FormTypesDAO.
	 */
	public FormTypesDAO() {
		super();
		log.info("Loading FormTypesDAO as Singleton and Service");
	}

	/**
	 * This function returns all matching FormTypes entities from the table dmp_formtypes depending on the passed parameters.
	 * 
	 * @param active
	 *          If true, only active FormTypes will be returned
	 * @param type
	 *          Type of the form type to be selected
	 * @return List of FormTypesDTO, which contains the selected subset
	 * @throws Exception
	 */
	public List<FormTypesDTO> findAllByType(final boolean active, final DWFieldTypes type) throws Exception {
		log.trace("Entering getAllByType for DWFieldType [type: {}; active: {}]", () -> type, () -> active);
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM dw_formtypes WHERE");
		if (active)
			sb.append(" dw_formtypes.active = true AND");
		sb.append(" dw_formtypes.type = ? ORDER BY dw_formtypes.sort");
		final List<FormTypesDTO> ret = jdbcTemplate.query(sb.toString(), new Object[] { type.toString() }, new RowMapper<FormTypesDTO>() {
			public FormTypesDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				FormTypesDTO dt = (FormTypesDTO) applicationContext.getBean("FormTypesDTO");
				dt.setId(rs.getInt("id"));
				dt.setNameDE(rs.getString("name_de"));
				dt.setNameEN(rs.getString("name_en"));
				dt.setActive(rs.getBoolean("active"));
				dt.setInvestPresent(rs.getBoolean("investpresent"));
				dt.setSort(rs.getInt("sort"));
				dt.setType(DWFieldTypes.valueOf(rs.getString("type")));
				return dt;
			}
		});
		log.debug("Transaction \"getAllByType\" terminates with result: [length: {}]", () -> ((ret != null) ? ret.size() : "null"));
		return ret;
	}

	/**
	 * This function returns a list of the selected form type for a DMP or study. To do this, the relation tables dw_dmp_formtypes or dw_study_formtypes are
	 * searched, depending on the passed parameters.
	 * 
	 * @param identifier
	 *          Study, or DMP identifier. Usage depends on boolean value isStudy
	 * @param type
	 *          Type of the form type to be selected
	 * @param isStudy
	 *          Has to be set true for finding study FormTypes, false for DMP FormTypes
	 * @return List of FormTypesDTO, which contains the selected subset
	 * @throws Exception
	 */
	public List<Integer> findSelectedFormTypesByIdAndType(final long identifier, final DWFieldTypes type, final boolean isStudy) throws Exception {
		log.trace("Entering getSelectedFormTypesByIdAndType for DWFieldType [id: {}; type: {}; isStudy: {}]", () -> identifier, () -> type.name(), () -> isStudy);
		String sql;
		if (isStudy)
			sql = "SELECT dw_study_formtypes.ftid FROM dw_study_formtypes " + "LEFT JOIN dw_formtypes ON dw_study_formtypes.ftid = dw_formtypes.id "
			    + "WHERE dw_study_formtypes.studyid = ? AND dw_formtypes.type = ? ";
		else
			sql = "SELECT dw_dmp_formtypes.ftid FROM dw_dmp_formtypes " + "LEFT JOIN dw_formtypes ON dw_dmp_formtypes.ftid = dw_formtypes.id "
			    + "WHERE dw_dmp_formtypes.dmpid = ? AND dw_formtypes.type = ? ";
		final List<Integer> ret = jdbcTemplate.query(sql, new Object[] { identifier, type.toString() }, new RowMapper<Integer>() {
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getInt("ftid");
			}
		});
		log.debug("Transaction \"getSelectedFormTypesByIdAndType\" terminates with result: [length: {}]", () -> ((ret != null) ? ret.size() : "null"));
		return ret;
	}

	/**
	 * This functions deletes all FormTypes Study/DMP entities from the relation tables dw_study_formtypes or dw_dmp_formtypes depending on the passed parameters.
	 * 
	 * @param identifier
	 *          Study, or DMP identifier. Usage depends on boolean value isStudy
	 * @param types
	 *          List of all FormTypes which have to be deleted
	 * @param isStudy
	 *          Has to be set true for deleting study FormTypes, false for DMP FormTypes
	 * @return List of results
	 */
	public int[] deleteSelectedFormType(final long identifier, final List<Integer> types, final boolean isStudy) {
		log.trace("Entering deleteSelectedFormType [size: {}]", () -> types.size());
		String query = null;
		if (isStudy)
			query = "DELETE FROM dw_study_formtypes WHERE studyid = ? AND ftid = ?";
		else
			query = "DELETE FROM dw_dmp_formtypes WHERE dmpid = ? AND ftid = ?";
		int[] ret = this.jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setLong(1, identifier);
				ps.setLong(2, types.get(i));
			}

			public int getBatchSize() {
				return types.size();
			}
		});
		log.debug("Transaction \"deleteSelectedFormType\" terminates with result: [length: {}]", () -> ret.length);
		return ret;
	}

	/**
	 * This functions saves a set of FormTypes Study/DMP entities into the relation tables dw_study_formtypes or dw_dmp_formtypes depending on the passed
	 * parameters.
	 * 
	 * @param identifier
	 *          Study, or DMP identifier. Usage depends on boolean value isStudy
	 * @param types
	 *          List of all FormTypes which have to be saved
	 * @param isStudy
	 *          Has to be set true for saving study FormTypes, false for DMP FormTypes
	 * @return List of results
	 */
	public int[] insertSelectedFormType(final long dmpOrStudyID, final List<Integer> types, final boolean isStudy) {
		log.trace("Entering insertSelectedFormType [size: {}]", () -> types.size());
		String query = null;
		if (isStudy)
			query = "INSERT INTO dw_study_formtypes (studyid, ftid) VALUES(?,?)";
		else
			query = "INSERT INTO dw_dmp_formtypes (dmpid, ftid) VALUES(?,?)";
		int[] ret = this.jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setLong(1, dmpOrStudyID);
				ps.setLong(2, types.get(i));
			}

			public int getBatchSize() {
				return types.size();
			}
		});
		log.debug("Transaction \"insertSelectedFormType\" terminates with result: [length: {}]", () -> ret.length);
		return ret;
	}
}
