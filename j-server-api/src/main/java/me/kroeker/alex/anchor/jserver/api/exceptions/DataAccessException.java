package me.kroeker.alex.anchor.jserver.api.exceptions;

import java.io.IOException;

/**
 */
public class DataAccessException extends IOException {
    private static final long serialVersionUID = -5778122139750179922L;

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
