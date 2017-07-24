package de.zpid.datawiz.enumeration;

public enum DataWizErrorCodes {

  OK,
  DATABASE_ERROR,
  RECORD_SAVE_ERROR,
  NO_DATA_ERROR,
  MINIO_SAVE_ERROR,
  MISSING_PID_ERROR,
  MISSING_STUDYID_ERROR,
  PROJECT_NOT_AVAILABLE,
  STUDY_NOT_AVAILABLE,
  RECORD_NOT_AVAILABLE,
  IMPORT_TYPE_NOT_SUPPORTED,
  DATE_PARSING_ERROR,
  RECORD_VALIDATION_ERROR,
  IMPORT_FILE_IS_EMPTY,
  USER_ACCESS_PERMITTED;
}
