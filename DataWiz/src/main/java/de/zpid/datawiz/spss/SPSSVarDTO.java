package de.zpid.datawiz.spss;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This file is part of SPSS_IO_UTILS<br />
 *
 * <b>Copyright 2016, Leibniz Institute for Psychology Information (ZPID),
 * <a href="http://zpid.de" title="http://zpid.de">http://zpid.de</a>.</b><br />
 * <br />
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License" style=
 * "border-width:0" src="https://i.creativecommons.org/l/by-nc-sa/4.0/80x15.png" /></a><br />
 * <span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">Datawiz</span> by
 * <a xmlns:cc="http://creativecommons.org/ns#" href="zpid.de" property="cc:attributionName" rel="cc:attributionURL">
 * Leibniz Institute for Psychology Information (ZPID)</a> is licensed under a
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International License</a>.
 *
 * @author Ronny Boelter
 * @version 1.0
 */
public class SPSSVarDTO implements Serializable {

    private static final long serialVersionUID = 3107163373622814581L;
    // DBS FIELDS ID
    private long id;
    private double varHandle;
    private String name;
    private SPSSVarTypes type;
    private int varType;
    private int decimals;
    private int width;
    private String label;
    private List<SPSSValueLabelDTO> values;
    private SPSSMissing missingFormat;
    private String missingVal1;
    private String missingVal2;
    private String missingVal3;
    private int columns;
    private SPSSAligment aligment;
    private SPSSMeasLevel measureLevel;
    private SPSSRoleCodes role;
    private int numOfAttributes;
    private List<SPSSValueLabelDTO> attributes;
    private List<SPSSValueLabelDTO> dw_attributes;
    private int position;

    public SPSSVarDTO() {
        super();
        this.values = new ArrayList<SPSSValueLabelDTO>();
        this.attributes = new ArrayList<SPSSValueLabelDTO>();
        this.dw_attributes = new ArrayList<SPSSValueLabelDTO>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getVarHandle() {
        return varHandle;
    }

    public void setVarHandle(double varHandle) {
        this.varHandle = varHandle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SPSSVarTypes getType() {
        return type;
    }

    public void setType(SPSSVarTypes type) {
        this.type = type;
    }

    public int getVarType() {
        return varType;
    }

    public void setVarType(int varType) {
        this.varType = varType;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<SPSSValueLabelDTO> getValues() {
        return values;
    }

    public void setValues(List<SPSSValueLabelDTO> values) {
        this.values = values;
    }

    public SPSSMissing getMissingFormat() {
        return missingFormat;
    }

    public void setMissingFormat(SPSSMissing missingFormat) {
        this.missingFormat = missingFormat;
    }

    public String getMissingVal1() {
        return missingVal1;
    }

    public void setMissingVal1(String missingVal1) {
        this.missingVal1 = missingVal1;
    }

    public String getMissingVal2() {
        return missingVal2;
    }

    public void setMissingVal2(String missingVal2) {
        this.missingVal2 = missingVal2;
    }

    public String getMissingVal3() {
        return missingVal3;
    }

    public void setMissingVal3(String missingVal3) {
        this.missingVal3 = missingVal3;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public SPSSAligment getAligment() {
        return aligment;
    }

    public void setAligment(SPSSAligment aligment) {
        this.aligment = aligment;
    }

    public SPSSMeasLevel getMeasureLevel() {
        return measureLevel;
    }

    public void setMeasureLevel(SPSSMeasLevel measureLevel) {
        this.measureLevel = measureLevel;
    }

    public SPSSRoleCodes getRole() {
        return role;
    }

    public void setRole(SPSSRoleCodes role) {
        this.role = role;
    }

    public int getNumOfAttributes() {
        return numOfAttributes;
    }

    public void setNumOfAttributes(int numOfAttributes) {
        this.numOfAttributes = numOfAttributes;
    }

    public List<SPSSValueLabelDTO> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<SPSSValueLabelDTO> attributes) {
        this.attributes = attributes;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<SPSSValueLabelDTO> getDw_attributes() {
        return dw_attributes;
    }

    public void setDw_attributes(List<SPSSValueLabelDTO> dw_attributes) {
        this.dw_attributes = dw_attributes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((aligment == null) ? 0 : aligment.hashCode());
        result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
        result = prime * result + columns;
        result = prime * result + decimals;
        result = prime * result + ((dw_attributes == null) ? 0 : dw_attributes.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((measureLevel == null) ? 0 : measureLevel.hashCode());
        result = prime * result + ((missingFormat == null) ? 0 : missingFormat.hashCode());
        result = prime * result + ((missingVal1 == null) ? 0 : missingVal1.hashCode());
        result = prime * result + ((missingVal2 == null) ? 0 : missingVal2.hashCode());
        result = prime * result + ((missingVal3 == null) ? 0 : missingVal3.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + numOfAttributes;
        result = prime * result + position;
        result = prime * result + ((role == null) ? 0 : role.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((values == null) ? 0 : values.hashCode());
        long temp;
        temp = Double.doubleToLongBits(varHandle);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + varType;
        result = prime * result + width;
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
        SPSSVarDTO other = (SPSSVarDTO) obj;
        if (aligment != other.aligment)
            return false;
        if (attributes == null) {
            if (other.attributes != null)
                return false;
        } else if (!attributes.equals(other.attributes))
            return false;
        if (columns != other.columns)
            return false;
        if (decimals != other.decimals)
            return false;
        if (dw_attributes == null) {
            if (other.dw_attributes != null)
                return false;
        } else if (!dw_attributes.equals(other.dw_attributes))
            return false;
        if (id != other.id)
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (measureLevel != other.measureLevel)
            return false;
        if (missingFormat != other.missingFormat)
            return false;
        if (missingVal1 == null) {
            if (other.missingVal1 != null)
                return false;
        } else if (!missingVal1.equals(other.missingVal1))
            return false;
        if (missingVal2 == null) {
            if (other.missingVal2 != null)
                return false;
        } else if (!missingVal2.equals(other.missingVal2))
            return false;
        if (missingVal3 == null) {
            if (other.missingVal3 != null)
                return false;
        } else if (!missingVal3.equals(other.missingVal3))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (numOfAttributes != other.numOfAttributes)
            return false;
        if (position != other.position)
            return false;
        if (role != other.role)
            return false;
        if (type != other.type)
            return false;
        if (values == null) {
            if (other.values != null)
                return false;
        } else if (!values.equals(other.values))
            return false;
        if (Double.doubleToLongBits(varHandle) != Double.doubleToLongBits(other.varHandle))
            return false;
        if (varType != other.varType)
            return false;
        if (width != other.width)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SPSSVarDTO [id=" + id + ", varHandle=" + varHandle + ", name=" + name + ", type=" + type + ", varType="
                + varType + ", decimals=" + decimals + ", width=" + width + ", label=" + label + ", values=" + values
                + ", missingFormat=" + missingFormat + ", missingVal1=" + missingVal1 + ", missingVal2=" + missingVal2
                + ", missingVal3=" + missingVal3 + ", columns=" + columns + ", aligment=" + aligment + ", measureLevel="
                + measureLevel + ", role=" + role + ", numOfAttributes=" + numOfAttributes + ", attributes=" + attributes
                + ", dw_attributes=" + dw_attributes + ", position=" + position + "]";
    }

}
