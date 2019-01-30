package de.zpid.datawiz.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * DAO Class for DataWiz Settings
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
public class SettingsDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final Logger log = LogManager.getLogger(SettingsDAO.class);

    @Autowired
    public SettingsDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * @param id
     * @return
     */
    public String findSettingValueById(final String id) {
        log.trace("Entering findSettingValueById for [id: {}]", () -> id);
        String ret = jdbcTemplate.queryForObject("SELECT * FROM dw_settings WHERE dw_settings.id = ?",
                new Object[]{id}, (resultSet, rowNum) -> resultSet.getString("value"));
        log.debug("Transaction \"findSettingValueById\" terminates with result: [length: {}]", (() -> ret != null ? ret.length() : "NULL"));
        return ret;
    }

    /**
     * @param id
     * @param value
     */
    public void updateSetting(final String id, final String value) {
        log.trace("Entering updateSetting for Setting [id {}]", () -> id);
        int ret = this.jdbcTemplate.update("UPDATE dw_settings SET value = ? WHERE id = ?", value, id);
        log.debug("Transaction \"updateSetting\" terminates with result: [{}]", () -> ret);
    }
}
