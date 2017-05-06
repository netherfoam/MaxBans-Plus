package org.maxgamer.maxbans.exception;

/**
 * Runtime exception raised whenever there's trouble processing an exception. This always wraps another exception.
 *
 * @author netherfoam
 */
public class TransactionException extends RuntimeException {
    public TransactionException(Throwable throwable) {
        super(throwable);
    }
}
