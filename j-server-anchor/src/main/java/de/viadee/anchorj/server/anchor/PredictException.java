package de.viadee.anchorj.server.anchor;

/**
 */
public class PredictException extends RuntimeException {
    private static final long serialVersionUID = -2761825202915608071L;

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
