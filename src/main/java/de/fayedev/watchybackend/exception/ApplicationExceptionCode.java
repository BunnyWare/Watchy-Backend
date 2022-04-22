package de.fayedev.watchybackend.exception;

import lombok.Getter;

@Getter
public enum ApplicationExceptionCode {
    UNHANDLED_EXCEPTION(0),
    JWT_INVALID_OR_EXPIRED(100),
    USER_AUTH_INVALID(101),
    USER_NOT_FOUND(102),
    USER_NOT_FOUND_TOKEN(103),
    METHOD_NOT_ALLOWED(104),
    BAD_REQUEST(105),
    FORBIDDEN(106),
    USER_ALREADY_CONFIRMED(107),
    USER_MAILING_DISABLED(108),
    USER_ALREADY_EXISTS(109),
    USER_LOGGED_IN_FAILED_EMAIL(110),
    USER_NOT_FOUND_EMAIL(111),
    TMDB_FAILED(112);

    private final int code;

    ApplicationExceptionCode(int code) {
        this.code = code;
    }
}
