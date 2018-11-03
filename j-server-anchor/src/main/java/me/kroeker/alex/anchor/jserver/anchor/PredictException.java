package me.kroeker.alex.anchor.jserver.anchor;

/**
 * @author ak902764
 */
public class PredictException extends RuntimeException {
    public PredictException() {
        super();
    }

    public PredictException(String message) {
        super(message);
    }

    public PredictException(String message, Throwable cause) {
        super(message, cause);
    }

    public PredictException(Throwable cause) {
        super(cause);
    }

    protected PredictException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
