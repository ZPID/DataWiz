package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.SideMenuDTO;
import de.zpid.datawiz.dto.UserDTO;

@Repository
@Scope("singleton")
public class SideMenuDAO {

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	private static Logger log = LogManager.getLogger(SideMenuDAO.class);

	public List<SideMenuDTO> findProjectsByUser(final UserDTO user) throws Exception {
		log.trace("Entering findProjectsByUser for user [id: {}; email: {}]", () -> user.getId(), () -> user.getEmail());
		String sql = "SELECT dw_project.id, dw_project.title FROM dw_user_roles " + "LEFT JOIN dw_project ON dw_user_roles.project_id = dw_project.id "
		    + "LEFT JOIN dw_roles ON dw_user_roles.role_id = dw_roles.id " + "WHERE dw_user_roles.user_id = ? AND dw_user_roles.project_id > 0 "
		    + "GROUP BY dw_project.id";
		List<SideMenuDTO> ret = jdbcTemplate.query(sql, new Object[] { user.getId() }, new RowMapper<SideMenuDTO>() {
			public SideMenuDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				SideMenuDTO smdto = new SideMenuDTO();
				smdto.setId(rs.getLong("id"));
				smdto.setTitle(rs.getString("title"));
				return smdto;
			}
		});
		log.debug("Transaction \"findProjectsByUser\" terminates with result: [lenght: {}]", () -> ret != null ? ret.size() : "null");
		return ret;
	}

	public List<SideMenuDTO> findAllStudiesByProjectId(final long pid) throws Exception {
		log.trace("execute findAllStudiesByProjectId for project [id: {}]", () -> pid);
		String sql = "SELECT id, title FROM dw_study WHERE dw_study.project_id = ?";
		final List<SideMenuDTO> res = jdbcTemplate.query(sql, new Object[] { pid }, new RowMapper<SideMenuDTO>() {
			public SideMenuDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				SideMenuDTO smdto = new SideMenuDTO();
				smdto.setId(rs.getLong("id"));
				smdto.setTitle(rs.getString("title"));
				return smdto;
			}
		});
		log.debug("leaving findAllStudiesByProjectId with size: {}", () -> res.size());
		return res;
	}

	public SideMenuDTO findById(final long studyId, final long projectId) throws Exception {
		log.trace("execute findById for study [id: {}] from project [id: {}]", () -> studyId, () -> projectId);
		final SideMenuDTO res = jdbcTemplate.query("SELECT dw_study.id, dw_study.title FROM dw_study WHERE dw_study.id = ? AND dw_study.project_id = ?",
		    new Object[] { studyId, projectId }, new ResultSetExtractor<SideMenuDTO>() {
			    @Override
			    public SideMenuDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
				    if (rs.next()) {
					    SideMenuDTO smdto = new SideMenuDTO();
					    smdto.setId(rs.getLong("id"));
					    smdto.setTitle(rs.getString("title"));
					    return smdto;
				    }
				    return null;
			    }
		    });
		log.debug("leaving findByID with study: {}", () -> res != null ? res.getId() : "NULL");
		return res;
	}

	public List<SideMenuDTO> findRecordsWithStudyID(final long studyId) throws Exception {
		log.trace("Entering findRecordsWithStudyID [studyId: {}]", () -> studyId);
		String sql = "SELECT * FROM dw_record WHERE dw_record.study_id  = ?";
		final List<SideMenuDTO> cRecords = this.jdbcTemplate.query(sql, new Object[] { studyId }, new RowMapper<SideMenuDTO>() {
			public SideMenuDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				SideMenuDTO smdto = new SideMenuDTO();
				smdto.setId(rs.getLong("id"));
				smdto.setTitle(rs.getString("name"));
				return smdto;
			}
		});
		log.debug("Transaction \"findRecordsWithStudyID\" terminates with result: [lenght: {}]", () -> cRecords.size());
		return cRecords;
	}

}
