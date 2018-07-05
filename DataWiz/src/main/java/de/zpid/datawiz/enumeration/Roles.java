package de.zpid.datawiz.enumeration;

import java.util.HashMap;
import java.util.Map;

/**
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
 **/
public enum Roles {
    REL_ROLE(0),
    USER(1),
    ADMIN(2),
    PROJECT_ADMIN(3),
    PROJECT_READER(4),
    PROJECT_WRITER(5),
    DS_READER(6),
    DS_WRITER(7);

    private final int code;

    private Roles(int code) {
        this.code = code;
    }

    public int toInt() {
        return code;
    }

    public String toString() {
        return String.valueOf(code);
    }

    private static final Map<Integer, Roles> intToTypeMap = new HashMap<Integer, Roles>();

    static {
        for (Roles type : Roles.values()) {
            intToTypeMap.put(type.code, type);
        }
    }

    public static Roles fromInt(int i) {
        Roles type = intToTypeMap.get(i);
        if (type == null)
            return null;
        return type;
    }
}
