package de.zpid.datawiz.dto;

import java.io.Serializable;

import javax.validation.constraints.Size;

public class InstrumentDTO implements Serializable {

  private static final long serialVersionUID = -2468624912740410851L;

  private long id;
  /** Study30 */
  @Size(min = 0, max = 500, groups = StudyDTO.StDesignVal.class)
  private String title;
  /** Study31 */
  @Size(min = 0, max = 1000, groups = StudyDTO.StDesignVal.class)
  private String author;
  /** Study32 */
  @Size(min = 0, max = 1000, groups = StudyDTO.StDesignVal.class)
  private String citation;
  /** Study33 */
  @Size(min = 0, max = 2000, groups = StudyDTO.StDesignVal.class)
  private String summary;
  /** Study34 */
  @Size(min = 0, max = 2000, groups = StudyDTO.StDesignVal.class)
  private String theoHint;
  /** Study35 */
  @Size(min = 0, max = 2000, groups = StudyDTO.StDesignVal.class)
  private String structure;
  /** Study36 */
  @Size(min = 0, max = 2000, groups = StudyDTO.StDesignVal.class)
  private String construction;
  /** Study37 */
  @Size(min = 0, max = 2000, groups = StudyDTO.StDesignVal.class)
  private String objectivity;
  /** Study38 */
  @Size(min = 0, max = 2000, groups = StudyDTO.StDesignVal.class)
  private String reliability;
  /** Study39 */
  @Size(min = 0, max = 2000, groups = StudyDTO.StDesignVal.class)
  private String validity;
  /** Study40 */
  @Size(min = 0, max = 2000, groups = StudyDTO.StDesignVal.class)
  private String norm;

  public InstrumentDTO(long id, String title, String author, String citation, String summary, String theoHint,
      String structure, String construction, String objectivity, String reliability, String validity, String norm) {
    super();
    this.id = id;
    this.title = title;
    this.author = author;
    this.citation = citation;
    this.summary = summary;
    this.theoHint = theoHint;
    this.structure = structure;
    this.construction = construction;
    this.objectivity = objectivity;
    this.reliability = reliability;
    this.validity = validity;
    this.norm = norm;
  }

  public InstrumentDTO() {
    super();
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getCitation() {
    return citation;
  }

  public void setCitation(String citation) {
    this.citation = citation;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getTheoHint() {
    return theoHint;
  }

  public void setTheoHint(String theoHint) {
    this.theoHint = theoHint;
  }

  public String getStructure() {
    return structure;
  }

  public void setStructure(String structure) {
    this.structure = structure;
  }

  public String getConstruction() {
    return construction;
  }

  public void setConstruction(String construction) {
    this.construction = construction;
  }

  public String getObjectivity() {
    return objectivity;
  }

  public void setObjectivity(String objectivity) {
    this.objectivity = objectivity;
  }

  public String getReliability() {
    return reliability;
  }

  public void setReliability(String reliability) {
    this.reliability = reliability;
  }

  public String getValidity() {
    return validity;
  }

  public void setValidity(String validity) {
    this.validity = validity;
  }

  public String getNorm() {
    return norm;
  }

  public void setNorm(String norm) {
    this.norm = norm;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((author == null) ? 0 : author.hashCode());
    result = prime * result + ((citation == null) ? 0 : citation.hashCode());
    result = prime * result + ((construction == null) ? 0 : construction.hashCode());
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + ((norm == null) ? 0 : norm.hashCode());
    result = prime * result + ((objectivity == null) ? 0 : objectivity.hashCode());
    result = prime * result + ((reliability == null) ? 0 : reliability.hashCode());
    result = prime * result + ((structure == null) ? 0 : structure.hashCode());
    result = prime * result + ((summary == null) ? 0 : summary.hashCode());
    result = prime * result + ((theoHint == null) ? 0 : theoHint.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    result = prime * result + ((validity == null) ? 0 : validity.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    InstrumentDTO other = (InstrumentDTO) obj;
    if (author == null) {
      if (other.author != null)
        return false;
    } else if (!author.equals(other.author))
      return false;
    if (citation == null) {
      if (other.citation != null)
        return false;
    } else if (!citation.equals(other.citation))
      return false;
    if (construction == null) {
      if (other.construction != null)
        return false;
    } else if (!construction.equals(other.construction))
      return false;
    if (id != other.id)
      return false;
    if (norm == null) {
      if (other.norm != null)
        return false;
    } else if (!norm.equals(other.norm))
      return false;
    if (objectivity == null) {
      if (other.objectivity != null)
        return false;
    } else if (!objectivity.equals(other.objectivity))
      return false;
    if (reliability == null) {
      if (other.reliability != null)
        return false;
    } else if (!reliability.equals(other.reliability))
      return false;
    if (structure == null) {
      if (other.structure != null)
        return false;
    } else if (!structure.equals(other.structure))
      return false;
    if (summary == null) {
      if (other.summary != null)
        return false;
    } else if (!summary.equals(other.summary))
      return false;
    if (theoHint == null) {
      if (other.theoHint != null)
        return false;
    } else if (!theoHint.equals(other.theoHint))
      return false;
    if (title == null) {
      if (other.title != null)
        return false;
    } else if (!title.equals(other.title))
      return false;
    if (validity == null) {
      if (other.validity != null)
        return false;
    } else if (!validity.equals(other.validity))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "InstrumentDTO [id=" + id + ", title=" + title + ", author=" + author + ", citation=" + citation
        + ", summary=" + summary + ", theoHint=" + theoHint + ", structure=" + structure + ", construction="
        + construction + ", objectivity=" + objectivity + ", reliability=" + reliability + ", validity=" + validity
        + ", norm=" + norm + "]";
  }

}
