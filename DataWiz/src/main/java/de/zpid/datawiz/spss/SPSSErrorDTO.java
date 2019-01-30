package de.zpid.datawiz.spss;


import java.io.Serializable;

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
public class SPSSErrorDTO implements Serializable {

    private static final long serialVersionUID = 3541681915680720633L;
    private String funcName;
    private String varName;
    private SPSSErrors error;

    public SPSSErrorDTO() {
        super();
    }

    public SPSSErrorDTO(String funcName, String varName, SPSSErrors error) {
        super();
        this.funcName = funcName;
        this.varName = varName;
        this.error = error;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public String getVarID() {
        return varName;
    }

    public void setVarID(String varName) {
        this.varName = varName;
    }

    public SPSSErrors getError() {
        return error;
    }

    public void setError(SPSSErrors error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "SPSSErrorDTO [funcName=" + funcName + ", varName=" + varName + ", error=" + error + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((error == null) ? 0 : error.hashCode());
        result = prime * result + ((funcName == null) ? 0 : funcName.hashCode());
        result = prime * result + ((varName == null) ? 0 : varName.hashCode());
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
        SPSSErrorDTO other = (SPSSErrorDTO) obj;
        if (error != other.error)
            return false;
        if (funcName == null) {
            if (other.funcName != null)
                return false;
        } else if (!funcName.equals(other.funcName))
            return false;
        if (varName == null) {
            return other.varName == null;
        } else return varName.equals(other.varName);
    }

}
