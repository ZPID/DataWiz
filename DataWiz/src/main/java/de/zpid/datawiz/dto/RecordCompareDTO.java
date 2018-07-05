package de.zpid.datawiz.dto;

import de.zpid.datawiz.enumeration.VariableStatus;

import java.io.Serializable;
import java.util.Objects;

/**
 * RecordCompare Data Transfer Object
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
 */
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordCompareDTO that = (RecordCompareDTO) o;
        return movedFrom == that.movedFrom &&
                movedTo == that.movedTo &&
                keepExpMeta == that.keepExpMeta &&
                equalVarId == that.equalVarId &&
                varStatus == that.varStatus &&
                Objects.equals(message, that.message) &&
                Objects.equals(bootstrapItemColor, that.bootstrapItemColor);
    }

    @Override
    public int hashCode() {

        return Objects.hash(varStatus, movedFrom, movedTo, keepExpMeta, message, equalVarId, bootstrapItemColor);
    }

    @Override
    public String toString() {
        return "RecordCompareDTO{" +
                "varStatus=" + varStatus +
                ", movedFrom=" + movedFrom +
                ", movedTo=" + movedTo +
                ", keepExpMeta=" + keepExpMeta +
                ", message='" + message + '\'' +
                ", equalVarId=" + equalVarId +
                ", bootstrapItemColor='" + bootstrapItemColor + '\'' +
                '}';
    }
}
