package de.viadee.anchorj.server.api.exceptions;

import java.io.IOException;

/**
 */
public class DataAccessException extends IOException {
    private static final long serialVersionUID = 8484184910399872974L;

    public DataAccessException() {
        super();
    }

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(Throwable cause) {
        super(cause);
    }
}
