package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import de.zpid.spss.dto.SPSSErrorDTO;
import de.zpid.spss.dto.SPSSFileDTO;
import de.zpid.spss.dto.SPSSValueLabelDTO;
import de.zpid.spss.dto.SPSSVarTDO;
import de.zpid.spss.util.SPSSCompression;
import de.zpid.spss.util.SPSSPageEncoding;

public class RecordDTO extends SPSSFileDTO implements Serializable {

  private static final long serialVersionUID = 1689784689090294373L;
  // DB Table dw_record
  private long id;
  private long studyId;
  private String recordName;
  private LocalDateTime created;
  private String description;
  private String fileName;
  // DB Table dw_record_metadata
  private long versionId;
  private String changeLog;
  private LocalDateTime changed;
  private String changedBy;
  private int fileSize;
  private String checksum;
  private boolean masterRecord;

  

  public RecordDTO() {
    super();
  }

  public RecordDTO(String filepath) {
    super(filepath);
  }

  public RecordDTO(SPSSFileDTO spssFile) {
    super(spssFile.isU8(), spssFile.gethFile(), spssFile.getFilepath(), spssFile.getPassword(),
        spssFile.getNumberOfVariables(), spssFile.getNumberOfFileAttributes(), spssFile.getNumberOfCases(),
        spssFile.getEstimatedNofCases(), spssFile.getCaseSize(), spssFile.getCaseWeightVar(), spssFile.getCompression(),
        spssFile.getDateNumOfElements(), spssFile.getDateInfo(), spssFile.getFileCodePage(), spssFile.getFileEncoding(),
        spssFile.getFileIdString(), spssFile.getInterfaceEncoding(), spssFile.getMultRespDefsEx(),
        spssFile.getVariables(), spssFile.getAttributes(), spssFile.getDataMatrix(), spssFile.getDataMatrixJson(),
        spssFile.getErrors());
  }

  

}
