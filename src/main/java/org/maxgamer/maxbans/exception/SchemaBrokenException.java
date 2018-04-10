package org.maxgamer.maxbans.exception;

import org.flywaydb.core.api.FlywayException;

/**
 * Exception raised whenever the database is validated by MaxBans and fails for some reason
 */
public class SchemaBrokenException extends FlywayException {
    private String helpUrl;

    public SchemaBrokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public SchemaBrokenException(Throwable cause) {
        super(cause);
    }

    public SchemaBrokenException(String message) {
        super(message);
    }

    public SchemaBrokenException() {
    }

    public SchemaBrokenException withHelpUrl(String helpUrl) {
        this.helpUrl = helpUrl;
        return this;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (helpUrl != null && !helpUrl.isEmpty()) {
            message += "\nHelp: " + helpUrl;
        }

        return message;
    }
}
