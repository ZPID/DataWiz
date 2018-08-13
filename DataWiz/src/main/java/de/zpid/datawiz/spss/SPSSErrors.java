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
 * Enumeration for SPSS errors
 *
 * @author Ronny Boelter
 * @version 1.0
 */
public enum SPSSErrors {

    SPSS_OK(0),
    SPSS_FILE_OERROR(1),
    SPSS_FILE_WERROR(2),
    SPSS_FILE_RERROR(3),
    SPSS_FITAB_FULL(4),
    SPSS_INVALID_HANDLE(5),
    SPSS_INVALID_FILE(6),
    SPSS_NO_MEMORY(7),
    SPSS_OPEN_RDMODE(8),
    SPSS_OPEN_WRMODE(9),
    SPSS_INVALID_VARNAME(10),
    SPSS_DICT_EMPTY(11),
    SPSS_VAR_NOTFOUND(12),
    SPSS_DUP_VAR(13),
    SPSS_NUME_EXP(14),
    SPSS_STR_EXP(15),
    SPSS_SHORTSTR_EXP(16),
    SPSS_INVALID_VARTYPE(17),
    SPSS_INVALID_MISSFOR(18),
    SPSS_INVALID_COMPSW(19),
    SPSS_INVALID_PRFOR(20),
    SPSS_INVALID_WRFOR(21),
    SPSS_INVALID_DATE(22),
    SPSS_INVALID_TIME(23),
    SPSS_NO_VARIABLES(24),
    SPSS_MIXED_TYPES(25),
    SPSS_DUP_VALUE(27),
    SPSS_INVALID_CASEWGT(28),
    SPSS_INCOMPATIBLE_DICT(29),
    SPSS_DICT_COMMIT(30),
    SPSS_DICT_NOTCOMMIT(31),
    SPSS_NO_TYPE2(33),
    SPSS_NO_TYPE73(41),
    SPSS_INVALID_DATEINFO(45),
    SPSS_NO_TYPE999(46),
    SPSS_EXC_STRVALUE(47),
    SPSS_CANNOT_FREE(48),
    SPSS_BUFFER_SHORT(49),
    SPSS_INVALID_CASE(50),
    SPSS_INTERNAL_VLABS(51),
    SPSS_INCOMPAT_APPEND(52),
    SPSS_INTERNAL_D_A(53),
    SPSS_FILE_BADTEMP(54),
    SPSS_DEW_NOFIRST(55),
    SPSS_INVALID_MEASURELEVEL(56),
    SPSS_INVALID_7SUBTYPE(57),
    SPSS_INVALID_VARHANDLE(58),
    SPSS_INVALID_ENCODING(59),
    SPSS_FILES_OPEN(60),
    SPSS_INVALID_MRSETDEF(70),
    SPSS_INVALID_MRSETNAME(71),
    SPSS_DUP_MRSETNAME(72),
    SPSS_BAD_EXTENSION(73),
    SPSS_INVALID_EXTENDEDSTRING(74),
    SPSS_INVALID_ATTRNAME(75),
    SPSS_INVALID_ATTRDEF(76),
    SPSS_INVALID_MRSETINDEX(77),
    SPSS_INVALID_VARSETDEF(78),
    SPSS_INVALID_ROLE(79),
    SPSS_INVALID_PASSWORD(80),
    SPSS_EMPTY_PASSWORD(81),
    SPSS_EXC_LEN64(-1),
    SPSS_EXC_LEN120(-2), /* Retain for compatibility */
    SPSS_EXC_VARLABEL(-2),
    SPSS_EXC_LEN60(-4),
    SPSS_EXC_VALLABEL(-4),
    SPSS_FILE_END(-5),
    SPSS_NO_VARSETS(-6),
    SPSS_EMPTY_VARSETS(-7),
    SPSS_NO_LABELS(-8),
    SPSS_NO_LABEL(-9),
    SPSS_NO_CASEWGT(-10),
    SPSS_NO_DATEINFO(-11),
    SPSS_NO_MULTRESP(-12),
    SPSS_EMPTY_MULTRESP(-13),
    SPSS_NO_DEW(-14),
    SPSS_EMPTY_DEW(-15),
    SPSS_UNKNOWN(-99);
    private final int number;

    private SPSSErrors(int s) {
        number = s;
    }

    public boolean equalsName(int number) {
        return this.number == number;
    }

    public int getNumber() {
        return this.number;
    }

    private static final Map<Integer, SPSSErrors> intToTypeMap = new HashMap<Integer, SPSSErrors>();

    static {
        for (SPSSErrors type : SPSSErrors.values()) {
            intToTypeMap.put(type.number, type);
        }
    }

    public static SPSSErrors fromInt(int i) {
        SPSSErrors type = intToTypeMap.get(Integer.valueOf(i));
        if (type == null)
            return SPSSErrors.SPSS_UNKNOWN;
        return type;
    }

}
