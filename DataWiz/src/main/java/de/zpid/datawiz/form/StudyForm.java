package de.zpid.datawiz.form;

import java.io.Serializable;

import javax.validation.Valid;

import de.zpid.datawiz.dto.StudyDTO;

public class StudyForm implements Serializable {
  
  private static final long serialVersionUID = 7871841325048805095L;
  @Valid
  private StudyDTO study;
  
  
}
