package de.zpid.datawiz.spss;

import java.util.HashMap;
import java.util.Map;

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
 * Attribution-NonCommercial-ShareAlike 4.0 International License</a>. <br />
 * <br />
 * Enumeration for SPSS alignment field
 *
 * @author Ronny Boelter
 * @version 1.0
 */
public enum SPSSAligment {

    SPSS_ALIGN_LEFT(0),
    SPSS_ALIGN_RIGHT(1),
    SPSS_ALIGN_CENTER(2),
    SPSS_UNKNOWN(-99);

    private final int number;

    private SPSSAligment(int s) {
        number = s;
    }

    public boolean equalsName(int number) {
        return this.number == number;
    }

    public int getNumber() {
        return this.number;
    }

    private static final Map<Integer, SPSSAligment> intToTypeMap = new HashMap<Integer, SPSSAligment>();

    static {
        for (SPSSAligment type : SPSSAligment.values()) {
            intToTypeMap.put(type.number, type);
        }
    }

    public static SPSSAligment fromInt(int i) {
        SPSSAligment type = intToTypeMap.get(i);
        if (type == null)
            return SPSSAligment.SPSS_UNKNOWN;
        return type;
    }

}
