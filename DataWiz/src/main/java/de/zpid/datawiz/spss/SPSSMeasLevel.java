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
 * Enumeration for SPSS measure type
 *
 * @author Ronny Boelter
 * @version 1.0
 */
public enum SPSSMeasLevel {

    SPSS_MLVL_UNK(0), /* Unknown */
    SPSS_MLVL_NOM(1), /* Nominal */
    SPSS_MLVL_ORD(2), /* Ordinal */
    SPSS_MLVL_RAT(3), /* Scale (Ratio) (Continues) */
    SPSS_MLVL_FLA(4), /* Flag */
    SPSS_MLVL_TPL(5); /* Typeless */

    private final int number;

    private SPSSMeasLevel(int s) {
        number = s;
    }

    public boolean equalsName(int number) {
        return this.number == number;
    }

    public int getNumber() {
        return this.number;
    }

    private static final Map<Integer, SPSSMeasLevel> intToTypeMap = new HashMap<Integer, SPSSMeasLevel>();

    static {
        for (SPSSMeasLevel type : SPSSMeasLevel.values()) {
            intToTypeMap.put(type.number, type);
        }
    }

    public static SPSSMeasLevel fromInt(int i) {
        SPSSMeasLevel type = intToTypeMap.get(Integer.valueOf(i));
        if (type == null)
            return SPSSMeasLevel.SPSS_MLVL_UNK;
        return type;
    }

}
