package de.zpid.datawiz.enumeration;

public enum DataWizErrorCodes {

  DATABASE_ERROR(100),
  RECORD_SAVE_ERROR(101),
  NO_DATA_ERROR(102),
  PROJECT_NOT_AVAILABLE(400),
  STUDY_NOT_AVAILABLE(401),
  RECORD_NOT_AVAILABLE(402),
  IMPORT_TYPE_NOT_SUPPORTED(500);

  private final int number;

  private DataWizErrorCodes(int number) {
    this.number = number;
  }

  public int getNumber() {
    return number;
  }

}
