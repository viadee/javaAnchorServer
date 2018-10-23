package me.kroeker.alex.anchor.jserver.dao.exceptions;

import java.io.IOException;

/**
 * @author ak902764
 */
public class DataAccessException extends IOException {
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
