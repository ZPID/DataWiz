package de.zpid.datawiz.enumeration;

/**
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
public enum DataWizErrorCodes {
    OK,
    SESSION_ERROR,
    DATABASE_ERROR,
    RECORD_SAVE_ERROR,
    NO_DATA_ERROR,
    MINIO_SAVE_ERROR,
    MINIO_READ_ERROR,
    MINIO_DELETE_ERROR,
    MISSING_PID_ERROR,
    MISSING_UID_ERROR,
    MISSING_STUDYID_ERROR,
    PROJECT_NOT_AVAILABLE,
    STUDY_NOT_AVAILABLE,
    STUDY_DELETE_ERROR,
    STUDY_ACCESS_LOCK,
    RECORD_NOT_AVAILABLE,
    RECORD_PARSING_ERROR,
    RECORD_VALIDATION_ERROR,
    DMP_VALIDATION_ERROR,
    RECORD_DELETE_ERROR,
    IMPORT_TYPE_NOT_SUPPORTED,
    DATE_PARSING_ERROR,
    IMPORT_FILE_IS_EMPTY,
    USER_ACCESS_PROJECT_PERMITTED,
    USER_ACCESS_STUDY_PERMITTED,
    USER_ACCESS_RECORD_PERMITTED,
    USER_NOT_IN_SESSION,
    CAPTCHA_OK,
    CAPTCHA_EMPTY,
    CAPTCHA_FAILURE
}
