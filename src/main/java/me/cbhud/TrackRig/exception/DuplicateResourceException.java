package me.cbhud.TrackRig.exception;

/**
 * Thrown when a resource violates a UNIQUE constraint (e.g. duplicate email,
 * duplicate serial number).
 * Handled by GlobalExceptionHandler → returns 409 Conflict.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
