package ua.sumdu.traveler.exception;

import lombok.Getter;

@Getter
public class VersionConflictException extends RuntimeException {
    private final Integer currentVersion;
    public VersionConflictException(String message, Integer currentVersion) {
        super(message);
        this.currentVersion = currentVersion;
    }
}