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
 * Enumeration for SPSS missing type
 *
 * @author Ronny Boelter
 * @version 1.0
 */
public enum SPSSMissing {

    SPSS_NO_MISSVAL(0),
    SPSS_ONE_MISSVAL(1),
    SPSS_TWO_MISSVAL(2),
    SPSS_THREE_MISSVAL(3),
    SPSS_MISS_RANGE(-2),
    SPSS_MISS_RANGEANDVAL(-3),
    SPSS_UNKNOWN(-99);

    private final int number;

    private SPSSMissing(int s) {
        number = s;
    }

    public boolean equalsName(int number) {
        return this.number == number;
    }

    public int getNumber() {
        return this.number;
    }

    private static final Map<Integer, SPSSMissing> intToTypeMap = new HashMap<Integer, SPSSMissing>();

    static {
        for (SPSSMissing type : SPSSMissing.values()) {
            intToTypeMap.put(type.number, type);
        }
    }

    public static SPSSMissing fromInt(int i) {
        SPSSMissing type = intToTypeMap.get(Integer.valueOf(i));
        if (type == null)
            return SPSSMissing.SPSS_UNKNOWN;
        return type;
    }

}
