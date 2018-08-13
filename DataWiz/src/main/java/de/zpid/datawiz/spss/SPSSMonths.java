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
 * Enumeration for month of the year
 *
 * @author Ronny Boelter
 * @version 1.0
 */
public enum SPSSMonths {

    JANUARY(1.0),
    FEBRUARY(2.0),
    MARCH(3.0),
    APRIL(4.0),
    MAY(5.0),
    JUNE(6.0),
    JULY(7.0),
    AUGUST(8.0),
    SEPTEMBER(9.0),
    OCTOBER(10.0),
    NOVEMBER(11.0),
    DECEMBER(12.0),
    SPSS_UNKNOWN(-99);

    private final double number;

    private SPSSMonths(double s) {
        number = s;
    }

    public boolean equalsName(double number) {
        return this.number == number;
    }

    public double getNumber() {
        return this.number;
    }

    private static final Map<Double, SPSSMonths> doubleToTypeMap = new HashMap<Double, SPSSMonths>();

    static {
        for (SPSSMonths type : SPSSMonths.values()) {
            doubleToTypeMap.put(type.number, type);
        }
    }

    public static SPSSMonths fromDouble(double i) {
        SPSSMonths type = doubleToTypeMap.get(Double.valueOf(i));
        if (type == null)
            return SPSSMonths.SPSS_UNKNOWN;
        return type;
    }

}
