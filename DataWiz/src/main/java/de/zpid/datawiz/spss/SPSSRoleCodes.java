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
 * Enumeration for SPSS role type
 *
 * @author Ronny Boelter
 * @version 1.0
 */
public enum SPSSRoleCodes {

    SPSS_ROLE_INPUT(0), /* Input Role */
    SPSS_ROLE_TARGET(1), /* Target Role */
    SPSS_ROLE_BOTH(2), /* Both Roles */
    SPSS_ROLE_NONE(3), /* None Role */
    SPSS_ROLE_PARTITION(4), /* Partition Role */
    SPSS_ROLE_SPLIT(5), /* Split Role */
    SPSS_ROLE_FREQUENCY(6), /* Frequency Role */
    SPSS_ROLE_RECORDID(7), /* Record ID */
    SPSS_UNKNOWN(-99);

    private final int number;

    private SPSSRoleCodes(int s) {
        number = s;
    }

    public boolean equalsName(int number) {
        return this.number == number;
    }

    public int getNumber() {
        return this.number;
    }

    private static final Map<Integer, SPSSRoleCodes> intToTypeMap = new HashMap<Integer, SPSSRoleCodes>();

    static {
        for (SPSSRoleCodes type : SPSSRoleCodes.values()) {
            intToTypeMap.put(type.number, type);
        }
    }

    public static SPSSRoleCodes fromInt(int i) {
        SPSSRoleCodes type = intToTypeMap.get(Integer.valueOf(i));
        if (type == null)
            return SPSSRoleCodes.SPSS_UNKNOWN;
        return type;
    }

}
