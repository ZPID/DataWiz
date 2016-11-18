package de.zpid.datawiz.dto;

import java.io.Serializable;

import de.zpid.datawiz.enumeration.VariableStatus;

public class RecordCompareDTO implements Serializable {

  private static final long serialVersionUID = -172918486411583983L;
  private VariableStatus varStatus;
  private int movedFrom;
  private int movedTo;
  private boolean keepExpMeta;
  private String message;
  private long equalVarId;
  private String bootstrapItemColor;

  public VariableStatus getVarStatus() {
    return varStatus;
  }

  public void setVarStatus(VariableStatus varStatus) {
    this.varStatus = varStatus;
  }

  public int getMovedFrom() {
    return movedFrom;
  }

  public void setMovedFrom(int movedFrom) {
    this.movedFrom = movedFrom;
  }

  public int getMovedTo() {
    return movedTo;
  }

  public void setMovedTo(int movedTo) {
    this.movedTo = movedTo;
  }

  public boolean isKeepExpMeta() {
    return keepExpMeta;
  }

  public void setKeepExpMeta(boolean keepExpMeta) {
    this.keepExpMeta = keepExpMeta;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public long getEqualVarId() {
    return equalVarId;
  }

  public void setEqualVarId(long equalVarId) {
    this.equalVarId = equalVarId;
  }

  public String getBootstrapItemColor() {
    return bootstrapItemColor;
  }

  public void setBootstrapItemColor(String bootstrapItemColor) {
    this.bootstrapItemColor = bootstrapItemColor;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  @Override
  public String toString() {
    return "RecordCompareDTO [varStatus=" + varStatus + ", movedFrom=" + movedFrom + ", movedTo=" + movedTo
        + ", keepExpMeta=" + keepExpMeta + ", message=" + message + ", equalVarId=" + equalVarId
        + ", bootstrapItemColor=" + bootstrapItemColor + "]";
  }

}
