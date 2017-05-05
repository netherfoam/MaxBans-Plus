package org.maxgamer.maxbans.exception;

/**
 * @author netherfoam
 */
public class PermissionException extends MessageException {
    public PermissionException(String message) {
        super(message);
    }

    @Override
    public PermissionException with(String key, Object value) {
        super.with(key, value);

        return this;
    }
}
