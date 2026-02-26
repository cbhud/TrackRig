package me.cbhud.TrackRig.exception;

/**
 * Thrown when an entity is not found by its ID.
 * Handled by GlobalExceptionHandler → returns 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
