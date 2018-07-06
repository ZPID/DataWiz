package de.zpid.datawiz.enumeration;

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
public enum ExportStates {

    PROJECT_EXPORT_FULL,
    PROJECT_EXPORT_METADATA,
    PROJECT_EXPORT_DMP,
    PROJECT_EXPORT_MATERIAL,
    STUDY_EXPORT_FULL,
    STUDY_EXPORT_METADATA,
    STUDY_EXPORT_MATERIAL,
    RECORD_EXPORT_FULL,
    RECORD_EXPORT_METADATA,
    RECORD_EXPORT_CODEBOOK,
    RECORD_EXPORT_MATRIX;

}
