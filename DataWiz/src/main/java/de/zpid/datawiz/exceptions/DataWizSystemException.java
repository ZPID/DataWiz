package de.zpid.datawiz.exceptions;

import de.zpid.datawiz.enumeration.DataWizErrorCodes;

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
public class DataWizSystemException extends Exception {

    private static final long serialVersionUID = 8028749143659397286L;
    private DataWizErrorCodes error;

    public DataWizSystemException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public DataWizSystemException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    public DataWizSystemException(String message, DataWizErrorCodes error, Throwable cause) {
        super(message, cause);
        this.error = error;
        // TODO Auto-generated constructor stub
    }

    public DataWizSystemException(String message, DataWizErrorCodes error) {
        super(message);
        this.error = error;
        // TODO Auto-generated constructor stub
    }

    public DataWizSystemException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public DataWizErrorCodes getErrorCode() {
        return error;
    }

}
