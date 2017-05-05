package org.maxgamer.maxbans.exception;

/**
 * @author Dirk Jamieson
 */
public class RejectedException extends MessageException {
    public RejectedException(String message) {
        super(message);
    }

    @Override
    public RejectedException with(String key, Object value) {
        super.with(key, value);

        return this;
    }
}
