package de.zpid.datawiz.spss;

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
 * Enumeration for SPSS variable type
 *
 * @author Ronny Boelter
 * @version 1.0
 */

import java.util.HashMap;
import java.util.Map;

public enum SPSSVarTypes {

    SPSS_FMT_A(1), /* Alphanumeric */
    SPSS_FMT_AHEX(2), /* Alphanumeric hexadecimal */
    SPSS_FMT_COMMA(3), /* F Format with commas */
    SPSS_FMT_DOLLAR(4), /* Commas and floating dollar sign */
    SPSS_FMT_F(5), /* Default Numeric Format */
    SPSS_FMT_IB(6), /* Integer binary */
    SPSS_FMT_PIBHEX(7), /* Positive integer binary - hex */
    SPSS_FMT_P(8), /* Packed decimal */
    SPSS_FMT_PIB(9), /* Positive integer binary unsigned */
    SPSS_FMT_PK(10), /* Positive integer binary unsigned */
    SPSS_FMT_RB(11), /* Floating point binary */
    SPSS_FMT_RBHEX(12), /* Floating point binary hex */
    SPSS_FMT_Z(15), /* Zoned decimal */
    SPSS_FMT_N(16), /* N Format- unsigned with leading 0s */
    SPSS_FMT_E(17), /* E Format- with explicit power of (10), */
    SPSS_FMT_DATE(20), /* Date format dd-mmm-yyyy */
    SPSS_FMT_TIME(21), /* Time format hh:mm:ss.s */
    SPSS_FMT_DATE_TIME(22), /* Date and Time */
    SPSS_FMT_ADATE(23), /* Date format mm/dd/yyyy */
    SPSS_FMT_JDATE(24), /* Julian date - yyyyddd */
    SPSS_FMT_DTIME(25), /* Date-time dd hh:mm:ss.s */
    SPSS_FMT_WKDAY(26), /* Day of the week */
    SPSS_FMT_MONTH(27), /* Month */
    SPSS_FMT_MOYR(28), /* mmm yyyy */
    SPSS_FMT_QYR(29), /* q Q yyyy */
    SPSS_FMT_WKYR(30), /* ww WK yyyy */
    SPSS_FMT_PCT(31), /* Percent - F followed by % */
    SPSS_FMT_DOT(32), /* Like COMMA, switching dot for comma */
    SPSS_FMT_CCA(33), /* User Programmable currency format */
    SPSS_FMT_CCB(34), /* User Programmable currency format */
    SPSS_FMT_CCC(35), /* User Programmable currency format */
    SPSS_FMT_CCD(36), /* User Programmable currency format */
    SPSS_FMT_CCE(37), /* User Programmable currency format */
    SPSS_FMT_EDATE(38), /* Date in dd.mm.yyyy style */
    SPSS_FMT_SDATE(39), /* Date in yyyy/mm/dd style */
    SPSS_FMT_MTIME(85), /* Time format mm:ss.ss */
    SPSS_FMT_YMDHMS(86), /* Data format yyyy-mm-dd hh:mm:ss.ss */
    SPSS_UNKNOWN(-99);

    private final int number;

    private SPSSVarTypes(int s) {
        number = s;
    }

    public boolean equalsName(int number) {
        return this.number == number;
    }

    public int getNumber() {
        return this.number;
    }

    private static final Map<Integer, SPSSVarTypes> intToTypeMap = new HashMap<Integer, SPSSVarTypes>();

    static {
        for (SPSSVarTypes type : SPSSVarTypes.values()) {
            intToTypeMap.put(type.number, type);
        }
    }

    public static SPSSVarTypes fromInt(int i) {
        SPSSVarTypes type = intToTypeMap.get(Integer.valueOf(i));
        if (type == null)
            return SPSSVarTypes.SPSS_UNKNOWN;
        return type;
    }

}
